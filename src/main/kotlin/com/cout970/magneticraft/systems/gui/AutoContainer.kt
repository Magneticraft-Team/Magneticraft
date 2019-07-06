package com.cout970.magneticraft.systems.gui

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.misc.network.IBD
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun <T> autoContainer(name: String, configFunc: GuiBuilder.(T) -> Unit, tile: T, player: EntityPlayer, world: World, pos: BlockPos): AutoContainer {
    if (Debug.DEBUG) GuiConfig.loadAll()
    val config = GuiConfig.config[name] ?: error("Unable to find gui in config: $name")
    val builder = GuiBuilder(config)
    configFunc.invoke(builder, tile)

    val container = builder.containerClass(builder, builder.containerConfig, player, world, pos)
    container.postInit()

    return container
}

open class AutoContainer(val builder: GuiBuilder, configFunc: (AutoContainer) -> Unit, player: EntityPlayer, world: World, pos: BlockPos)
    : ContainerBase(player, world, pos) {

    val buttonListeners = mutableMapOf<String, () -> Unit>()
    val switchButtonCallbacks = mutableMapOf<String, () -> Boolean>()
    val selectButtonCallbacks = mutableMapOf<String, () -> Int>()
    var receiveDataFromClientFunc: ((IBD) -> Unit)? = null

    init {
        configFunc(this)
    }

    open fun postInit() = Unit

    public override fun addSlotToContainer(slotIn: Slot): Slot {
        return super.addSlotToContainer(slotIn)
    }

    override fun receiveDataFromClient(ibd: IBD) {
        receiveDataFromClientFunc?.invoke(ibd)
    }
}