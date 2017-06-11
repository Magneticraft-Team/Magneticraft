@file:Suppress("DEPRECATION", "OverridingDeprecatedMember")

package com.cout970.magneticraft.block

import com.cout970.magneticraft.api.energy.IManualConnectionHandler
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.MANUAL_CONNECTION_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import com.cout970.magneticraft.tilerenderer.PIXEL
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.times
import com.cout970.magneticraft.util.vector.toAABBWith
import com.cout970.magneticraft.util.vector.toVec3d
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 29/06/2016.
 */
object BlockElectricConnector : BlockMultiState(Material.IRON,
        "electric_connector"), ITileEntityProvider, IManualConnectionHandler, ICapabilityProvider {

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState?, playerIn: EntityPlayer,
                                  hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float,
                                  hitZ: Float): Boolean {
        if (playerIn.isSneaking && playerIn.heldItemMainhand == null) {
            val te = worldIn.getTile<TileBase>(pos)
            if (te != null) {
                val trait = te.traits.find { it is TraitElectricity }
                if (trait is TraitElectricity) {
                    trait.autoConnectWires = !trait.autoConnectWires
                    if (!trait.autoConnectWires) {
                        trait.clearWireConnections()
                    }
                    if (worldIn.isServer) {
                        if (trait.autoConnectWires) {
                            playerIn.addChatComponentMessage(
                                    TextComponentTranslation("text.magneticraft.auto_connect.activate"))
                        } else {
                            playerIn.addChatComponentMessage(
                                    TextComponentTranslation("text.magneticraft.auto_connect.deactivate"))
                        }
                    }
                    return true
                }

            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess?, pos: BlockPos?): AxisAlignedBB {
        val facing = state[PROPERTY_FACING]!!
        return when (facing) {
            EnumFacing.DOWN -> {
                Vec3d(PIXEL * 5, 0.0, PIXEL * 5) toAABBWith Vec3d(1.0 - PIXEL * 5, PIXEL * 5, 1.0 - PIXEL * 5)
            }
            EnumFacing.UP -> {
                Vec3d(PIXEL * 5, 1.0 - PIXEL * 5, PIXEL * 5) toAABBWith Vec3d(1.0 - PIXEL * 5, 1.0, 1.0 - PIXEL * 5)
            }
            EnumFacing.NORTH -> {
                Vec3d(PIXEL * 5, PIXEL * 5, 0.0) toAABBWith Vec3d(1.0 - PIXEL * 5, 1.0 - PIXEL * 5, PIXEL * 5)
            }
            EnumFacing.SOUTH -> {
                Vec3d(PIXEL * 5, PIXEL * 5, 1.0 - PIXEL * 5) toAABBWith Vec3d(1.0 - PIXEL * 5, 1.0 - PIXEL * 5, 1.0)
            }
            EnumFacing.WEST -> {
                Vec3d(0.0, PIXEL * 5, PIXEL * 5) toAABBWith Vec3d(PIXEL * 5, 1.0 - PIXEL * 5, 1.0 - PIXEL * 5)
            }
            EnumFacing.EAST -> {
                Vec3d(1 - PIXEL * 5, PIXEL * 5, PIXEL * 5) toAABBWith Vec3d(1.0, 1.0 - PIXEL * 5, 1.0 - PIXEL * 5)
            }
        }
    }

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileElectricConnector()

    override fun onBlockPlaced(worldIn: World?, pos: BlockPos?, facing: EnumFacing, hitX: Float, hitY: Float,
                               hitZ: Float, meta: Int, placer: EntityLivingBase?): IBlockState {
        return defaultState.withProperty(PROPERTY_FACING, facing.opposite)
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType = EnumBlockRenderType.INVISIBLE

    override fun getMetaFromState(state: IBlockState): Int = state[PROPERTY_FACING].ordinal

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.withProperty(PROPERTY_FACING,
            EnumFacing.getFront(meta))

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, PROPERTY_FACING)

    override fun getBasePos(thisBlock: BlockPos?, world: World?, player: EntityPlayer?, side: EnumFacing?,
                            stack: ItemStack?): BlockPos? = thisBlock

    override fun connectWire(otherBlock: BlockPos, thisBlock: BlockPos, world: World, player: EntityPlayer,
                             side: EnumFacing, stack: ItemStack): Boolean {
        val tile = world.getTile<TileElectricConnector>(thisBlock)
        val other = world.getTileEntity(otherBlock)
        if (tile == null || other == null) {
            return false
        }
        val handler = ELECTRIC_NODE_HANDLER!!.fromTile(other) ?: return false
        return tile.connectWire(handler, side)
    }

    override fun canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
        return super.canPlaceBlockOnSide(worldIn, pos, side) && canStayInSide(worldIn, pos, side.opposite)
    }

    //pos, block to place the connector
    //side PROPERTY_FACING in the connector
    fun canStayInSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
        if (worldIn.isSideSolid(pos.offset(side), side.opposite, false)) return true

        var box = Vec3d(0.5 - PIXEL, 0.5 - PIXEL, 0.5 - PIXEL) toAABBWith Vec3d(0.5 + PIXEL, 0.5 + PIXEL, 0.5 + PIXEL)
        val temp = side.directionVec.toVec3d() * 0.625 + Vec3d(0.5, 0.5, 0.5)
        val blockPos = pos.offset(side)

        box = box.union(temp toAABBWith temp).offset(pos)
        val state = worldIn.getBlockState(blockPos)
        val list = mutableListOf<AxisAlignedBB>()

        state.addCollisionBoxToList(worldIn, blockPos, box, list, null)
        return list.isNotEmpty()
    }

    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, blockIn: Block?) {
        val dir = state[PROPERTY_FACING]
        if (!canStayInSide(world, pos, dir)) {
            world.destroyBlock(pos, true)
        }
        super.neighborChanged(state, world, pos, blockIn)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?): T = this as T

    override fun hasCapability(capability: Capability<*>?,
                               facing: EnumFacing?): Boolean = capability == MANUAL_CONNECTION_HANDLER
}