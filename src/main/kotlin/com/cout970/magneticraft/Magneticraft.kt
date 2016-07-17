package com.cout970.magneticraft

import coffee.cypher.mcextlib.extensions.blocks.stack
import coffee.cypher.mcextlib.extensions.items.stack
import com.cout970.magneticraft.api.registries.machines.crushingtable.CrushingTableRegistry
import com.cout970.magneticraft.config.ConfigHandler
import com.cout970.magneticraft.proxy.CommonProxy
import com.cout970.magneticraft.util.LANG_ADAPTER
import com.cout970.magneticraft.util.MODID
import com.cout970.magneticraft.util.NAME
import com.cout970.magneticraft.util.VERSION
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import org.apache.logging.log4j.Logger
import java.io.File

@Mod(
    modid = MODID,
    name = NAME,
    version = VERSION,
    modLanguage = "kotlin",
    modLanguageAdapter = LANG_ADAPTER
)
object Magneticraft {
    lateinit var log: Logger
    val network = SimpleNetworkWrapper(MODID)
    lateinit var configFile: File

    @SidedProxy(
        clientSide = "com.cout970.magneticraft.proxy.ClientProxy",
        serverSide = "com.cout970.magneticraft.proxy.ServerProxy"
    )
    lateinit var proxy: CommonProxy

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        log = event.modLog
        configFile = event.suggestedConfigurationFile

        log.info("Starting pre-init")
        ConfigHandler.load()
        ConfigHandler.read()
        ConfigHandler.save()

        proxy.preInit()

        if (Debug.DEBUG) {
            Debug.preInit(event)
        }

        log.info("Pre-init done")
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        log.info("Starting init")

        proxy.init()

        CrushingTableRegistry.registerRecipe(Items.SKULL.stack(meta = 4), Items.GUNPOWDER.stack(size = 8))
        CrushingTableRegistry.registerRecipe(Blocks.DIRT.stack(), Blocks.DIAMOND_BLOCK.stack(size = 64))

        log.info("Init done")
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        log.info("Starting post-init")

        proxy.postInit()

        log.info("Post-init done")
    }
}