package com.cout970.magneticraft.block.generators;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.block.BlockModeled;
import com.cout970.magneticraft.tileentity.electric.generators.TileCombustionEngine;
import net.darkaqua.blacksmith.api.common.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.common.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.common.block.methods.BlockMethod;
import net.darkaqua.blacksmith.api.common.entity.IPlayer;
import net.darkaqua.blacksmith.api.common.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.common.util.Direction;
import net.darkaqua.blacksmith.api.common.util.vectors.Vect3d;
import net.darkaqua.blacksmith.api.common.util.WorldRef;
import net.darkaqua.blacksmith.api.common.world.IWorld;

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
        iPlayer.openGui(Magneticraft.IDENTIFIER, 0, worldRef);
        return true;
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld iWorld, IBlockData iBlockVariant) {
        return new TileCombustionEngine();
    }
}
