package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.getBlockPos
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.vector.toBlockPos
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
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