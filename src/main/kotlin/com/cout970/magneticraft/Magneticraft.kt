package com.cout970.magneticraft

import coffee.cypher.mcextlib.extensions.blocks.stack
import coffee.cypher.mcextlib.extensions.items.stack
import com.cout970.magneticraft.api.registries.machines.crushingtable.CrushingTableRegistry
import com.cout970.magneticraft.client.sounds.registerSounds
import com.cout970.magneticraft.config.ConfigHandler
import com.cout970.magneticraft.gui.GuiHandler
import com.cout970.magneticraft.network.MessageIBD
import com.cout970.magneticraft.proxy.CommonProxy
import com.cout970.magneticraft.registry.*
import com.cout970.magneticraft.util.LANG_ADAPTER
import com.cout970.magneticraft.util.MODID
import com.cout970.magneticraft.util.NAME
import com.cout970.magneticraft.util.VERSION
import com.cout970.magneticraft.world.WorldGenerator
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
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
        serverSide = "com.cout970.magneticraft.proxy.CommonProxy"
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

        registerBlocks()
        registerItems()
        registerTileEntities()
        registerOreDictionaryEntries()
        registerSounds()
        registerRecipes()
        registerCapabilities()

        if (Debug.DEBUG) {
            Debug.preInit(event)
        }

        proxy.preInit()

        log.info("Pre-init done")
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        log.info("Starting init")

        proxy.init()
        WorldGenerator.init()
        GameRegistry.registerWorldGenerator(WorldGenerator, 10)
        NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler)

        network.registerMessage(MessageIBD.Companion, MessageIBD::class.java, 0, Side.CLIENT)

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