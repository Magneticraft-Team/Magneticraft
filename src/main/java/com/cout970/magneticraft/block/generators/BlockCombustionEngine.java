package com.cout970.magneticraft.block.generators;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.block.BlockModeled;
import com.cout970.magneticraft.tileentity.electric.generators.TileCombustionEngine;
import net.darkaqua.blacksmith.api.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.block.methods.BlockMethod;
import net.darkaqua.blacksmith.api.entity.IPlayer;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3d;
import net.darkaqua.blacksmith.api.util.WorldRef;
import net.darkaqua.blacksmith.api.world.IWorld;

public class BlockCombustionEngine extends BlockModeled implements BlockMethod.OnActivated, IBlockContainerDefinition {
    @Override
    public String getBlockName() {
        return "combustion_engine";
    }


    @Override
    public boolean onActivated(WorldRef worldRef, IBlockData iBlockVariant, IPlayer iPlayer, Direction direction, Vect3d vect3d) {
        if (iPlayer.isSneaking()) {
            return false;
        }
        iPlayer.openGui(Magneticraft.INSTANCE, 0, worldRef);
        return true;
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld iWorld, IBlockData iBlockVariant) {
        return new TileCombustionEngine();
    }
}
