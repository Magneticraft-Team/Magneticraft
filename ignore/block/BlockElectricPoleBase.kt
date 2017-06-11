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
import com.cout970.magneticraft.tileentity.electric.TileElectricPole
import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import com.cout970.magneticraft.util.vector.toAABBWith
import com.teamwizardry.librarianlib.common.base.block.BlockMod
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 05/07/2016.
 */
abstract class BlockElectricPoleBase(material: Material, name: String) : BlockMod(name, material), IManualConnectionHandler, ICapabilityProvider {

    val boundingBox by lazy {
        val size = 0.0625 * 3
        Vec3d(0.5 - size, 0.0, 0.5 - size) toAABBWith Vec3d(0.5 + size, 1.0, 0.5 + size)
    }

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?) = boundingBox

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun getMetaFromState(state: IBlockState): Int =
            state[ELECTRIC_POLE_PLACE].ordinal

    override fun getStateFromMeta(meta: Int): IBlockState? =
            defaultState.withProperty(ELECTRIC_POLE_PLACE, ElectricPoleStates.values()[meta])

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, ELECTRIC_POLE_PLACE)


    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)

        val yaw = if (placer.rotationYaw >= 180) {
            placer.rotationYaw - 360
        } else if (placer.rotationYaw <= -180) {
            placer.rotationYaw + 360
        } else {
            placer.rotationYaw
        }
        val a = 45
        val b = 45 / 2
        val dir =
                if (yaw < -a * 3 + b && yaw >= -a * 4 + b) {
                    ElectricPoleStates.NORTH_EAST
                } else if (yaw < -a * 2 + b && yaw >= -a * 3 + b) {
                    ElectricPoleStates.EAST
                } else if (yaw < -a + b && yaw >= -a * 2 + b) {
                    ElectricPoleStates.SOUTH_EAST
                } else if (yaw < 0 + b && yaw >= -a + b) {
                    ElectricPoleStates.SOUTH
                } else if (yaw < a + b && yaw >= 0 + b) {
                    ElectricPoleStates.SOUTH_WEST
                } else if (yaw < a * 2 + b && yaw >= a + b) {
                    ElectricPoleStates.WEST
                } else if (yaw < a * 3 + b && yaw >= a * 2 + b) {
                    ElectricPoleStates.NORTH_WEST
                } else if (yaw < a * 4 + b && yaw >= a * 3 + b) {
                    ElectricPoleStates.NORTH
                } else {
                    ElectricPoleStates.NORTH
                }

        worldIn.setBlockState(pos, defaultState.withProperty(ELECTRIC_POLE_PLACE, ElectricPoleStates.DOWN_4))
        worldIn.setBlockState(pos.offset(EnumFacing.UP, 1), defaultState.withProperty(ELECTRIC_POLE_PLACE, ElectricPoleStates.DOWN_3))
        worldIn.setBlockState(pos.offset(EnumFacing.UP, 2), defaultState.withProperty(ELECTRIC_POLE_PLACE, ElectricPoleStates.DOWN_2))
        worldIn.setBlockState(pos.offset(EnumFacing.UP, 3), defaultState.withProperty(ELECTRIC_POLE_PLACE, ElectricPoleStates.DOWN_1))
        worldIn.setBlockState(pos.offset(EnumFacing.UP, 4), defaultState.withProperty(ELECTRIC_POLE_PLACE, dir))
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)
        val place = state[ELECTRIC_POLE_PLACE]
        if (place == ElectricPoleStates.DOWN_1 || place == ElectricPoleStates.DOWN_2 || place == ElectricPoleStates.DOWN_3 || place == ElectricPoleStates.DOWN_4) {
            val newPos = getMainPos(state, pos)
            worldIn.setBlockToAir(newPos)
        } else {
            for (i in 1..4) {
                worldIn.setBlockToAir(pos.offset(EnumFacing.DOWN, i))
            }
        }
    }

    fun getMainPos(state: IBlockState, pos: BlockPos): BlockPos {
        return when (state[ELECTRIC_POLE_PLACE]) {
            ElectricPoleStates.DOWN_1 -> pos.offset(EnumFacing.UP, 1)
            ElectricPoleStates.DOWN_2 -> pos.offset(EnumFacing.UP, 2)
            ElectricPoleStates.DOWN_3 -> pos.offset(EnumFacing.UP, 3)
            ElectricPoleStates.DOWN_4 -> pos.offset(EnumFacing.UP, 4)
            else -> pos
        }
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return when (state[ELECTRIC_POLE_PLACE]) {
            ElectricPoleStates.DOWN_1 -> EnumBlockRenderType.INVISIBLE
            ElectricPoleStates.DOWN_2 -> EnumBlockRenderType.INVISIBLE
            ElectricPoleStates.DOWN_3 -> EnumBlockRenderType.INVISIBLE
            ElectricPoleStates.DOWN_4 -> EnumBlockRenderType.INVISIBLE
            else -> super.getRenderType(state)
        }
    }

    override fun connectWire(otherBlock: BlockPos, thisBlock: BlockPos, world: World, player: EntityPlayer, side: EnumFacing, stack: ItemStack): Boolean {
        val state = world.getBlockState(thisBlock)
        val mainPos = getMainPos(state, thisBlock)
        val tile = world.getTileEntity(mainPos)
        val other = world.getTileEntity(otherBlock) ?: return false
        val handler = ELECTRIC_NODE_HANDLER!!.fromTile(other) ?: return false
        if (tile is TileElectricPole) {
            return tile.connectWire(handler, side)
        }
        if (tile is TileElectricPoleAdapter) {
            return tile.connectWire(handler, side)
        }
        return false
    }

    override fun getBasePos(thisBlock: BlockPos?, world: World?, player: EntityPlayer?, side: EnumFacing?, stack: ItemStack?): BlockPos {
        val state = world!!.getBlockState(thisBlock)
        return getMainPos(state, thisBlock!!)
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
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

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?): T = this as T

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean = capability == MANUAL_CONNECTION_HANDLER

    enum class ElectricPoleStates(
            val offset: Vec3d,
            val offsetY: Int = 0
    ) : IStringSerializable {

        NORTH(Vec3d(1.0, 0.0, 0.0)),
        NORTH_EAST(Vec3d(0.707106, 0.0, 0.707106)),
        EAST(Vec3d(0.0, 0.0, 1.0)),
        SOUTH_EAST(Vec3d(-0.707106, 0.0, 0.707106)),
        SOUTH(Vec3d(-1.0, 0.0, 0.0)),
        SOUTH_WEST(Vec3d(-0.707106, 0.0, -0.707106)),
        WEST(Vec3d(0.0, 0.0, -1.0)),
        NORTH_WEST(Vec3d(0.707106, 0.0, -0.707106)),
        DOWN_1(Vec3d.ZERO, 1),
        DOWN_2(Vec3d.ZERO, 2),
        DOWN_3(Vec3d.ZERO, 3),
        DOWN_4(Vec3d.ZERO, 4);

        override fun getName() = name.toLowerCase()

        fun isMainBlock() = offsetY == 0
    }
}
