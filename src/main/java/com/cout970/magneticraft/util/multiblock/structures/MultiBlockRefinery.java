package com.cout970.magneticraft.util.multiblock.structures;

import com.cout970.magneticraft.ManagerBlocks;
import com.cout970.magneticraft.util.multiblock.MB_Component;
import com.cout970.magneticraft.util.multiblock.MultiBlock;
import com.cout970.magneticraft.util.multiblock.SimpleComponent;
import net.darkaqua.blacksmith.vectors.Vect3i;

/**
 * Created by cout970 on 15/01/2016.
 */
public class MultiBlockRefinery extends MultiBlock {

    @Override
    public void init() {
//        SimpleComponent r = new SimpleComponent(ManagerBlocks.refinery);
//        SimpleComponent v = new SimpleComponent(ManagerBlocks.refinery_gap);
//        SimpleComponent h = new SimpleComponent(ManagerBlocks.refinery_tank);
//        SimpleComponent b = new SimpleComponent(ManagerBlocks.chassis);
        SimpleComponent r = new SimpleComponent(ManagerBlocks.Chassis.getBlock());
        SimpleComponent v = new SimpleComponent(ManagerBlocks.Chassis.getBlock());
        SimpleComponent h = new SimpleComponent(ManagerBlocks.Chassis.getBlock());
        SimpleComponent b = new SimpleComponent(ManagerBlocks.Chassis.getBlock());

        MB_Component[][][] matrix = {
        //      {{z2,z1,z0}x2,{z2,z1,z0}x1,{z2,z1,z0}x0}y0
                {{b, b, b}, {b, b, b}, {b, b, b}},
                {{v, r, v}, {v, v, v}, {b, h, b}},
                {{v, v, v}, {v, v, v}, {b, b, b}},
                {{v, v, v}, {v, v, v}, {b, h, b}},
                {{v, v, v}, {v, v, v}, {b, b, b}},
                {{v, v, v}, {v, v, v}, {b, h, b}},
                {{v, v, v}, {v, v, v}, {b, b, b}},
                {{v, v, v}, {v, v, v}, {b, h, b}}
        };
        this.matrix = matrix;
        tranlation = new Vect3i(-1, -1, 0);
        size = new Vect3i(matrix.length, matrix[0].length, matrix[0][0].length);
    }

    @Override
    public int getID() {
        return 0;
    }
}
