package com.cout970.magneticraft.client.model;

import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.api.render.model.IModelPart;
import net.darkaqua.blacksmith.api.render.techne.TechneModelLoader;
import net.darkaqua.blacksmith.api.util.ResourceReference;

/**
 * Created by cout970 on 16/12/2015.
 */
public class ModelConstants {

    private static final String DOMAIN = Magneticraft.ID;

    public static final String WOODEN_SHAFT = "wooden_shaft";
    public static final String CRUSHING_TABLE = "crushing_table";
    public static final String SIEVE_TABLE = "table_sieve";
    public static final String WIND_TURBINE = "wind_turbine";
    public static final String HAND_CRANK = "hand_crank";

    public static ResourceReference ofTexture(String s) {
        return new ResourceReference(DOMAIN, "textures/models/" + s + ".png");
    }

    public static IModelPart ofTechne(String file) {
        return ofTechne(file, file);
    }

    public static IModelPart ofTechne(String file, String texture) {
        return TechneModelLoader.loadModel(new ResourceReference(DOMAIN, "models/" + file + ".tcn"), new ResourceReference(DOMAIN, "models/" + texture));
    }


}