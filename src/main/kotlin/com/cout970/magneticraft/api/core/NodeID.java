package com.cout970.magneticraft.api.core;

import java.util.Objects;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by cout970 on 2017/06/12.
 */
public final class NodeID implements INBTSerializable<CompoundNBT> {

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
        this.dimension = world.getDimension().getType().getId();
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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("name", name);
        nbt.putInt("posX", pos.getX());
        nbt.putInt("posY", pos.getY());
        nbt.putInt("posZ", pos.getZ());
        nbt.putInt("dimension", dimension);
        return nbt;
    }

    public static NodeID deserializeFromNBT(CompoundNBT nbt) {
        String name = nbt.getString("name");
        int dimension = nbt.getInt("dimension");
        int posX = nbt.getInt("posX");
        int posY = nbt.getInt("posY");
        int posZ = nbt.getInt("posZ");

        return new NodeID(name, new BlockPos(posX, posY, posZ), dimension);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
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
        if (!Objects.equals(name, nodeID.name)) {
            return false;
        }
        return Objects.equals(pos, nodeID.pos);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (pos != null ? pos.hashCode() : 0);
        result = 31 * result + dimension;
        return result;
    }
}
