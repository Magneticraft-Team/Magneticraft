package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.multiblock.core.IMultiblockCenter
import com.cout970.magneticraft.multiblock.core.Multiblock
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

class ModuleMultiblockCenter(
        val multiblockStructure: Multiblock,
        val facingGetter: () -> EnumFacing,
        val filter: (cap: Capability<*>, side: EnumFacing?, pos: BlockPos) -> Boolean = { cap, side, pos -> false },
        override val name: String = "module_multiblock"
) : IModule, IMultiblockCenter {

    lateinit override var container: IModuleContainer

    //current multiblock
    override var multiblock: Multiblock? = multiblockStructure
        set(value) {}
    //relative position from the multiblock center to this block
    override var centerPos: BlockPos? = BlockPos.ORIGIN
        set(value) {}
    //orientation of the multiblock
    override var multiblockFacing: EnumFacing?
        get() = facingGetter()
        set(value) {}

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean {
        return container.tile.hasCapability(capability, facing) && (filter?.invoke(capability, facing, relPos) ?: true)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?, relPos: BlockPos): T? {
        return container.tile.getCapability(capability, facing)
    }
}