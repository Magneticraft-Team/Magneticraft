package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.api.access.RecipeCrushingTable;
import com.cout970.magneticraft.tileentity.base.TileBase;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.intermod.IInterfaceProvider;
import net.darkaqua.blacksmith.api.inventory.IInventoryHandler;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.inventory.InventoryUtils;
import net.darkaqua.blacksmith.api.inventory.defaults.SimpleInventoryHandler;
import net.darkaqua.blacksmith.api.item.IItemBlock;
import net.darkaqua.blacksmith.api.registry.IParticleManager;
import net.darkaqua.blacksmith.api.registry.StaticAccess;
import net.darkaqua.blacksmith.api.render.particle.IParticle;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3d;

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
        if (getContent() == null) return;
        IParticleManager man = StaticAccess.GAME.getParticleManager();
        IParticle part;
        if (getContent().getItem() instanceof IItemBlock){
            part = man.getBlockCrackParticle(((IItemBlock) getContent().getItem()).getBlock().getDefaultBlockData());
        }else{
            part = man.getItemCrackParticle(getContent());
        }
        for (int i = 0; i < 20; i++) {
            man.addParticle(getWorld(), part, getPosition().toVect3d().add(0.5, 0.8, 0.5), Vect3d.randomVector().multiply(1 / 32d));
        }
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
