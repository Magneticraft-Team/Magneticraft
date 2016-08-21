package com.cout970.magneticraft.block.multiblock

import com.cout970.magneticraft.block.states.PROPERTY_DIRECTION
import com.cout970.magneticraft.multiblock.MultiblockContext
import com.cout970.magneticraft.multiblock.MultiblockManager
import com.cout970.magneticraft.multiblock.impl.MultiblockHydraulicPress
import com.cout970.magneticraft.tileentity.multiblock.TileHydraulicPress
import com.cout970.magneticraft.tileentity.multiblock.TileMultiblock
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.util.isServer
import com.cout970.magneticraft.util.sendMessage
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 17/08/2016.
 */
object BlockHydraulicPress : BlockMultiblock(Material.IRON, "hydraulic_press"), ITileEntityProvider {

    lateinit var PROPERTY_CENTER: PropertyBool
    lateinit var PROPERTY_ACTIVE: PropertyBool

    init {
        defaultState = defaultState.withProperty(PROPERTY_CENTER, false).withProperty(PROPERTY_ACTIVE, false)
        setLightOpacity(0)
    }

    override fun isOpaqueCube(state: IBlockState): Boolean = false
    override fun isFullCube(state: IBlockState): Boolean = false
    override fun isFullBlock(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer?): Boolean {
        return PROPERTY_CENTER[state] && !PROPERTY_ACTIVE[state] && super.canRenderInLayer(state, layer)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? = createTileEntity(worldIn, getStateFromMeta(meta))

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        if (PROPERTY_CENTER[state]) return TileHydraulicPress()
        return TileMultiblock()
    }

    override fun getMetaFromState(state: IBlockState): Int {
        var meta = 0
        if (PROPERTY_CENTER[state]) {
            meta = meta or 8
        }
        if (PROPERTY_ACTIVE[state]) {
            meta = meta or 4
        }
        val dir = PROPERTY_DIRECTION[state]
        meta = meta or ((dir.ordinal - 2) and 3)
        return meta
    }

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.
            withProperty(PROPERTY_CENTER, (meta and 8) != 0).
            withProperty(PROPERTY_ACTIVE, (meta and 4) != 0).
            withProperty(PROPERTY_DIRECTION, EnumFacing.getHorizontal(meta and 3))

    override fun createBlockState(): BlockStateContainer {
        PROPERTY_CENTER = PropertyBool.create("center")
        PROPERTY_ACTIVE = PropertyBool.create("active")
        return BlockStateContainer(this, PROPERTY_CENTER, PROPERTY_ACTIVE, PROPERTY_DIRECTION)
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack?) {
        worldIn.setBlockState(pos, defaultState
                .withProperty(PROPERTY_DIRECTION, placer.horizontalFacing.opposite)
                .withProperty(PROPERTY_CENTER, true)
                .withProperty(PROPERTY_ACTIVE, false))
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isServer && hand == EnumHand.MAIN_HAND && PROPERTY_CENTER[state] && !PROPERTY_ACTIVE[state]) {
            val context = MultiblockContext(MultiblockHydraulicPress, worldIn, pos, PROPERTY_DIRECTION[state], playerIn)
            val errors = MultiblockManager.checkMultiblockStructure(context)
            if (errors.isNotEmpty()) {
                playerIn.sendMessage("text.magneticraft.multiblock.error_count", errors.size)
                if (errors.size > 2) {
                    playerIn.sendMessage("text.magneticraft.multiblock.first_errors", 2)
                    errors.stream().limit(2).forEach {
                        playerIn.addChatComponentMessage(it)
                    }
                } else {
                    playerIn.sendMessage("text.magneticraft.multiblock.all_errors")
                    errors.forEach {
                        playerIn.addChatComponentMessage(it)
                    }
                }
            } else {
                MultiblockManager.activateMultiblockStructure(context)
                playerIn.sendMessage("text.magneticraft.multiblock.activate")
            }
        }
        return true
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (PROPERTY_ACTIVE[state]) {
            if (PROPERTY_CENTER[state]) {
                MultiblockManager.deactivateMultiblockStructure(MultiblockContext(MultiblockHydraulicPress, worldIn, pos, PROPERTY_DIRECTION[state], null))
            } else {
                super.breakBlock(worldIn, pos, state)
            }
        }
    }

    override fun getCustomStateMapper(): IStateMapper = StateMap.Builder().ignore(PROPERTY_ACTIVE, PROPERTY_CENTER).build()
}