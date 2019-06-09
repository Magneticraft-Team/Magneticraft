package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.systems.multiblocks.IMultiblockCenter
import com.cout970.magneticraft.systems.multiblocks.Multiblock
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

class ModuleMultiblockCenter(
    val multiblockStructure: Multiblock,
    val facingGetter: () -> EnumFacing,
    val capabilityGetter: (cap: Capability<*>, side: EnumFacing?, pos: BlockPos) -> Any?,
    val dynamicCollisionBoxes: (BlockPos) -> List<AABB> = { emptyList() },
    override val name: String = "module_multiblock"
) : IModule, IMultiblockCenter {

    override lateinit var container: IModuleContainer

    companion object {
        val emptyCapabilityGetter: (cap: Capability<*>, side: EnumFacing?, pos: BlockPos) -> Any? = { _, _, _ -> null }
    }

    //current multiblock
    override var multiblock: Multiblock? = multiblockStructure
        set(_) {}

    //relative position from the multiblock center to this block
    override var centerPos: BlockPos? = BlockPos.ORIGIN
        set(_) {}

    //orientation of the multiblock
    override var multiblockFacing: EnumFacing?
        get() = facingGetter()
        set(_) {}

    override fun onDeactivate() {
        container.tile.onBreak()
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean {
        return capabilityGetter.invoke(capability, facing, relPos) != null
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?, relPos: BlockPos): T? {
        return capabilityGetter.invoke(capability, facing, relPos) as? T?
    }

    override fun getDynamicCollisionBoxes(otherPos: BlockPos): List<AABB> = dynamicCollisionBoxes(otherPos)
}