package com.cout970.magneticraft.systems.gui.json

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.systems.gui.containers.ContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun <T> jsonContainer(name: String, configFunc: GuiBuilder.(T) -> Unit, tile: T, player: EntityPlayer, world: World, pos: BlockPos): JsonContainer {
    if (Debug.DEBUG) GuiConfig.loadAll()
    val config = GuiConfig.config[name] ?: error("Unable to find gui in config: $name")
    val builder = GuiBuilder(config)
    configFunc.invoke(builder, tile)

    return JsonContainer(builder, builder.containerConfig, player, world, pos)
}

class JsonContainer(val gui: GuiBuilder, configFunc: (JsonContainer) -> Unit, player: EntityPlayer, world: World, pos: BlockPos) : ContainerBase(player, world, pos) {

    init {
        configFunc(this)
    }

    public override fun addSlotToContainer(slotIn: Slot): Slot {
        return super.addSlotToContainer(slotIn)
    }
}