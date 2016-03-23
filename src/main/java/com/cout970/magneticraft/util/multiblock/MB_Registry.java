package com.cout970.magneticraft.util.multiblock;

import com.cout970.magneticraft.util.multiblock.structures.MultiBlockRefinery;

import java.util.ArrayList;
import java.util.List;

public class MB_Registry {

    private static List<MultiBlock> mb = new ArrayList<>();
    public static final MultiBlock MB_REFINERY = new MultiBlockRefinery();

//    public static final int ID_REFINERY = 0;
//    public static final int ID_CRUSHER = 1;
//    public static final int ID_GRINDER = 2;
//    public static final int ID_POLIMERIZER = 3;
//    public static final int ID_TURBINE = 4;
//    public static final int ID_STIRLING = 5;
//    public static final int ID_OIL_DISTILLERY = 6;
//    public static final int ID_GRINDING_MILL = 7;
//    public static final int ID_SIFTER = 8;


    public static void init() {
        mb.add(MB_REFINERY);
//        mb.add(new MultiblockCrusher());
//        mb.add(new MultiblockGrinder());
//        mb.add(new MultiblockPolymerizer());
//        mb.add(new MultiblockTurbine());
//        mb.add(new MultiblockStirlig());
//        mb.add(new MultiblockOilDistillery());
//        mb.add(new MultiblockGrindingMill());
//        mb.add(new MultiblockSifter());

        mb.forEach(MultiBlock::init);
    }


    public static MultiBlock getMBbyID(int i) {
        for (MultiBlock b : mb)
            if (b.getID() == i) return b;
        return null;
    }
}
