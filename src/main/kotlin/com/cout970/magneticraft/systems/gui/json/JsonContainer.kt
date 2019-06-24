package com.cout970.magneticraft.systems.gui.json

import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.containers.ContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class JsonContainer(name: String, configFunc: (GuiBuilder) -> Unit, player: EntityPlayer, world: World, pos: BlockPos) : ContainerBase(player, world, pos) {

    val config: GuiConfig
    val guiComponents = mutableMapOf<String, AbstractGuiComponent>()

    init {
        GuiConfig.loadAll()
        config = GuiConfig.allConfigs[name]!!

        GuiBuilder(this).apply(configFunc)

        config.slots.forEach { group ->
            repeat(group.rows) { row ->
                repeat(group.columns) loop@{ column ->
                    val index = group.startIndex + column + row * group.columns
                    val slot = inventorySlots.getOrNull(index) ?: return@loop

                    slot.xPos = group.posX + column * 18
                    slot.yPos = group.posY + row * 18
                }
            }
        }

        if (config.playerInventory) {
            val offsetX = config.background.sizeX - 176
            val offsetY = config.background.sizeY - 166
            bindPlayerInventory(player.inventory, vec2Of(offsetX, offsetY))
        }
    }

    public override fun addSlotToContainer(slotIn: Slot): Slot {
        return super.addSlotToContainer(slotIn)
    }
}