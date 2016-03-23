package com.cout970.magneticraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

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
        OreDictionary.registerOre(name, block.getBlock());
    }

    public static ItemStack getOreWithPriority(String name){

        List<ItemStack> stacks = OreDictionary.getOres(name);
        if (stacks.isEmpty()){
            return null;
        }
        for(ItemStack stack : stacks){
            if (Magneticraft.ID.equals(GameRegistry.findUniqueIdentifierFor(stack.getItem()).modId)){
                return stack.copy();
            }
        }
        return stacks.get(0).copy();
    }

    public static ItemStack getOre(String name){
        List<ItemStack> stacks = OreDictionary.getOres(name);
        if (stacks.isEmpty()){
            return null;
        }
        return stacks.get(0).copy();
    }
}
