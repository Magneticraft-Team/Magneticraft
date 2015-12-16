package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.block.VoidBlockModelProvider;
import com.cout970.magneticraft.tileentity.TileCrushingTable;
import net.darkaqua.blacksmith.api.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.block.IBlockVariant;
import net.darkaqua.blacksmith.api.render.model.IBlockModelProvider;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.Cube;
import net.darkaqua.blacksmith.api.world.IWorld;

/**
 * Created by cout970 on 16/12/2015.
 */
public class BlockCrushingTable extends BlockBase implements IBlockContainerDefinition{

    @Override
    public boolean shouldRender() {
        return false;
    }

    @Override
    public String getBlockName() {
        return "crushing_table";
    }

    @Override
    public float getLightOpacity() {
        return 0f;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public Cube getBounds() {
        return new Cube(0, 0, 0, 1, 0.875f, 1);
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld world, IBlockVariant state) {
        return new TileCrushingTable();
    }

    public IBlockModelProvider getModelProvider(){
        return new VoidBlockModelProvider();
    }
}
