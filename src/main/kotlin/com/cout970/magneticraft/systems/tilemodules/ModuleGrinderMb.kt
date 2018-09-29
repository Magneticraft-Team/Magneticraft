package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.misc.vector.rotatePoint
import com.cout970.magneticraft.misc.vector.xi
import com.cout970.magneticraft.misc.vector.yi
import com.cout970.magneticraft.misc.vector.zi
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.systems.blocks.IOnActivated
import com.cout970.magneticraft.systems.blocks.OnActivatedArgs
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

class ModuleGrinderMb(
    val facingGetter: () -> EnumFacing,
    val energyModule: () -> ModuleElectricity,
    override val name: String = "module_grinder_mb"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer
    inline val facing get() = facingGetter()


    fun getCapability(cap: Capability<*>, side: EnumFacing?, relPos: BlockPos): Any? {
        if (cap != ELECTRIC_NODE_HANDLER) return null
        if (side != facing.rotateY()) return null
        val connectorPos = facing.rotatePoint(BlockPos.ORIGIN, BlockPos(1, 1, -1))
        if (relPos != connectorPos) return null

        return energyModule()
    }


    fun getConnectableDirections(): List<Pair<BlockPos, EnumFacing>> {
        return if (facing.rotateY().axisDirection == EnumFacing.AxisDirection.NEGATIVE) {
            val pos = facing.rotatePoint(BlockPos.ORIGIN, BlockPos(2, 1, -1))
            listOf(pos to getConnectionSide())
        } else emptyList()
    }

    private fun getConnectionSide() = facing.rotateY()

    fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == getConnectionSide() || facing == getConnectionSide().opposite
    }

    override fun onActivated(args: OnActivatedArgs): Boolean {
        if (!args.playerIn.isSneaking) {
            if (args.worldIn.isServer) {
                args.playerIn.openGui(Magneticraft, -1, args.worldIn, pos.xi, pos.yi, pos.zi)
            }
            return true
        } else {
            return false
        }
    }
}