package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.toBlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

class ModuleSolarTower(
        val facingGetter: () -> EnumFacing,
        val steamBoilerModule: ModuleSteamBoiler,
        override val name: String = "module_solar_tower"
) : IModule {

    override lateinit var container: IModuleContainer
    var searchMirrors = false

    override fun update() {
        if(searchMirrors){
            searchMirrors = false
            orientateNearMirrors()
        }
    }

    fun applyHeat(heat: Float){
        steamBoilerModule.applyHeat(heat)
    }

    fun orientateNearMirrors() {
        val area = BlockPos(25, 20, 25)
        val center = pos + facingGetter().toBlockPos()

        for (x in -area.x..area.x) {
            for (y in 0..area.y) {
                for (z in -area.z..area.z) {
                    val otherPos = center + BlockPos(x, -y, z)
                    val mod = world.getModule<ModuleSolarMirror>(otherPos) ?: continue

                    mod.setTargetTower(center, pos)
                }
            }
        }
    }
}