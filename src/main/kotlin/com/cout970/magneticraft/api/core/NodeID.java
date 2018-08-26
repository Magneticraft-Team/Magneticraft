package com.cout970.magneticraft.api.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by cout970 on 2017/06/12.
 */
public final class NodeID implements INBTSerializable<NBTTagCompound> {

    private final String name;
    private final BlockPos pos;
    private final int dimension;

    public NodeID(String name, BlockPos pos, int dimension) {
        this.name = name;
        this.pos = pos;
        this.dimension = dimension;
    }

    public NodeID(String name, BlockPos pos, World world) {
        this.name = name;
        this.pos = pos;
        this.dimension = world.provider.getDimension();
    }

    public String getName() {
        return name;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("name", name);
        nbt.setInteger("posX", pos.getX());
        nbt.setInteger("posY", pos.getY());
        nbt.setInteger("posZ", pos.getZ());
        nbt.setInteger("dimension", dimension);
        return nbt;
    }

    public static NodeID deserializeFromNBT(NBTTagCompound nbt) {
        String name = nbt.getString("name");
        int dimension = nbt.getInteger("dimension");
        int posX = nbt.getInteger("posX");
        int posY = nbt.getInteger("posY");
        int posZ = nbt.getInteger("posZ");

        return new NodeID(name, new BlockPos(posX, posY, posZ), dimension);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        throw new IllegalStateException("Immutable class cannot be loaded from NTB, use the static method instead");
    }

    @Override
    public String toString() {
        return "NodeID{" +
            "name='" + name + '\'' +
            ", pos=" + pos +
            ", dimension=" + dimension +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeID)) {
            return false;
        }

        NodeID nodeID = (NodeID) o;

        if (dimension != nodeID.dimension) {
            return false;
        }
        if (name != null ? !name.equals(nodeID.name) : nodeID.name != null) {
            return false;
        }
        return pos != null ? pos.equals(nodeID.pos) : nodeID.pos == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (pos != null ? pos.hashCode() : 0);
        result = 31 * result + dimension;
        return result;
    }
}
