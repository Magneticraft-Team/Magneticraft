package com.cout970.magneticraft;


import net.darkaqua.blacksmith.config.ConfigHandler;
import net.darkaqua.blacksmith.config.ConfigurationFactory;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

/**
 * Created by cout970 on 06/12/2015.
 */
public class ManagerConfig {

    public static ConfigHandler config;
    public static final Object CONFIG = new Object();//TODO

    public static void init(File file) {
        if (config == null) {
            config = new ConfigHandler(CONFIG, ConfigurationFactory.create(file));
            LoadConfigs();
        }
    }

    @Mod.EventHandler
    public void configChangeEvent(ConfigChangedEvent event) {
        if (event.modID.equalsIgnoreCase(Magneticraft.ID)) {
            LoadConfigs();
        }
    }

    public static void LoadConfigs() {
        config.read();
        config.save();
    }

//    private static OreGenConfig getOreConfig(Configuration conf, String name, int chunk, int vein, int max, int min) {
//        boolean active = conf.getBoolean(name + "_gen_active", Configuration.CATEGORY_GENERAL, true, "Generation of " + name);
//        int amount_per_chunk = conf.getInt(name + "_amount_chunk", Configuration.CATEGORY_GENERAL, chunk, 0, 20, "Number of veins of " + name + " per chunk");
//        int amount_per_vein = conf.getInt(name + "_amount_vein", Configuration.CATEGORY_GENERAL, vein, 0, 20, "Max amount of blocks of " + name + " in a vein");
//        int max_height = conf.getInt(name + "_max_height", Configuration.CATEGORY_GENERAL, max, 0, 256, "Max height for generation of " + name);
//        int min_height = conf.getInt(name + "_min_height", Configuration.CATEGORY_GENERAL, min, 0, 256, "Min height for generation of " + name);
//        return new OreGenConfig(active, amount_per_chunk, amount_per_vein, max_height, min_height);
//    }
//
//    private static GaussOreGenConfig getGaussOreConfig(Configuration conf, String name, int chunk, float deviation, int min_am, int max_am, int vein, int max, int min) {
//        boolean active = conf.getBoolean(name + "_gen_active", Configuration.CATEGORY_GENERAL, true, "Generation of " + name);
//        int amount_per_chunk = conf.getInt(name + "_amount_chunk", Configuration.CATEGORY_GENERAL, chunk, 0, 30, "Average number of veins of " + name + " per chunk");
//        float amount_deviation = conf.getFloat(name + "_std_dev", Configuration.CATEGORY_GENERAL, deviation, 0F, 10F, "Standard deviation of number of veins per chunk.\nHigher value means more chunks with low/high amounts of veins, lower - more with medium amount");
//        int min_chunk = conf.getInt(name + "_min_per_chunk", Configuration.CATEGORY_GENERAL, min_am, 0, amount_per_chunk, "Minimal amount of veins per chunk");
//        int max_chunk = conf.getInt(name + "_max_per_chunk", Configuration.CATEGORY_GENERAL, max_am, 0, 100, "Maximal amount of veins per chunk");
//        int amount_per_vein = conf.getInt(name + "_amount_vein", Configuration.CATEGORY_GENERAL, vein, 0, 50, "Max amount of blocks of " + name + " in a vein");
//        int max_height = conf.getInt(name + "_max_height", Configuration.CATEGORY_GENERAL, max, 0, 256, "Max height for generation of " + name);
//        int min_height = conf.getInt(name + "_min_height", Configuration.CATEGORY_GENERAL, min, 0, 256, "Min height for generation of " + name);
//        return new GaussOreGenConfig(active, amount_per_chunk, amount_deviation, min_chunk, max_chunk, amount_per_vein, max_height, min_height);
//    }
}
