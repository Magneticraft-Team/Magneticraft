package com.cout970.magneticraft.tileentity;

import net.darkaqua.blacksmith.api.inventory.IInventoryHandler;
import net.darkaqua.blacksmith.api.inventory.IInventoryProvider;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.inventory.defaults.SimpleInventoryHandler;
import net.darkaqua.blacksmith.api.storage.IDataCompound;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileCrushingTable extends TileBase implements IInventoryProvider{

    private SimpleInventoryHandler inventory;

    public TileCrushingTable(){
        inventory = new SimpleInventoryHandler(1);
    }

    @Override
    public IInventoryHandler getInventory() {
        return inventory;
    }

    @Override
    public void loadData(IDataCompound tag) {
        super.loadData(tag);
        inventory.load(tag, "inv");
    }

    @Override
    public void saveData(IDataCompound tag) {
        super.saveData(tag);
        inventory.save(tag, "inv");
    }

    public IItemStack getContent() {
        return inventory.getStackInSlot(null, 0);
    }

    public void setContent(IItemStack input) {
        inventory.setStackInSlot(null, 0, input);
    }

    public boolean canWork() {
        //TODO make it work
        return true;
    }

    public void tick(int maxHits) {
        //TODO make it work
    }
}
