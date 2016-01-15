package com.cout970.magneticraft.util.multiblock;

import net.darkaqua.blacksmith.api.command.ChatMessageFactory;
import net.darkaqua.blacksmith.api.entity.IPlayer;
import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.util.WorldRef;

public class MB_Watcher {

    public static void watchStructure(WorldRef ref, IMultiBlockData data, IPlayer player) {

        Vect3i size = data.getMultiBlock().getDimensions(data);
        for (int y = 0; y < size.getX(); y++) {
            for (int z = 0; z < size.getY(); z++) {
                for (int x = 0; x < size.getZ(); x++) {
                    MB_Component mut = data.getMultiBlock().getCompoenet(x, y, z);
                    Vect3i rot = new Vect3i(x,y,z);
                    if (!mut.matches(ref, rot, data)) {
                        String s = mut.getErrorMessage(ref, rot, data);
                        player.sendChatMessage(ChatMessageFactory.createChatMessage(s));
                        return;
                    }
                }
            }
        }

        establishStructure(ref, data);
        player.sendChatMessage(ChatMessageFactory.createChatMessage("Successful activation"));
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
