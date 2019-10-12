package com.cout970.magneticraft.systems.gui

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.misc.crafting.TimedCraftingProcess
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.gui.*
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.components.*
import com.cout970.magneticraft.systems.gui.components.bars.*
import com.cout970.magneticraft.systems.gui.components.buttons.ClickButton
import com.cout970.magneticraft.systems.gui.components.buttons.SelectButton
import com.cout970.magneticraft.systems.gui.components.buttons.SwitchButton
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.tilemodules.ModuleBigCombustionChamber
import com.cout970.magneticraft.systems.tilemodules.ModuleCombustionChamber
import com.cout970.magneticraft.systems.tilemodules.ModuleInternalStorage
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.items.IItemHandler
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.max

enum class TankIO { IN, OUT, INOUT, NONE }

class GuiBuilder(val config: GuiConfig) {

    var containerConfig: (AutoContainer) -> Unit = {}
    var bars: DslBars? = null
    var comps: DslComponents? = null
    var containerClass: (GuiBuilder, (AutoContainer) -> Unit, EntityPlayer, World, BlockPos) -> AutoContainer = ::AutoContainer

    fun container(func: ContainerBuilder.() -> Unit) {
        containerConfig = { container ->
            ContainerBuilder(container, config).func()
        }
    }

    fun bars(func: DslBars.() -> Unit) {
        val dsl = DslBars(config)
        dsl.func()
        bars = dsl
    }

    fun components(func: DslComponents.() -> Unit) {
        val dsl = DslComponents(config)
        dsl.func()
        comps = dsl
    }

    fun build(gui: AutoGui, container: AutoContainer) {
        val bars = bars

        if (bars != null) {
            val sizeX = bars.getExternalSize().xi
            val startX = (gui.sizeX - sizeX) / 2

            bars.build(gui, container, vec2Of(startX, 0))
        }

        comps?.build(gui, container)
    }
}

class ContainerBuilder(val container: AutoContainer, val config: GuiConfig) {

    fun playerInventory(point: String? = null) {
        val pos = config.points[point] ?: Vec2d.ZERO
        container.bindPlayerInventory(container.player.inventory, pos)
    }

    fun slot(inventory: IItemHandler, index: Int, point: String, type: SlotType = SlotType.NORMAL, blockInput: Boolean = false) {
        val pos = config.points[point] ?: Vec2d.ZERO
        val slot = if (blockInput) {
            SlotTakeOnly(inventory, index, pos.xi, pos.yi)
        } else {
            TypedSlot(inventory, index, pos.xi, pos.yi, type)
        }
        container.addSlotToContainer(slot)
    }

    fun slotGroup(rows: Int, columns: Int, inventory: IItemHandler, startIndex: Int, point: String, type: SlotType = SlotType.NORMAL) {
        val pos = config.points[point] ?: Vec2d.ZERO

        repeat(rows) { row ->
            repeat(columns) { column ->
                val index = startIndex + column + row * columns

                val slot = if (type == SlotType.FILTER) {
                    SlotFilter(inventory, index, pos.xi + column * 18, pos.yi + row * 18)
                } else {
                    TypedSlot(inventory, index, pos.xi + column * 18, pos.yi + row * 18, type)
                }

                container.addSlotToContainer(slot)
            }
        }
    }

    fun slotButton(inventory: IItemHandler, index: Int, point: String, func: (EntityPlayer, Int) -> Unit) {
        val pos = config.points[point] ?: Vec2d.ZERO
        val slot = SlotButton(inventory, index, pos.xi, pos.yi, func)

        container.addSlotToContainer(slot)
    }

    fun region(
            first: Int,
            size: Int,
            inverseDirection: Boolean = false,
            filter: (ItemStack, Int) -> Boolean = { _, _ -> true }
    ) {
        container.inventoryRegions += InventoryRegion(first until first + size, inverseDirection, filter)
    }

    fun onClick(id: String, func: () -> Unit) {
        container.buttonListeners[id] = func
    }

    fun switchButtonState(id: String, func: () -> Boolean) {
        container.switchButtonCallbacks[id] = func
    }

    fun selectButtonState(id: String, func: () -> Int) {
        container.selectButtonCallbacks[id] = func
    }

    fun sendToServer(data: IBD) {
        container.sendUpdate(data)
    }

    fun receiveDataFromClient(func: (IBD) -> Unit) {
        container.receiveDataFromClientFunc = func
    }
}

private val ICON_SIZE = vec2Of(0, 10)
private const val ICON_HEIGHT = 10

class DslBars(val config: GuiConfig) {
    private val components = mutableListOf<Pair<IVector2, (IVector2) -> List<IComponent>>>()
    var marginX = 4
    var marginY = 4

    var paddingX = 1
    private var disableSpacing = false

    private fun component(size: IVector2, getter: (IVector2) -> List<IComponent>) {
        components += if (disableSpacing) {
            Vec2d.ZERO to getter
        } else {
            size to getter
        }
    }

    private fun barOf(texture: IVector2,
                      value: () -> Double, tooltip: () -> List<String> = { emptyList() }, icon: Int) {

        component(vec2Of(12, 54 + ICON_HEIGHT)) { pos ->
            listOf(
                    GuiValueBar(
                            pos = pos + ICON_SIZE,
                            size = vec2Of(12, 54),
                            colorTextureOffset = texture,
                            value = value,
                            tooltip = tooltip
                    ),
                    iconOf(icon, pos + vec2Of(2, 0))
            )
        }
    }

    private fun bar2Of(texture: IVector2, value: () -> Double, tooltip: () -> List<String> = { emptyList() },
                       icon: Int) {

        component(vec2Of(12, 54 + ICON_HEIGHT)) { pos ->
            listOf(
                    GuiValueBar(
                            pos = pos + ICON_SIZE + vec2Of(1, 0),
                            size = vec2Of(10, 54),
                            backgroundTextureOffset = vec2Of(10, 175),
                            colorTextureOffset = texture,
                            value = value,
                            tooltip = tooltip
                    ),
                    iconOf(icon, pos + vec2Of(2, 0))
            )
        }
    }

    fun electricBar(node: IElectricNode) {
        barOf(
                texture = vec2Of(23, 121),
                value = { node.voltage / 120.0 },
                tooltip = { listOf(String.format("%.2fV", node.voltage)) },
                icon = 1
        )
    }

    fun storageBar(node: ModuleInternalStorage) {
        barOf(
                texture = vec2Of(45, 121),
                value = { node.energy / node.capacity.toDouble() },
                tooltip = { listOf(node.energy.format() + "J") },
                icon = 0
        )
    }

    fun heatBar(node: IHeatNode) {
        barOf(
                texture = vec2Of(34, 121),
                value = { node.temperature / 4000.0 },
                tooltip = { listOf(formatHeat(node.temperature)) },
                icon = 2
        )
    }

    fun rfBar(node: IEnergyStorage) {
        barOf(
                texture = vec2Of(57, 121),
                value = { node.energyStored / node.maxEnergyStored.toDouble() },
                tooltip = { listOf(node.energyStored.format() + "RF") },
                icon = 8
        )
    }

    fun genericBar(index: Int, icon: Int, prov: IBarProvider, tooltip: () -> List<String>) {
        component(vec2Of(7, 60)) { pos -> listOf(CompDynamicBar(prov, index, pos + ICON_SIZE, tooltip), iconOf(icon, pos)) }
    }

    fun consumptionBar(va: ValueAverage, limit: Number) {
        bar2Of(
                texture = vec2Of(39, 176),
                value = { va.storage / limit.toDouble() },
                tooltip = { listOf(String.format("Consumption: %.2fW", va.storage)) },
                icon = 5
        )
    }

    fun slotSpacer(rows: Int = 1, columns: Int = 1) {
        component(vec2Of(18 * columns, 18 * rows)) { emptyList() }
    }

    fun fuelBar(mod: ModuleCombustionChamber) {
        val value = {
            if (mod.maxBurningTime == 0) 0.0 else {
                max(0, mod.maxBurningTime - mod.burningTime) / mod.maxBurningTime.toDouble()
            }
        }
        bar2Of(
                texture = vec2Of(21, 176),
                value = value,
                tooltip = { listOf(String.format("Fuel: %.1f%%", 100.0 * value())) },
                icon = 5
        )
    }

    fun fuelBar(mod: ModuleBigCombustionChamber) {
        val value = {
            if (mod.maxBurningTime == 0) 0.0 else {
                (mod.maxBurningTime - mod.burningTime) / mod.maxBurningTime.toDouble()
            }
        }
        bar2Of(
                texture = vec2Of(21, 176),
                value = value,
                tooltip = { listOf(String.format("Fuel: %.1f%%", 100.0 * value())) },
                icon = 5
        )
    }

    fun productionBar(va: ValueAverage, limit: Number) {
        bar2Of(
                texture = vec2Of(30, 176),
                value = { va.storage / limit.toDouble() },
                tooltip = { listOf(String.format("Production: %.2fW", va.storage)) },
                icon = 4
        )
    }

    fun machineFluidBar(va: ValueAverage, limit: Number) {
        bar2Of(
                texture = vec2Of(30, 176),
                value = { va.storage / limit.toDouble() },
                tooltip = { listOf(String.format("%.1f mB/t", va.storage)) },
                icon = 4
        )
    }

    fun electricTransferBar(va: ValueAverage, min: Number, max: Number) {
        component(vec2Of(7, 54 + ICON_HEIGHT)) { pos ->
            listOf(
                    TransferRateBar(
                            pos = pos + ICON_SIZE + vec2Of(1, 3),
                            value = { va.storage.toDouble() },
                            base = { 0.0 },
                            min = { min.toDouble() },
                            max = { max.toDouble() }
                    ),
                    iconOf(10, pos)
            )
        }
    }

    fun progressBar(timed: TimedCraftingProcess) {
        val value = { if (!timed.process.canCraft()) 0.0 else timed.timer / timed.limit().toDouble() }
        bar2Of(
                texture = vec2Of(66, 176),
                value = value,
                tooltip = { listOf(String.format("Progress: %.1f%%", 100 * value())) },
                icon = 4
        )
    }

    fun tank(tank: Tank, use: TankIO) {
        component(vec2Of(18, 50) + ICON_SIZE) { pos ->
            val icons = when (use) {
                TankIO.IN -> listOf(iconOf(5, pos + vec2Of(6, 0)))
                TankIO.OUT -> listOf(iconOf(3, pos + vec2Of(6, 0)))
                TankIO.INOUT -> listOf(iconOf(5, pos + vec2Of(2, 0)), iconOf(3, pos + vec2Of(9, 0)))
                TankIO.NONE -> emptyList()
            }
            listOf(CompFluidBar2(pos + ICON_SIZE, tank)) + icons
        }
    }

    fun drawable(hitbox: IVector2, offset: String, size: String, uv: String) {
        val offsetVec = config.points[offset] ?: Vec2d.ZERO
        val sizeVec = config.points[size] ?: Vec2d.ZERO
        val texVec = config.points[uv] ?: Vec2d.ZERO
        component(hitbox) { pos ->
            listOf(CompDrawable(pos + offsetVec, sizeVec, texVec))
        }
    }

    fun light(offset: String, size: String, uv: String, callback: () -> Boolean) {
        val offsetVec = config.points[offset] ?: Vec2d.ZERO
        val sizeVec = config.points[size] ?: Vec2d.ZERO
        val texVec = config.points[uv] ?: Vec2d.ZERO

        component(sizeVec) { pos ->
            listOf(CompDrawable(pos + offsetVec, sizeVec, texVec, callback))
        }
    }

    fun clickButton(id: String, offset: String) {
        val offsetVec = config.points[offset] ?: Vec2d.ZERO
        component(vec2Of(18, 18)) { pos ->
            listOf(ClickButton(pos + offsetVec, id))
        }
    }

    fun switchButton(id: String, offset: String,
                     enableIcon: String, disableIcon: String,
                     tooltipOn: String, tooltipOff: String = tooltipOn) {

        val offsetVec = config.points[offset] ?: Vec2d.ZERO
        val enableIconVec = config.points[enableIcon] ?: Vec2d.ZERO
        val disableIconVec = config.points[disableIcon] ?: Vec2d.ZERO

        component(vec2Of(18, 18)) { pos ->
            listOf(SwitchButton(
                    pos = pos + offsetVec,
                    enableIcon = enableIconVec,
                    disableIcon = disableIconVec,
                    tooltipEnable = tooltipOn,
                    tooltipDisable = tooltipOff,
                    id = id
            ))
        }
    }

    fun group(size: IVector2, dsl: DslBars.() -> Unit) {
        disableSpacing = true
        dsl()
        disableSpacing = false
        component(size) { emptyList() }
    }

    fun selectButton(size: IVector2, id: String, func: SelectButtonDsl.() -> Unit) {
        component(size) { pos ->
            listOf(SelectButtonDsl(config).apply(func).build(pos, id))
        }
    }

    fun getInternalSize(): IVector2 {
        val sizeX = components.sumBy {
            if (it.first.xi != 0) it.first.xi + paddingX * 2 else 0
        }
        val sizeY = components.fold(0) { acc, it -> max(acc, it.first.yi) }

        return vec2Of(sizeX, sizeY)
    }

    fun getExternalSize(): IVector2 {
        val size = getInternalSize()
        return vec2Of(size.x + marginX * 2, size.y + marginY * 2)
    }

    fun build(gui: AutoGui, container: AutoContainer, pos: IVector2) {
        var startX = pos.xi + marginX
        val startY = pos.yi + marginY

        gui.components += CompDynamicBackground(pos, getExternalSize())

        components.forEach { (compSize, factory) ->
            val compPos = vec2Of(startX + paddingX, startY)
            startX += if (compSize.xi != 0) compSize.xi + paddingX * 2 else 0

            if (Debug.DEBUG) {
                gui.components += CompDebugOutline(compPos, compSize)
            }
            gui.components += factory(compPos)
        }

        gui.components.filterIsInstance<ClickButton>().forEach {
            val listener = container.buttonListeners[it.id]
            if (listener != null) {
                it.onClick = listener
            }
        }

        gui.components.filterIsInstance<SwitchButton>().forEach {
            val listener = container.buttonListeners[it.id]
            val callback = container.switchButtonCallbacks[it.id]

            if (listener != null) {
                it.onClick = listener
            }
            if (callback != null) {
                it.isEnable = callback
            }
        }

        gui.components.filterIsInstance<SelectButton>().forEach { btn ->
            val callback = container.selectButtonCallbacks[btn.id]

            if (callback != null) {
                btn.selectedOption = callback
            }

            val listeners = mutableMapOf<Int, () -> Unit>()
            for (index in btn.options.indices) {
                val listener = container.buttonListeners["${btn.id}_$index"]
                if (listener != null) {
                    listeners[index] = listener
                }
            }
            btn.onClick = { index -> listeners[index]?.invoke() }
        }
    }
}

class SelectButtonDsl(val config: GuiConfig) {

    private val options = mutableListOf<SelectButton.SelectOption>()

    fun option(offset: String, background: String, tooltip: String) {
        val offsetVec = config.points[offset] ?: Vec2d.ZERO
        val backgroundVec = config.points[background] ?: Vec2d.ZERO
        options += SelectButton.SelectOption(offsetVec, backgroundVec, tooltip)
    }

    fun build(pos: IVector2, id: String): SelectButton {
        return SelectButton(pos, options, id)
    }
}

class DslComponents(val config: GuiConfig) {

    private val components = mutableListOf<IComponent>()

    private fun barOf(pos: IVector2, texture: IVector2,
                      value: () -> Double, tooltip: () -> List<String> = { emptyList() }, icon: Int) {

        components += GuiValueBar(
                pos = pos + ICON_SIZE,
                size = vec2Of(12, 54),
                colorTextureOffset = texture,
                value = value,
                tooltip = tooltip
        )
        components += iconOf(icon, pos + vec2Of(2, 0))
    }

    fun electricBar(pos: String, node: IElectricNode) {
        val posVec = config.points[pos] ?: Vec2d.ZERO
        barOf(
                pos = posVec,
                texture = vec2Of(23, 121),
                value = { node.voltage / 120.0 },
                tooltip = { listOf(String.format("%.2fV", node.voltage)) },
                icon = 1
        )
    }

    fun storageBar(pos: String, node: ModuleInternalStorage) {
        val posVec = config.points[pos] ?: Vec2d.ZERO
        barOf(
                pos = posVec,
                texture = vec2Of(45, 121),
                value = { node.energy / node.capacity.toDouble() },
                tooltip = { listOf(node.energy.format() + "J") },
                icon = 0
        )
    }

    fun searchBar(id: String, pos: String) {
        val posVec = config.points[pos] ?: Vec2d.ZERO
        components += SearchBar(posVec, id)
    }

    fun scrollBar(id: String, pos: String, steps: Int) {
        val posVec = config.points[pos] ?: Vec2d.ZERO
        components += ScrollBar(posVec, steps, id)
    }

    fun custom(vararg points: String, func: (List<IVector2>) -> IComponent) {
        val vecs = points.map { config.points[it] ?: Vec2d.ZERO }
        components += func(vecs)
    }

    fun selectButton(id: String, func: SelectButtonDsl.() -> Unit) {
        components += SelectButtonDsl(config).apply(func).build(Vec2d.ZERO, id)
    }

    fun drawable(offset: String, size: String, uv: String) {
        val offsetVec = config.points[offset] ?: Vec2d.ZERO
        val sizeVec = config.points[size] ?: Vec2d.ZERO
        val texVec = config.points[uv] ?: Vec2d.ZERO
        components += CompDrawable(offsetVec, sizeVec, texVec)
    }

    fun light(offset: String, size: String, uv: String, callback: () -> Boolean) {
        val offsetVec = config.points[offset] ?: Vec2d.ZERO
        val sizeVec = config.points[size] ?: Vec2d.ZERO
        val texVec = config.points[uv] ?: Vec2d.ZERO

        components += CompDrawable(offsetVec, sizeVec, texVec, callback)
    }

    fun clickButton(id: String, offset: String) {
        val offsetVec = config.points[offset] ?: Vec2d.ZERO
        components += ClickButton(offsetVec, id)
    }

    fun build(gui: AutoGui, container: AutoContainer) {
        gui.components += components

        gui.components.filterIsInstance<ClickButton>().forEach {
            val listener = container.buttonListeners[it.id]
            if (listener != null) {
                it.onClick = listener
            }
        }

        gui.components.filterIsInstance<SelectButton>().forEach { btn ->
            val callback = container.selectButtonCallbacks[btn.id]

            if (callback != null) {
                btn.selectedOption = callback
            }

            val listeners = mutableMapOf<Int, () -> Unit>()
            for (index in btn.options.indices) {
                val listener = container.buttonListeners["${btn.id}_$index"]
                if (listener != null) {
                    listeners[index] = listener
                }
            }
            btn.onClick = { index -> listeners[index]?.invoke() }
        }
    }
}

private fun iconOf(index: Int, pos: IVector2): CompImage = CompImage(guiTexture("misc"), DrawableBox(
        screenPos = pos,
        screenSize = vec2Of(7, 8),
        texturePos = vec2Of(37 + (index % 8) * 8, 62 + (index / 8) * 9),
        textureSize = vec2Of(7, 8),
        textureScale = vec2Of(256)
))