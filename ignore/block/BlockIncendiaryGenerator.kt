@file:Suppress("DEPRECATION", "OverridingDeprecatedMember")

package com.cout970.magneticraft.block


import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.block.itemblock.ItemBlockIncendiaryGenerator
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.tileentity.electric.TileIncendiaryGenerator
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
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
object BlockIncendiaryGenerator : BlockModContainer("incendiary_generator", Material.IRON) {

    override fun createItemForm(): ItemBlock? {
        return ItemBlockIncendiaryGenerator(this)
    }

    lateinit var PROPERTY_LOCATION: PropertyEnum<BlockIncendiaryGenerator.Location>
        private set
    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun getRenderType(state: IBlockState): EnumBlockRenderType = EnumBlockRenderType.INVISIBLE

    override fun createTileEntity(worldIn: World, meta: IBlockState): TileEntity? {
        if (meta[PROPERTY_LOCATION] == Location.TOP) {
            return TileIncendiaryGenerator()
        }
        return TileIncendiaryGenerator.TileIncendiaryGeneratorBottom()
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)
        if (state[PROPERTY_LOCATION] == Location.TOP) {
            worldIn.setBlockToAir(pos.add(0, -1, 0))
        } else {
            worldIn.setBlockToAir(pos.add(0, 1, 0))
        }
    }

    override fun onBlockActivated(worldIn: World, position: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isClient) return true
        if (playerIn.isSneaking) return false
        val location = state[PROPERTY_LOCATION]
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
                    if(drained != null) {
                        val accepted = tile.tank.fill(drained, true)
                        if (accepted > 0 && !playerIn.capabilities.isCreativeMode) {
                            cap.drain(FluidStack(FluidRegistry.WATER, accepted), true)
                        }
                        return true
                    }
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
        val location = state[PROPERTY_LOCATION]
        if (location == Location.BASE) return 0
        return state[PROPERTY_DIRECTION].horizontalIndex + 1
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        if (meta == 0) return defaultState.withProperty(PROPERTY_LOCATION, Location.BASE)
        return defaultState.withProperty(PROPERTY_LOCATION, Location.TOP).withProperty(PROPERTY_DIRECTION, EnumFacing.getHorizontal(meta - 1))
    }

    override fun createBlockState(): BlockStateContainer {
        PROPERTY_LOCATION = PropertyEnum.create("location", BlockIncendiaryGenerator.Location::class.java)!!
        return BlockStateContainer(this, PROPERTY_LOCATION, PROPERTY_DIRECTION)
    }

    enum class Location : IStringSerializable {
        BASE, TOP;

        override fun getName(): String = name.toLowerCase()
    }
}