package com.cout970.magneticraft

import com.cout970.magneticraft.registry.registerSounds
import com.cout970.magneticraft.systems.gui.AutoContainer
import com.cout970.magneticraft.systems.gui.AutoGui
import com.cout970.magneticraft.systems.gui.GuiHandler
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilerenderers.TileRenderer
import net.minecraft.block.Block
import net.minecraft.client.gui.ScreenManager
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.SoundEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.network.IContainerFactory

object RegistryEvents {

    val tileEntityRenderers = mutableListOf<TileRenderer<out TileBase>>()
    val blocks: MutableList<Block> = mutableListOf()
    val items: MutableList<Item> = mutableListOf()
    val tiles: MutableMap<Class<TileEntity>, TileEntityType<*>> = mutableMapOf()
    lateinit var container: ContainerType<AutoContainer>

    @SubscribeEvent
    fun blocks(event: RegistryEvent.Register<Block>) {
        Init.initBlocks(event.registry)
    }

    @SubscribeEvent
    fun items(event: RegistryEvent.Register<Item>) {
        Init.initItems(event.registry)
    }

    @SubscribeEvent
    fun tiles(event: RegistryEvent.Register<TileEntityType<*>>) {
        Init.initTiles(event.registry)
    }

    @SubscribeEvent
    fun sounds(event: RegistryEvent.Register<SoundEvent>) {
        registerSounds(event.registry)
    }

    @SubscribeEvent
    fun containers(event: RegistryEvent.Register<ContainerType<*>>) {
        container = ContainerType(IContainerFactory { window, inventory, buff -> GuiHandler.create(window, inventory, buff) })
        event.registry.register(container)
        ScreenManager.registerFactory(container) { container, _, _ -> AutoGui(container) }
    }
}