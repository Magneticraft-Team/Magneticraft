package com.cout970.magneticraft.proxy

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.gui.GuiHandler
import com.cout970.magneticraft.multiblock.MultiblockManager
import com.cout970.magneticraft.network.MessageContainerUpdate
import com.cout970.magneticraft.network.MessageGuiUpdate
import com.cout970.magneticraft.network.MessageTileUpdate
import com.cout970.magneticraft.registry.registerCapabilities
import com.cout970.magneticraft.registry.registerFuelHandler
import com.cout970.magneticraft.registry.registerOreDictionaryEntries
import com.cout970.magneticraft.registry.registerRecipes
import com.cout970.magneticraft.world.WorldGenerator
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side

/**
 * This class has the task to initialize the mod, in both sides, client and server
 * See ClientProxy for more tasks executed only in the client
 * See ServerProxy for more tasks executed only in the server, currently nothing
 */
abstract class CommonProxy {

    open fun preInit() {
        //Common preInit stuff
        registerFuelHandler()
        registerOreDictionaryEntries()
        registerCapabilities()
        MultiblockManager.registerDefaults()
    }

    open fun init() {
        //Init recipes
        registerRecipes()

        //World generator
        WorldGenerator.init()
        GameRegistry.registerWorldGenerator(WorldGenerator, 10)

        //Gui
        NetworkRegistry.INSTANCE.registerGuiHandler(Magneticraft, GuiHandler)

        //Network
        //Note for implementing Messages:
        //The class that implements IMessage must have an empty constructor
        Magneticraft.network.registerMessage(MessageContainerUpdate.Companion, MessageContainerUpdate::class.java, 0, Side.CLIENT)
        Magneticraft.network.registerMessage(MessageTileUpdate.Companion, MessageTileUpdate::class.java, 1, Side.CLIENT)
        Magneticraft.network.registerMessage(MessageTileUpdate.Companion, MessageTileUpdate::class.java, 2, Side.SERVER)
        Magneticraft.network.registerMessage(MessageGuiUpdate.Companion, MessageGuiUpdate::class.java, 3, Side.SERVER)
    }

    open fun postInit() = Unit

    /**
     * The side of the proxy
     */
    abstract fun getSide(): Side
}