package com.cout970.magneticraft.client.model;

import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.modelloader.techne.TechneModelLoader;
import net.darkaqua.blacksmith.render.TextureManager;
import net.darkaqua.blacksmith.util.ResourceReference;

/**
 * Created by cout970 on 16/12/2015.
 */
public class ModelConstants {

    public static TechneModelLoader.TechneModelPart WOODEN_SHAFT;
    public static TechneModelLoader.TechneModelPart CRUSHING_TABLE;
    public static TechneModelLoader.TechneModelPart SIEVE_TABLE;
    public static TechneModelLoader.TechneModelPart WIND_TURBINE;
    public static TechneModelLoader.TechneModelPart WIND_TURBINE_ITEM;
    public static TechneModelLoader.TechneModelPart HAND_CRANK;
    public static TechneModelLoader.TechneModelPart HAND_SIEVE;
    public static TechneModelLoader.TechneModelPart KINETIC_GRINDER;
    public static TechneModelLoader.TechneModelPart ORE_WASHER;

    public static void loadModels() {
        WOODEN_SHAFT = ofTechne("wooden_shaft");
        CRUSHING_TABLE = ofTechne("crushing_table");
        SIEVE_TABLE = ofTechne("table_sieve");
        WIND_TURBINE = ofTechne("wind_turbine");
        WIND_TURBINE_ITEM = ofTechne("wind_turbine_item");
        HAND_CRANK = ofTechne("hand_crank");
        HAND_SIEVE = ofTechne("hand_sieve");
        KINETIC_GRINDER = ofTechne("kinetic_grinder");
        ORE_WASHER = ofTechne("ore_washer");
    }

    public static TechneModelLoader.TechneModelPart ofTechne(String file) {
        return ofTechne(file, file);
    }

    public static TechneModelLoader.TechneModelPart ofTechne(String file, String texture) {
        return TechneModelLoader.loadModel(new ResourceReference(Magneticraft.ID, "models/" + file + ".tcn"), TextureManager.registerTexture(new ResourceReference(Magneticraft.ID, "models/" + texture)));
    }
}