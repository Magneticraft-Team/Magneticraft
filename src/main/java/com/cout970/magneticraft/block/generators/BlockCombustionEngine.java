package com.cout970.magneticraft.block.generators;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.block.BlockModeled;
import com.cout970.magneticraft.tileentity.electric.generators.TileCombustionEngine;
import com.cout970.magneticraft.util.MiscUtils;
import net.darkaqua.blacksmith.render.ITexture;
import net.darkaqua.blacksmith.render.TextureManager;
import net.darkaqua.blacksmith.render.providers.IBlockModelProvider;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.darkaqua.blacksmith.render.providers.factory.SimpleBlockModelFactory;
import net.darkaqua.blacksmith.render.providers.block.UniqueModelProvider;
import net.darkaqua.blacksmith.util.ResourceReference;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockCombustionEngine extends BlockModeled implements ITileEntityProvider {

    @Override
    public String getBlockName() {
        return "combustion_engine";
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer iPlayer, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (iPlayer.isSneaking()) {
            return false;
        }
        MiscUtils.openGui(0, worldIn, pos, iPlayer);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCombustionEngine();
    }

    @Override
    public IModelFactory getModelFactory() {
        return null;
    }

    @Override
    public IBlockModelProvider getModelProvider() {
        //TODO
        ITexture texture = TextureManager.registerTexture(
                new ResourceReference(Magneticraft.ID, "blocks/" + getBlockName().toLowerCase()));
        IModelFactory factory = new SimpleBlockModelFactory(texture);
        return new UniqueModelProvider(factory);
    }
}
