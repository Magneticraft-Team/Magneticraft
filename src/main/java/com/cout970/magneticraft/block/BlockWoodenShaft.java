package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.TileWoodenShaft;
import net.darkaqua.blacksmith.modelloader.techne.TechneModelFactory;
import net.darkaqua.blacksmith.raytrace.Cube;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.darkaqua.blacksmith.util.WorldRef;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cout970 on 28/12/2015.
 */
public class BlockWoodenShaft extends BlockModeled implements ITileEntityProvider {

    @Override
    public String getBlockName() {
        return "wooden_shaft";
    }

    @Override
    public Cube getSelectionCube(WorldRef ref) {
        Cube cube = null;
        MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
        Vect3d hit = new Vect3d(mop.hitVec).sub(ref.getPosition().toVect3d());
        for (Cube b : getCollisionCubes(ref, null)) {
            if (b.isHit(hit)) {
                cube = b;
                break;
            }
        }
        if (cube == null) { return getBounds(ref); }
        return cube;
    }

    @Override
    public List<Cube> getCollisionCubes(WorldRef ref, Entity entity) {
        List<Cube> boxes = new LinkedList<>();
        boxes.add(getBounds(ref));
        TileEntity t = ref.getTileEntity();
        if (t instanceof TileWoodenShaft) {
            int conn = ((TileWoodenShaft) t).getConnections();
            double p = 0.0625;
            if ((conn & 1) > 0) { boxes.add(new Cube(6 * p, 0, 6 * p, 1 - 6 * p, 1 - 6 * p, 1 - 6 * p)); }
            if ((conn & 2) > 0) { boxes.add(new Cube(6 * p, 6 * p, 6 * p, 1 - 6 * p, 1, 1 - 6 * p)); }
            if ((conn & 4) > 0) { boxes.add(new Cube(6 * p, 6 * p, 0, 1 - 6 * p, 1 - 6 * p, 1 - 6 * p)); }
            if ((conn & 8) > 0) { boxes.add(new Cube(6 * p, 6 * p, 6 * p, 1 - 6 * p, 1 - 6 * p, 1)); }
            if ((conn & 16) > 0) { boxes.add(new Cube(0, 6 * p, 6 * p, 1 - 6 * p, 1 - 6 * p, 1 - 6 * p)); }
            if ((conn & 32) > 0) { boxes.add(new Cube(6 * p, 6 * p, 6 * p, 1, 1 - 6 * p, 1 - 6 * p)); }
        }
        return boxes;
    }

    @Override
    public Cube getBounds(IBlockAccess access, BlockPos pos, IBlockState state) {
        double w = 4d / 16d;
        return new Cube(0.5 - w, 0.5 - w, 0.5 - w, 0.5 + w, 0.5 + w, 0.5 + w);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileWoodenShaft();
    }

    @Override
    public IModelFactory getModelFactory() {
        return new TechneModelFactory(ModelConstants.WOODEN_SHAFT);
    }
}
