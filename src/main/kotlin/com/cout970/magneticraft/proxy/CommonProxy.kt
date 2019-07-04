package com.cout970.magneticraft.proxy

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.misc.logTime
import com.cout970.magneticraft.registry.*
import com.cout970.magneticraft.systems.gui.GuiConfig
import com.cout970.magneticraft.systems.gui.GuiHandler
import com.cout970.magneticraft.systems.multiblocks.MultiblockManager
import com.cout970.magneticraft.systems.network.MessageContainerUpdate
import com.cout970.magneticraft.systems.network.MessageGuiUpdate
import com.cout970.magneticraft.systems.network.MessageTileUpdate
import com.cout970.magneticraft.systems.worldgen.WorldGenerator
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side

/**
 * This class has the task to initialize the mod, in both sides, client and server
 * See ClientProxy for more tasks executed only in the client
 * See ServerProxy for more tasks executed only in the server, currently nothing
 */
abstract class CommonProxy {

    @SubscribeEvent
    fun initBlocksEvent(event: RegistryEvent.Register<Block>) {
        initBlocks(event.registry)
    }

    @SubscribeEvent
    fun initItemsEvent(event: RegistryEvent.Register<Item>) {
        initItems(event.registry)
        postItemRegister()
    }

    open fun postItemRegister() {
        // There are no registries for this stuff and there are no events between the item registration and init
        // so this shit need be here
        // @formatter:off
        logTime("Task registerCapabilities:")         { registerCapabilities() }
        logTime("Task initTileEntities:")             { initTileEntities() }
        logTime("Task initFluids:")                   { initFluids() }
        logTime("Task registerOreDictionaryEntries:") { registerOreDictionaryEntries() }
        logTime("Task registerOreGenerations:")       { registerOreGenerations() }
        logTime("Task registerMultiblocks:")          { MultiblockManager.registerDefaults() }
        // @formatter:on
    }

    open fun preInit() {
        GuiConfig.loadAll()
        MinecraftForge.EVENT_BUS.register(this)
    }

    open fun init() {
        //Init recipes
        logTime("Task registerRecipes:") {
            registerRecipes()
        }

        logTime("Task registerMisc:") {
            registerMisc()
        }

        //World generator
        logTime("Task registerWorldGenerator:") {
            WorldGenerator.init()
            GameRegistry.registerWorldGenerator(WorldGenerator, 10)
        }

        //Gui
        logTime("Task registerGuiHandler:") {
            NetworkRegistry.INSTANCE.registerGuiHandler(Magneticraft, GuiHandler)
        }

        //Network
        //Note for implementing Messages:
        //The class that implements IMessage must have an empty constructor
        logTime("Task registerNetworkMessages:") {
            Magneticraft.network.registerMessage(MessageContainerUpdate, MessageContainerUpdate::class.java, 0, Side.CLIENT)
            Magneticraft.network.registerMessage(MessageTileUpdate, MessageTileUpdate::class.java, 1, Side.CLIENT)
            Magneticraft.network.registerMessage(MessageTileUpdate, MessageTileUpdate::class.java, 2, Side.SERVER)
            Magneticraft.network.registerMessage(MessageGuiUpdate, MessageGuiUpdate::class.java, 3, Side.SERVER)
        }
    }

    open fun postInit() = Unit

    /**
     * The side of the proxy
     */
    abstract fun getSide(): Side
}