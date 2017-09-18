package com.cout970.magneticraft

import com.cout970.magneticraft.config.ConfigHandler
import com.cout970.magneticraft.integration.IntegrationHandler
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.proxy.CommonProxy
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import org.apache.logging.log4j.Logger
import java.io.File
import kotlin.system.measureTimeMillis

@Suppress("UNUSED_PARAMETER", "unused")
//Basic mod information for Forge
@Mod(
        modid = MOD_ID,
        name = MOD_NAME,
        version = "2.0.1",
        modLanguage = "kotlin",
        modLanguageAdapter = LANG_ADAPTER,
        acceptedMinecraftVersions = "[1.12]",
        dependencies = "required-after:modelloader@[1.0.5,);required-after:forgelin",
        updateJSON = "https://raw.githubusercontent.com/Magneticraft-Team/Magneticraft/1.12/src/main/resources/update.json"
)
//Singleton, see KotlinAdapter to know how it's loaded by forge
object Magneticraft {

    //Mod logger, please use the functions in utils.Logger instead
    lateinit var log: Logger

    //Main Network wrapper, see CommonProxy for packet registration
    val network = SimpleNetworkWrapper(MOD_ID)

    //The reference to the config file used by ConfigHandler
    lateinit var configFile: File

    //The jar file of the mod
    lateinit var sourceFile: File

    // Used to auto-register classes with an specific annotation
    internal lateinit var asmData: ASMDataTable

    /**
     * The sided proxy, used to initialize the mod differently in the server than the client
     * See ClientProxy and ServerProxy
     */
    @SidedProxy(
            clientSide = "com.cout970.magneticraft.proxy.ClientProxy",
            serverSide = "com.cout970.magneticraft.proxy.ServerProxy"
    )
    lateinit var proxy: CommonProxy

    /**
     * preInit event, called when the game starts to load blocks, items, the config, etc
     */
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        asmData = event.asmData
        log = event.modLog
        configFile = event.suggestedConfigurationFile
        sourceFile = event.sourceFile

        val time = measureTimeMillis {
            //Enables the creative tab
            CreativeTabMg

            log.info("Starting pre-init")
            log.info("Loading config...")
            val configTime = measureTimeMillis {
                ConfigHandler.apply {
                    load()
                    read()
                    save()
                }
            }
            log.info("Config loaded in $configTime milliseconds")

            //Initialization of the Mod stuff
            proxy.preInit()

            //Detection of other mods installed for compatibility
            IntegrationHandler.preInit()

            if (Debug.DEBUG) {
                Debug.preInit(event)
            }
        }

        log.info("Pre-init done in $time milliseconds")
    }

    /**
     * init event, called when the game has all the blocks, items, etc to load recipes, models and network packets
     */
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        log.info("Starting init")

        val time = measureTimeMillis {
            proxy.init()
        }
        IntegrationHandler.init()

        log.info("Init done in $time milliseconds")
    }

    /**
     * postInit event, called after all the initialization stuff
     * not used currently, is meant for inter-mod stuff after all mods are loaded
     */
    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        log.info("Starting post-init")

        val time = measureTimeMillis {
            proxy.postInit()
        }

        log.info("Post-init done in $time milliseconds")
    }
}