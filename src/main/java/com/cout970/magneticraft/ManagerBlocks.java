package com.cout970.magneticraft;

import com.cout970.magneticraft.block.BlockBase;
import com.cout970.magneticraft.block.BlockCrushingTable;
import com.cout970.magneticraft.block.BlockOreBase;
import com.cout970.magneticraft.tileentity.TileCrushingTable;
import net.darkaqua.blacksmith.api.block.IBlock;
import net.darkaqua.blacksmith.api.block.IBlockDefinition;
import net.darkaqua.blacksmith.api.registry.StaticAccess;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.mod.registry.BlockRegistry;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cout970 on 16/12/2015.
 */
public enum ManagerBlocks {

    CopperOre(new BlockOreBase("oreCopper"), "Copper Ore"),
    TungstenOre(new BlockOreBase("oreTungsten"), "Tungsten Ore"),
    CrushingTable(new BlockCrushingTable(), "Crushing Table", TileCrushingTable.class);

    private static List<BlockRegistry.RegisteredBlock> registeredBlocks = new LinkedList<>();
    private BlockBase definition;
    private IBlock block;
    private String identifier;
    private Class<? extends ITileEntityDefinition> tileEntityClass;

    ManagerBlocks(BlockBase def, String englishName){
        definition = def;
        identifier = def.getUnlocalizedName();
        LangHelper.addName("tile."+def.getUnlocalizedName(), englishName);
    }

    ManagerBlocks(BlockBase def, String englishName, Class<? extends ITileEntityDefinition> tile){
        this(def, englishName);
        tileEntityClass = tile;
    }

    public static void initBlocks(){
        for(ManagerBlocks b : ManagerBlocks.values()){
            b.block = StaticAccess.GAME.getBlockRegistry().registerBlockDefinition(b.definition, b.identifier);
            if (b.tileEntityClass != null) {
                StaticAccess.GAME.getTileEntityRegistry().registerTileEntityDefinition(b.tileEntityClass, b.identifier);
            }
        }
    }

    public static void initBlockRenders(){
        for(ManagerBlocks b : ManagerBlocks.values()){
            StaticAccess.GAME.getRenderRegistry().registerBlockModelProvider(b.definition, b.definition.getModelProvider());
        }
    }

    public IBlockDefinition getDefinition() {
        return definition;
    }

    public String getIdentifier() {
        return identifier;
    }

    public IBlock getBlock() {
        return block;
    }

    public static List<BlockRegistry.RegisteredBlock> getRegisteredBlocks() {
        return registeredBlocks;
    }
}
