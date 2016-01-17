package com.cout970.magneticraft;

import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.api.kinetic.defaults.KineticConductor;
import com.cout970.magneticraft.api.kinetic.defaults.KineticStorageHandler;
import com.cout970.magneticraft.proxy.CommonProxy;
import com.cout970.magneticraft.util.Log;
import com.cout970.magneticraft.world.ManagerWorldGen;
import net.darkaqua.blacksmith.api.event.EventSubscribe;
import net.darkaqua.blacksmith.api.event.modloader.IInitEvent;
import net.darkaqua.blacksmith.api.event.modloader.IPostInitEvent;
import net.darkaqua.blacksmith.api.event.modloader.IPreInitEvent;
import net.darkaqua.blacksmith.api.modloader.BlacksmithMod;
import net.darkaqua.blacksmith.api.modloader.ModInstance;
import net.darkaqua.blacksmith.api.modloader.ModSidedProxy;
import net.darkaqua.blacksmith.api.registry.StaticAccess;

import java.io.File;

/**
 * Created by cout970 on 06/12/2015.
 */
@BlacksmithMod(id = Magneticraft.ID, name = Magneticraft.NAME, version = Magneticraft.VERSION
//        , dependencies = "required-after:ForgeMultipart;after:BuildCraft|Core;after:CoFHCore;after:IC2;after:Railcraft;after:ImmersiveEngineering"
)
public class Magneticraft {

    public final static String ID = "magneticraft";
    public final static String NAME = "Magneticraft";
    public final static String VERSION = "@VERSION@";
    public static final boolean DEBUG = StaticAccess.GAME.isDeobfuscatedEnvironment();
    public static String DEV_HOME;

    @ModInstance(NAME)
    public static Magneticraft INSTANCE;

    @ModSidedProxy(clientSide = "com.cout970.magneticraft.proxy.ClientProxy",
            serverSide = "com.cout970.magneticraft.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventSubscribe
    public void preInit(IPreInitEvent event) {
        Log.LOGGER = event.getModLog();
        Log.info("Starting preInit");
        ManagerConfig.init(event.getSuggestedConfigurationFile());

        ManagerBlocks.initBlocks();
        ManagerItems.initItems();

//        ManagerFluids.initFluids();

        proxy.init();
        StaticAccess.GAME.getInterModRegistry().registerInterface(IKineticConductor.class, new KineticStorageHandler(), () -> new KineticConductor(null));

        ManagerNetwork.init();
        ManagerRecipe.init();

        if (DEBUG) {
            //BEGIN FINDING OF SOURCE DIR
            File temp = event.getModConfigurationDirectory();
            while (temp != null && temp.isDirectory()) {
                if (new File(temp, "build.gradle").exists()) {
                    DEV_HOME = temp.getAbsolutePath();
                    Log.info("Found source code directory at " + DEV_HOME);
                    break;
                }
                temp = temp.getParentFile();
            }
            if (DEV_HOME == null) {
                throw new RuntimeException("Could not find source code directory!");
            }
            //END FINDING OF SOURCE DIR
            LangHelper.setupLangFile();
        }

//        ManagerOreDict.registerOreDict();
        Log.info("preInit Done");
    }

    @EventSubscribe
    public void init(IInitEvent e) {
        Log.info("Starting Init");
        StaticAccess.GAME.getWorldGenerationRegistry().registerWorldGenerator(new ManagerWorldGen(), 10);
        Log.info("Init Done");
    }

    @EventSubscribe
    public void postInit(IPostInitEvent e) {

    }
}
