package com.cout970.magneticraft;

import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.registry.StaticAccess;

import java.util.List;

/**
 * Created by cout970 on 30/01/2016.
 */
public class ManagerOreDict {

    public static void registerOredictNames(){
        register("oreCopper", ManagerBlocks.CopperOre);
        register("oreTungsten", ManagerBlocks.TungstenOre);
    }

    private static void register(String name, ManagerBlocks block) {
        StaticAccess.GAME.getOreDictionary().registerOre(name, block.toItemStack());
    }

    public static IItemStack getOreWithPriority(String name){
        List<IItemStack> stacks = StaticAccess.GAME.getOreDictionary().getOres(name);
        if (stacks.isEmpty()){
            return null;
        }
        for(IItemStack stack : stacks){
            if (Magneticraft.ID.equals(StaticAccess.GAME.getItemRegistry().getItemDomain(stack.getItem()))){
                return stack.copy();
            }
        }
        return stacks.get(0).copy();
    }

    public static IItemStack getOre(String name){
        List<IItemStack> stacks = StaticAccess.GAME.getOreDictionary().getOres(name);
        if (stacks.isEmpty()){
            return null;
        }
        return stacks.get(0).copy();
    }
}
