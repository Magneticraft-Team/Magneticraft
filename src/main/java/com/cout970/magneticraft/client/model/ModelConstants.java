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

    public static final String TEXTURE_WOODEN_SHAFT = "misc/wooden_shaft";
    public static final String TEXTURE_CRUSHING_TABLE = "misc/crushing_table";
    public static final String TEXTURE_SIEVE_TABLE = "misc/table_sieve";
    public static final String TEXTURE_WIND_TURBINE = "misc/wind_turbine";


    public static final String MODEL_CRUSHING_TABLE = "models/crushing_table.tcn";
    public static final String MODEL_SIEVE_TABLE = "models/table_sieve.tcn";
    public static final String MODEL_WOODEN_SHAFT = "models/wooden_shaft.tcn";


    public static ResourceReference of(String s) {
        return new ResourceReference(DOMAIN, s);
    }

    public static ResourceReference ofTexture(String s) {
        return new ResourceReference(DOMAIN, "textures/" + s + ".png");
    }

    public static IModelPart ofTechne(String file, String texture) {
        return TechneModelLoader.loadModel(of(file), of(texture));
    }
}