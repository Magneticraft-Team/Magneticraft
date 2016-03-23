package com.cout970.magneticraft.util.multiblock;


import net.darkaqua.blacksmith.util.WorldRef;
import net.darkaqua.blacksmith.vectors.Vect3i;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class MB_Watcher {

    public static void watchStructure(WorldRef ref, IMultiBlockData data, EntityPlayer player) {

        Vect3i size = data.getMultiBlock().getDimensions(data);
        for (int y = 0; y < size.getX(); y++) {
            for (int z = 0; z < size.getY(); z++) {
                for (int x = 0; x < size.getZ(); x++) {
                    MB_Component mut = data.getMultiBlock().getCompoenet(x, y, z);
                    Vect3i rot = new Vect3i(x,y,z);
                    if (!mut.matches(ref, rot, data)) {
                        String s = mut.getErrorMessage(ref, rot, data);
                        player.addChatComponentMessage(new ChatComponentText(s));
                        return;
                    }
                }
            }
        }

        establishStructure(ref, data);
        player.addChatComponentMessage(new ChatComponentText("Successful activation"));//TODO add I18n
    }

    public static void establishStructure(WorldRef ref, IMultiBlockData data) {
        Vect3i size = data.getMultiBlock().getDimensions(data);
        for (int y = 0; y < size.getX(); y++) {
            for (int z = 0; z < size.getY(); z++) {
                for (int x = 0; x < size.getZ(); x++) {
                    MB_Component mut = data.getMultiBlock().getCompoenet(x, y, z);
                    mut.setup(ref, new Vect3i(x, y, z), data);
                }
            }
        }
    }

    public static void destroyStructure(WorldRef ref, IMultiBlockData data) {

        Vect3i size = data.getMultiBlock().getDimensions(data);
        for (int y = 0; y < size.getX(); y++) {
            for (int z = 0; z < size.getY(); z++) {
                for (int x = 0; x < size.getZ(); x++) {
                    MB_Component mut = data.getMultiBlock().getCompoenet(x, y, z);
                    mut.destroy(ref, new Vect3i(x, y, z), data);
                }
            }
        }
    }
}
