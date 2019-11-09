package com.cout970.magneticraft

import com.cout970.magneticraft.misc.Asm
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.forgespi.language.ModFileScanData
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

@Mod(MOD_ID)
object Magneticraft {

    //Mod logger, please use the functions in utils.Logger instead
    val log: Logger = LogManager.getLogger()

    //The reference to the config file used by ConfigHandler
    lateinit var configFile: File

    //The jar file of the mod
    lateinit var sourceFile: File

    // Used to auto-register classes with an specific annotation
    internal val asmData: ModFileScanData

    init {
        // Crash immediately if kotlin is not loaded
        try {
            Class.forName("kotlin.jvm.internal.Intrinsics")
        } catch (error: NoClassDefFoundError) {
            error("Mod Magneticraft requires the Kotlin standard library, please install Forgelin")
        }

        // Set debug mode if in dev environment
        Debug.init()

        // Get info about annotated classes
        asmData = Asm.getPrivateField(ModLoadingContext.get().activeContainer, "scanResults") as ModFileScanData

        // FML event bus
        val modEventBus = FMLJavaModLoadingContext.get().modEventBus

        // Start initialization process
        Init.init(modEventBus)
    }
}
