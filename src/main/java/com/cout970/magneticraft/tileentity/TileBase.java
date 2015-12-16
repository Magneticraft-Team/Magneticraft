package com.cout970.magneticraft.tileentity;

import net.darkaqua.blacksmith.api.block.IBlockVariant;
import net.darkaqua.blacksmith.api.network.packet.IDescriptionPacket;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.Cube;
import net.darkaqua.blacksmith.api.util.WorldRef;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileBase implements ITileEntityDefinition {

    protected ITileEntity parent;

    @Override
    public void update() {

    }

    @Override
    public void onLoad(ITileEntity parent) {
        this.parent = parent;
    }

    @Override
    public void loadData(IDataCompound tag) {}

    @Override
    public void saveData(IDataCompound tag) {}

    @Override
    public IDescriptionPacket getDescriptionPacket() {
        return null;
    }

    @Override
    public void onDescriptionPacketArrives(IDescriptionPacket packet) {}

    @Override
    public void onChunkUnload() {}

    @Override
    public boolean shouldRecreate(WorldRef ref, IBlockVariant oldState, IBlockVariant newSate) {
        return false;
    }

    @Override
    public void onBlockChange() {}

    @Override
    public void onClientDataArrive(int id, int data) {}

    @Override
    public double getRenderDistance() {
        return 64;
    }

    @Override
    public Cube getRenderBox() {
        return Cube.fullBlock().translate(parent.getWorldRef().getPosition().toVector3d());
    }
}
