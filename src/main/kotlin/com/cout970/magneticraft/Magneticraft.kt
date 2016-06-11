package com.cout970.magneticraft

import com.cout970.magneticraft.config.ConfigHandler
import com.cout970.magneticraft.gui.GuiHandler
import com.cout970.magneticraft.proxy.CommonProxy
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.world.WorldGenerator
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.common.registry.GameRegistry
import java.io.File

@Mod(
    modid = MODID,
    name = NAME,
    version = VERSION,
    modLanguage = "kotlin",
    modLanguageAdapter = LANG_ADAPTER
)
object Magneticraft {
    val network = SimpleNetworkWrapper(MODID)
    lateinit var configFile : File

    @SidedProxy(
        clientSide = "com.cout970.magneticraft.proxy.ClientProxy",
        serverSide = "com.cout970.magneticraft.proxy.CommonProxy"
    )
    lateinit var proxy: CommonProxy;

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        Log.setLogger(event.modLog)
        configFile = event.suggestedConfigurationFile
        Log.info("Starting pre-init")
        ConfigHandler.load()
        ConfigHandler.read()
        ConfigHandler.save()


        registerBlocks()
        registerItems()
        registerTileEntities()

        if(Debug.DEBUG){
           Debug.preInit(event)
        }

        proxy.preInit()

        Log.info("Pre-init done")
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        Log.info("Starting init")

        proxy.init()
        WorldGenerator.init()
        GameRegistry.registerWorldGenerator(WorldGenerator, 10)
        NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler)

        Log.info("Init done")
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        Log.info("Starting post-init")

        proxy.postInit()

        Log.info("Post-init done")
    }
}