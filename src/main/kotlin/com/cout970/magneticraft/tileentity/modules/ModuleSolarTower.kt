package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.gui.common.core.DATA_ID_MACHINE_HEAT
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.fromCelsiusToKelvin
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.rotatePoint
import com.cout970.magneticraft.util.vector.toBlockPos
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

class ModuleSolarTower(
        val node: IHeatNode,
        val facingGetter: () -> EnumFacing,
        override val name: String = "module_solar_tower"
) : IModule {

    override lateinit var container: IModuleContainer
    var searchMirrors = false
    val production = ValueAverage()

    override fun update() {
        if (world.isClient) return

        production.tick()

        if (searchMirrors) {
            searchMirrors = false
            orientateNearMirrors()
        }

        if (node.temperature > 4000f.fromCelsiusToKelvin()) {
            meltDown()
        }
    }

    fun meltDown() {
        val dir = facingGetter().opposite

        for (i in -1..1) {
            for (j in 0..2) {
                for (k in 0..2) {
                    if (i == 0 && j == 0 && k == 0) continue
                    val relPos = dir.rotatePoint(BlockPos.ORIGIN, BlockPos(i, j, k))
                    world.setBlockState(pos + relPos, Blocks.LAVA.defaultState)
                }
            }
        }

        world.setBlockState(pos, Blocks.LAVA.defaultState)
    }

    fun applyHeat(heat: Float) {
        production += heat
        node.applyHeat(heat.toDouble())
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

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(production.toSyncVariable(DATA_ID_MACHINE_HEAT))
    }
}