package com.cout970.magneticraft.proxy

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.gui.GuiHandler
import com.cout970.magneticraft.network.MessageContainerUpdate
import com.cout970.magneticraft.network.MessageTileUpdate
import com.cout970.magneticraft.registry.*
import com.cout970.magneticraft.world.WorldGenerator
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side

abstract class CommonProxy {

    open fun preInit() {
        //common preInit stuff
        registerBlocks()
        registerItems()
        registerTileEntities()
        registerOreDictionaryEntries()
        registerRecipes()
        registerCapabilities()
    }

    open fun init() {
        //world generator
        WorldGenerator.init()
        GameRegistry.registerWorldGenerator(WorldGenerator, 10)

        //gui
        NetworkRegistry.INSTANCE.registerGuiHandler(Magneticraft, GuiHandler)

        //network
        Magneticraft.network.registerMessage(MessageContainerUpdate.Companion, MessageContainerUpdate::class.java, 0, Side.CLIENT)
        Magneticraft.network.registerMessage(MessageTileUpdate.Companion, MessageTileUpdate::class.java, 1, Side.CLIENT)
        Magneticraft.network.registerMessage(MessageTileUpdate.Companion, MessageTileUpdate::class.java, 2, Side.SERVER)
    }

    open fun postInit() {}

    abstract fun getSide(): Side
}