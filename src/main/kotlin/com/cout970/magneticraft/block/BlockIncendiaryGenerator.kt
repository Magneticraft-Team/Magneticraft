package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.tileentity.electric.TileIncendiaryGenerator
import com.cout970.magneticraft.util.get
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 04/07/2016.
 */

val PROPERTY_LOCATION: PropertyEnum<BlockIncendiaryGenerator.Location> = PropertyEnum.create("location", BlockIncendiaryGenerator.Location::class.java)!!
val PROPERTY_DIRECTION: PropertyDirection = PropertyDirection.create("direction", listOf(*EnumFacing.HORIZONTALS))!!

object BlockIncendiaryGenerator : BlockState(Material.IRON, "incendiary_generator"), ITileEntityProvider {

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun getRenderType(state: IBlockState): EnumBlockRenderType = EnumBlockRenderType.INVISIBLE

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        if (PROPERTY_LOCATION[getStateFromMeta(meta)] == Location.TOP) {
            return TileIncendiaryGenerator()
        }
        return null
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return PROPERTY_LOCATION[state] == Location.TOP
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)
        if (PROPERTY_LOCATION[state] == Location.TOP) {
            worldIn.setBlockToAir(pos.add(0, -1, 0))
        } else {
            worldIn.setBlockToAir(pos.add(0, 1, 0))
        }
    }

    override fun onBlockActivated(worldIn: World, position: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) return true
        val location = PROPERTY_LOCATION[state]
        var pos = position
        if (location == Location.BASE) {
            pos = pos.add(0, 1, 0)
        }
        val item = playerIn.getHeldItem(hand)
        if (item != null) {
            if (item.hasCapability(FLUID_HANDLER, null)) {
                val cap = item.getCapability(FLUID_HANDLER, null)
                val tile = worldIn.getTile<TileIncendiaryGenerator>(pos)
                if (tile != null) {
                    val drained = cap.drain(FluidStack(FluidRegistry.WATER, 1000), false)
                    val accepted = tile.tank.fill(drained, true)
                    if (accepted > 0 && !playerIn.capabilities.isCreativeMode) {
                        cap.drain(FluidStack(FluidRegistry.WATER, accepted), true)
                    }
                    return true
                }
            }
        }
        playerIn.openGui(Magneticraft, -1, worldIn, pos.x, pos.y, pos.z)

        return true
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack?) {
        worldIn.setBlockState(pos, defaultState.withProperty(PROPERTY_LOCATION, Location.BASE))
        worldIn.setBlockState(pos.add(0, 1, 0), defaultState.withProperty(PROPERTY_LOCATION, Location.TOP).withProperty(PROPERTY_DIRECTION, placer.horizontalFacing.opposite))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        val location = PROPERTY_LOCATION[state]
        if (location == Location.BASE) return 0
        return PROPERTY_DIRECTION[state].horizontalIndex + 1
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        if (meta == 0) return defaultState.withProperty(PROPERTY_LOCATION, Location.BASE)
        return defaultState.withProperty(PROPERTY_LOCATION, Location.TOP).withProperty(PROPERTY_DIRECTION, EnumFacing.getHorizontal(meta - 1))
    }

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, PROPERTY_LOCATION, PROPERTY_DIRECTION)

    enum class Location : IStringSerializable {
        BASE, TOP;

        override fun getName(): String = name.toLowerCase()
    }
}