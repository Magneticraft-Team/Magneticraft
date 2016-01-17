package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.api.access.RecipeCrushingTable;
import com.cout970.magneticraft.tileentity.base.TileBase;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.intermod.IInterfaceProvider;
import net.darkaqua.blacksmith.api.inventory.IInventoryHandler;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.inventory.InventoryUtils;
import net.darkaqua.blacksmith.api.inventory.defaults.SimpleInventoryHandler;
import net.darkaqua.blacksmith.api.registry.StaticAccess;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.util.Direction;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileCrushingTable extends TileBase implements IInterfaceProvider{

    private SimpleInventoryHandler inventory;
    private int progress;

    public TileCrushingTable() {
        inventory = new SimpleInventoryHandler(1){
            @Override
            public void setStackInSlot(Direction side, int slot, IItemStack stack) {
                inventory[slot] = stack;
                sendUpdateToClient();
            }
            @Override
            public IItemStack insertItemStack(Direction side, int slot, IItemStack stack, boolean simulated) {
                IItemStack ret = super.insertItemStack(side, slot, stack, simulated);
                if (!simulated && InventoryUtils.areExactlyEqual(ret, stack)){
                    sendUpdateToClient();
                }
                return ret;
            }
        };
    }

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
        progress++;
        if (StaticAccess.GAME.isClient()) {
            addParticles();
        }
        if (progress >= maxHits) {
            progress = 0;
            setContent(getOutput().copy());
        }
    }

    private void addParticles() {

    }

    @Override
    public boolean hasInterface(IInterfaceIdentifier identifier, Direction side) {
        return identifier == IInventoryHandler.IDENTIFIER;
    }

    @Override
    public Object getInterface(IInterfaceIdentifier identifier, Direction side) {
        return identifier == IInventoryHandler.IDENTIFIER ? inventory : null;
    }
}
