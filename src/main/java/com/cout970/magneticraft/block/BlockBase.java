package com.cout970.magneticraft.block;

import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.api.block.Blocks;
import net.darkaqua.blacksmith.api.block.IBlock;
import net.darkaqua.blacksmith.api.block.IBlockDefinition;
import net.darkaqua.blacksmith.api.creativetab.CreativeTabFactory;
import net.darkaqua.blacksmith.api.creativetab.ICreativeTab;
import net.darkaqua.blacksmith.api.util.Cube;

/**
 * Created by cout970 on 16/12/2015.
 */
public class BlockBase implements IBlockDefinition {

    public static ICreativeTab MAIN_CREATIVE_TAB = CreativeTabFactory.createCreativeTab(Magneticraft.NAME+" Main", Blocks.IRON_BLOCK.newItemStack(1));
    protected IBlock parent;
    protected String blockName;

    @Override
    public void onCreate(IBlock block) {
        parent = block;
    }

    @Override
    public String getUnlocalizedName() {
        return blockName;
    }

    @Override
    public Cube getBounds() {
        return Cube.fullBlock();
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getLightEmitted() {
        return 0;
    }

    @Override
    public float getLightOpacity() {
        return 1f;
    }

    @Override
    public float getResistance() {
        return 10f;
    }

    @Override
    public ICreativeTab getCreativeTab() {
        return MAIN_CREATIVE_TAB;
    }
}
