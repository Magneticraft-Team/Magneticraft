package com.cout970.magneticraft.gui.client.core

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.CompDynamicBackground
import com.cout970.magneticraft.gui.client.components.CompImage
import com.cout970.magneticraft.gui.client.components.bars.*
import com.cout970.magneticraft.misc.crafting.TimedCraftingProcess
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.tileentity.modules.ModuleInternalStorage
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraftforge.energy.IEnergyStorage
import kotlin.math.max

fun GuiBase.dsl(func: GuiSdl.() -> Unit) {
    val dsl = GuiSdl()
    dsl.func()
    dsl.build(this)
}

class GuiSdl {

    private val bars = mutableListOf<GuiDslBars>()
    var marginY = 0
    var paddingX = 7

    fun bars(func: GuiDslBars.() -> Unit) {
        val dsl = GuiDslBars()
        dsl.func()
        bars += dsl
    }

    fun build(gui: GuiBase) {
        gui.components += CompBackground(guiTexture("base"))

        val sizeX = bars.map { it.getExternalSize() }.sumBy { it.xi } + (bars.size - 1) * paddingX
        var startX = (gui.sizeX - sizeX) / 2

        bars.forEach {
            val pos = vec2Of(startX, marginY)
            startX += it.getExternalSize().xi + paddingX
            it.build(gui, pos)
        }
    }
}

private val ICON_SIZE = vec2Of(0, 10)

enum class TankIO { IN, OUT, INOUT, NONE }

class GuiDslBars {
    private val components = mutableListOf<Pair<IVector2, (IVector2) -> List<IComponent>>>()
    var marginX = 6
    var marginY = 6

    var paddingX = 1

    fun electricBar(node: IElectricNode) {
        components += Vec2d(7, 60) to { pos -> listOf(CompElectricBar2(node, pos + ICON_SIZE), iconOf(1, pos)) }
    }

    fun storageBar(node: ModuleInternalStorage) {
        components += Vec2d(13, 60) to { pos -> listOf(CompStorageBar2(node, pos + ICON_SIZE), iconOf(0, pos + vec2Of(3, 0))) }
    }

    fun heatBar(node: IHeatNode) {
        components += Vec2d(7, 60) to { pos -> listOf(CompHeatBar2(node, pos + ICON_SIZE), iconOf(2, pos)) }
    }

    fun rfBar(node: IEnergyStorage) {
        components += Vec2d(7, 60) to { pos -> listOf(CompRfBar2(node, pos + ICON_SIZE), iconOf(8, pos)) }
    }

    fun genericBar(index: Int, icon: Int, prov: StaticBarProvider, tooltip: () -> List<String>) {
        components += Vec2d(7, 60) to { pos -> listOf(CompDynamicBar(prov, index, pos + ICON_SIZE, tooltip), iconOf(icon, pos)) }
    }

    fun electricConsumption(va: ValueAverage, limit: Number) {
        components += Vec2d(7, 60) to { pos -> listOf(CompDynamicBar(pos + ICON_SIZE, 3, va, limit) { it.toEnergyText() }, iconOf(5, pos)) }
    }

    fun electricProduction(va: ValueAverage, limit: Number) {
        components += Vec2d(7, 60) to { pos -> listOf(CompDynamicBar(pos + ICON_SIZE, 3, va, limit) { it.toEnergyText() }, iconOf(3, pos)) }
    }

    fun rfConsumption(va: ValueAverage, limit: Number) {
        components += Vec2d(7, 60) to { pos -> listOf(CompDynamicBar(pos + ICON_SIZE, 3, va, limit) { it.toIntText(postfix = " RF/t") }, iconOf(5, pos)) }
    }

    fun heatProduction(va: ValueAverage, limit: Number) {
        components += Vec2d(7, 60) to { pos -> listOf(CompDynamicBar(pos + ICON_SIZE, 6, va, limit) { it.toHeatPerTickText() }, iconOf(4, pos)) }
    }

    fun progressBar(timed: TimedCraftingProcess) {
        components += Vec2d(7, 60) to { pos -> listOf(CompDynamicBar(pos + ICON_SIZE, 6, timed) { it.toPercentText() }, iconOf(3, pos)) }
    }

    fun tank(tank: Tank, use: TankIO) {
        components += Vec2d(18, 50) to { pos ->
            val icons = when (use) {
                TankIO.IN -> listOf(iconOf(5, pos + vec2Of(6, 0)))
                TankIO.OUT -> listOf(iconOf(3, pos + vec2Of(6, 0)))
                TankIO.INOUT -> listOf(iconOf(5, pos + vec2Of(2, 0)), iconOf(3, pos + vec2Of(9, 0)))
                TankIO.NONE -> emptyList()
            }
            listOf(CompFluidBar2(pos + ICON_SIZE, tank)) + icons
        }
    }

    fun slotPair() {
        components += Vec2d(18, 50) to { pos ->
            listOf(CompImage(guiTexture("misc"), DrawableBox(
                    screenPos = pos + ICON_SIZE,
                    screenSize = vec2Of(18, 18),
                    texturePos = vec2Of(55, 81),
                    textureSize = vec2Of(18, 18),
                    textureScale = vec2Of(256)
            )), CompImage(guiTexture("misc"), DrawableBox(
                    screenPos = pos + vec2Of(0, 42),
                    screenSize = vec2Of(18, 18),
                    texturePos = vec2Of(36, 81),
                    textureSize = vec2Of(18, 18),
                    textureScale = vec2Of(256)
            )))
        }
    }

    private fun iconOf(index: Int, pos: IVector2): CompImage = CompImage(guiTexture("misc"), DrawableBox(
            screenPos = pos,
            screenSize = vec2Of(7, 8),
            texturePos = vec2Of(37 + (index % 8) * 8, 62 + (index / 8) * 9),
            textureSize = vec2Of(7, 8),
            textureScale = vec2Of(256)
    ))

    fun getInternalSize(): IVector2 {
        val sizeX = components.sumByDouble { it.first.x + paddingX * 2 }
        val sizeY = components.fold(0.0) { acc, it -> max(acc, it.first.y) }
        return vec2Of(sizeX, sizeY)
    }

    fun getExternalSize(): IVector2 {
        val size = getInternalSize()
        return vec2Of(size.x + marginX * 2, size.y + marginY * 2)
    }

    fun build(gui: GuiBase, pos: IVector2) {
        var startX = pos.xi + marginX
        val startY = pos.yi + marginY

        gui.components += CompDynamicBackground(pos, getExternalSize())

        components.forEach { (compSize, factory) ->
            val compPos = vec2Of(startX + paddingX, startY)
            startX += compSize.xi + paddingX * 2
            gui.components += factory(compPos)
        }
    }
}

