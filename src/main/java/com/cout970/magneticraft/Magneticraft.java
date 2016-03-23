package com.cout970.magneticraft;

import com.cout970.magneticraft.block.BlockBase;
import com.cout970.magneticraft.proxy.CommonProxy;
import com.cout970.magneticraft.util.Log;
import com.cout970.magneticraft.world.ManagerWorldGen;
import net.darkaqua.blacksmith.Blacksmith;
import net.darkaqua.blacksmith.Game;
import net.darkaqua.blacksmith.lang.ILangManager;
import net.darkaqua.blacksmith.lang.LangManagerFactory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

/**
 * Created by cout970 on 06/12/2015.
 */
@Mod(modid = Magneticraft.ID, name = Magneticraft.NAME, version = Magneticraft.VERSION)
public class Magneticraft {

    public final static String ID = "magneticraft";
    public final static String NAME = "Magneticraft";
    public final static String VERSION = "@VERSION@";
    public static final boolean DEBUG = Game.isDeobfuscatedEnvironment();
    public static ILangManager LANG;
    public static String DEV_HOME;

    @Mod.Instance
    public static Magneticraft INSTANCE;

    @SidedProxy(clientSide = "com.cout970.magneticraft.proxy.ClientProxy",
            serverSide = "com.cout970.magneticraft.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Log.info("Starting preInit");
        Blacksmith.preInit();
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

        }
        LANG = LangManagerFactory.createLangManager(new File(Magneticraft.DEV_HOME + "/src/main/resources/assets/magneticraft/lang/"));
        LANG.addName("itemGroup."+ BlockBase.CREATIVE_TAB_MAIN.getTabLabel(), "Magneticraft Main");

        ManagerConfig.init(event.getSuggestedConfigurationFile());

        ManagerBlocks.initBlocks();
        ManagerItems.initItems();

        proxy.init();
        ManagerApi.init();
        ManagerNetwork.init();
        ManagerOreDict.registerOredictNames();
        ManagerRecipe.init();

        if (DEBUG) {
            LANG.save();
        }

        Log.info("preInit Done");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        Log.info("Starting Init");
        GameRegistry.registerWorldGenerator(new ManagerWorldGen(), 10);
        Log.info("Init Done");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {

    }
}
