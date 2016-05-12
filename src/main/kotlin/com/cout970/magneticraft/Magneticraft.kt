package com.cout970.magneticraft

import com.cout970.magneticraft.gui.GuiHandler
import com.cout970.magneticraft.proxy.CommonProxy
import com.cout970.magneticraft.util.LANG_ADAPTER
import com.cout970.magneticraft.util.MODID
import com.cout970.magneticraft.util.NAME
import com.cout970.magneticraft.util.VERSION
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import org.apache.logging.log4j.Logger

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

    @SidedProxy(
        clientSide = "com.cout970.magneticraft.proxy.ClientProxy",
        serverSide = "com.cout970.magneticraft.proxy.CommonProxy"
    )
    lateinit var proxy: CommonProxy;

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        log = event.modLog
        log.info("Starting pre-init")

        registerBlocks()
        registerItems()
        proxy.preInit()

        log.info("Pre-init done")
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        log.info("Starting init")

        proxy.init()
        NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler)

        log.info("Init done")
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        log.info("Starting post-init")

        proxy.postInit()

        log.info("Post-init done")
    }
}