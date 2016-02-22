package com.cout970.magneticraft.block;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.TileWoodenShaft;
import net.darkaqua.blacksmith.api.common.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.common.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.client.render.block.IBlockModelProvider;
import net.darkaqua.blacksmith.api.client.render.block.defaults.SimpleBlockModelProvider;
import net.darkaqua.blacksmith.api.client.render.item.defaults.SimpleItemBlockModelProvider;
import net.darkaqua.blacksmith.api.common.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.common.util.raytrace.Cube;
import net.darkaqua.blacksmith.api.common.util.WorldRef;
import net.darkaqua.blacksmith.api.common.world.IWorld;

/**
 * Created by cout970 on 28/12/2015.
 */
public class BlockWoodenShaft extends BlockModeled implements IBlockContainerDefinition {

    @Override
    public String getBlockName() {
        return "wooden_shaft";
    }

    public IBlockModelProvider getModelProvider(){
        return new SimpleItemBlockModelProvider(iModelRegistry -> new SimpleBlockModelProvider.BlockModel(
                iModelRegistry.registerModelPart(Magneticraft.IDENTIFIER, ModelConstants.WOODEN_SHAFT)));

    }

    @Override
    public Cube getBounds(WorldRef ref) {
        double w = 4 / 16d;
        return new Cube(0.5 - w, 0.5 - w, 0.5 - w, 0.5 + w, 0.5 + w, 0.5 + w);
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld world, IBlockData state) {
        return new TileWoodenShaft();
    }
}
