package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.api.access.RecipeCrushingTable;
import com.cout970.magneticraft.tileentity.base.TileBase;
import net.darkaqua.blacksmith.api.inventory.IInventoryHandler;
import net.darkaqua.blacksmith.api.inventory.IInventoryProvider;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.inventory.defaults.SimpleInventoryHandler;
import net.darkaqua.blacksmith.api.registry.StaticAccess;
import net.darkaqua.blacksmith.api.storage.IDataCompound;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileCrushingTable extends TileBase implements IInventoryProvider {

    private SimpleInventoryHandler inventory;
    private int maxProgress;
    private int progress;

    public TileCrushingTable() {
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
        return (inventory.getStackInSlot(null, 0) != null) &&
                (RecipeCrushingTable.getRecipe(inventory.getStackInSlot(null, 0)) != null);
    }

    public IItemStack getOutput() {
        RecipeCrushingTable rec = RecipeCrushingTable.getRecipe(inventory.getStackInSlot(null, 0));
        if (rec == null)
            return null;
        return rec.getOutput().copy();
    }

    public void tick(int maxHits) {
        maxProgress = maxHits;
        progress++;
        if (StaticAccess.GAME.isClient()) {
            addParticles();
        }
        if (progress >= maxProgress) {
            maxProgress = 0;
            progress = 0;
            inventory.setStackInSlot(null, 0, getOutput().copy());
        }
    }

    private void addParticles() {

    }
}
