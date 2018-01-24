package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.getBlockPos
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.toBlockPos
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

class ModuleSolarMirror(
        val facingGetter: () -> EnumFacing,
        override val name: String = "module_solar_mirror"
) : IModule {

    override lateinit var container: IModuleContainer

    var solarTowerPos: BlockPos? = null
    val mirrorPos: BlockPos get() = pos + facingGetter().toBlockPos()

    // rendering data
    var angleX = 0f
    var angleY = 0f
    var deltaTime = System.currentTimeMillis()

    override fun deserializeNBT(nbt: NBTTagCompound) {
        if (nbt.hasKey("towerPos")) {
            solarTowerPos = nbt.getBlockPos("towerPos")
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        return newNbt {
            solarTowerPos?.let { add("towerPos", it) }
        }
    }

    fun setTargetTower(center: BlockPos) {
        solarTowerPos = center
        container.sendUpdateToNearPlayers()
    }
}