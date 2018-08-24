package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isClient
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
    val active: () -> Boolean,
    override val name: String = "module_solar_mirror"
) : IModule {

    override lateinit var container: IModuleContainer

    companion object {
        // Only send heat every 20 ticks to reduce lag
        const val TICKS_BETWEEN_OPERATIONS = 20
    }

    var solarTowerPos: BlockPos? = null
    var solarTowerTilePos: BlockPos? = null
    val mirrorPos: BlockPos get() = pos + facingGetter().toBlockPos()

    // rendering data
    var angleX = 0f
    var angleY = 0f
    var deltaTime = System.currentTimeMillis()

    override fun update() {
        if (world.isClient) return
        if (!world.isDaytime || !active()) return

        if (container.shouldTick(TICKS_BETWEEN_OPERATIONS)) {
            solarTowerTilePos?.let { pos ->
                val mod = world.getModule<ModuleSolarTower>(pos)

                if (mod == null || !mod.active()) {
                    clearTargetTower()
                } else {
                    mod.applyHeat(Config.solarMirrorHeatProduction.toFloat() * TICKS_BETWEEN_OPERATIONS)
                }
            }
        }
    }

    fun setTargetTower(center: BlockPos, tilePos: BlockPos) {
        solarTowerPos = center
        solarTowerTilePos = tilePos
        container.sendUpdateToNearPlayers()
    }

    fun clearTargetTower() {
        solarTowerTilePos = null
        solarTowerPos = null
        container.sendUpdateToNearPlayers()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        solarTowerPos = if (nbt.hasKey("towerPos")) nbt.getBlockPos("towerPos") else null
        solarTowerTilePos = if (nbt.hasKey("towerTilePos")) nbt.getBlockPos("towerTilePos") else null
    }

    override fun serializeNBT(): NBTTagCompound {
        return newNbt {
            solarTowerPos?.let { add("towerPos", it) }
            solarTowerTilePos?.let { add("towerTilePos", it) }
        }
    }
}