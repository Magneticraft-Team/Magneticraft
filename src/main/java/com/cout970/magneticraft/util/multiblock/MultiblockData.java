package com.cout970.magneticraft.util.multiblock;

import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3i;

/**
 * Created by cout970 on 15/01/2016.
 */
public class MultiblockData implements IMultiBlockData {

    private Vect3i pos;
    private Direction dir;
    private MultiBlock multiBlock;

    public MultiblockData(Vect3i pos, Direction dir, MultiBlock multiBlock) {
        this.pos = pos;
        this.dir = dir;
        this.multiBlock = multiBlock;
    }

    public static IMultiBlockData load(IDataCompound data) {
        if (!data.containsKey("multiblock")) return null;
        Vect3i position = new Vect3i(data.getDataCompound("position"));
        Direction direction = Direction.getDirection(data.getInteger("direction"));
        MultiBlock multiblock = MB_Registry.getMBbyID(data.getInteger("multiblock"));
        return new MultiblockData(position, direction, multiblock);
    }

    public static void save(IDataCompound data, IMultiBlockData mb) {
        if (mb == null) return;
        data.setDataElement("position", mb.getControlBlock().save());
        data.setInteger("direction", mb.getDirection().ordinal());
        data.setInteger("multiblock", mb.getMultiBlock().getID());
    }

    @Override
    public Vect3i getControlBlock() {
        return pos;
    }

    @Override
    public Direction getDirection() {
        return dir;
    }

    @Override
    public MultiBlock getMultiBlock() {
        return multiBlock;
    }
}
