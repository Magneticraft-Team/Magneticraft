package com.cout970.magneticraft;

import com.cout970.magneticraft.block.BlockCrushingTable;
import net.darkaqua.blacksmith.api.block.IBlockDefinition;
import net.darkaqua.blacksmith.api.registry.StaticAccess;
import net.darkaqua.blacksmith.mod.registry.BlockRegistry;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cout970 on 16/12/2015.
 */
public enum ManagerBlocks {

    CrushingTable(new BlockCrushingTable());

    private static List<BlockRegistry.RegisteredBlock> registeredBlocks = new LinkedList<>();
    private IBlockDefinition definition;
    private String identifier;

    ManagerBlocks(IBlockDefinition def){
        definition = def;
        identifier = def.getUnlocalizedName();
    }

    public static void initBlocks(){
        for(ManagerBlocks b : ManagerBlocks.values()){
            StaticAccess.GAME.getBlockRegistry().registerBlockDefinition(b.definition, b.identifier);
        }
    }

    public IBlockDefinition getDefinition() {
        return definition;
    }

    public String getIdentifier() {
        return identifier;
    }

    public static List<BlockRegistry.RegisteredBlock> getRegisteredBlocks() {
        return registeredBlocks;
    }
}
