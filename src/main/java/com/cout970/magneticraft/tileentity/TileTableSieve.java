package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.api.access.RecipeRegister;
import com.cout970.magneticraft.api.access.RecipeTableSieve;
import com.cout970.magneticraft.util.MiscUtils;
import net.darkaqua.blacksmith.api.block.Blocks;
import net.darkaqua.blacksmith.api.entity.IEntity;
import net.darkaqua.blacksmith.api.entity.types.IEntityItem;
import net.darkaqua.blacksmith.api.inventory.IInventoryHandler;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.inventory.InventoryUtils;
import net.darkaqua.blacksmith.api.inventory.defaults.SimpleInventoryHandler;
import net.darkaqua.blacksmith.api.registry.StaticAccess;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.*;

import java.util.List;

/**
 * Created by cout970 on 28/12/2015.
 */
public class TileTableSieve extends TileBase {

    private SimpleInventoryHandler inv = new SimpleInventoryHandler(1, 1);
    private int progress;
    private static final int MAX_PROGRESS = 50;
    private boolean blocked;

    public void update() {

        if (blocked) {
            if (getWorld().getWorldTime() % 20 == 0) {
                blocked = !ejectItem(getRecipeOutput(inv.getStackInSlot(0)));
            }
            if (!blocked) {
                inv.setStackInSlot(0, null);
            }
        } else {
            if (inv.getStackInSlot(0) == null) {
                if (getWorld().getWorldTime() % 20 == 0) {
                    suckUpItems();
                }
            } else if (hasValidRecipe(inv.getStackInSlot(0))) {
                progress++;
                if (progress >= MAX_PROGRESS) {
                    progress = 0;
                    blocked = true;
                }
            }
        }
    }

    private boolean ejectItem(IItemStack stack) {
        Vect3i down = getParent().getWorldRef().getPosition().down();
        if (getWorld().getBlockData(down).getBlock().equals(Blocks.AIR.getBlock())) {
            if (StaticAccess.GAME.isServer()) {
                MiscUtils.dropItem(getParent().getWorldRef(), new Vect3d(0.5, -0.5, 0.5), stack);
            }
            return true;
        } else {
            ITileEntity tile = getWorld().getTileEntity(down);
            IInventoryHandler inventory = ObjectScanner.findInTileEntity(tile, IInventoryHandler.IDENTIFIER, Direction.UP);
            if (inventory != null && InventoryUtils.insertAllInInventory(inventory, stack)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasValidRecipe(IItemStack stack) {
        return RecipeRegister.getTableSieveRecipe(stack) != null;
    }

    private IItemStack getRecipeOutput(IItemStack stack) {
        RecipeTableSieve r = RecipeRegister.getTableSieveRecipe(stack);
        return r == null ? null : r.getOutput();
    }

    private void suckUpItems() {
        List<IEntity> list = getWorld().getEntitiesInsideCube(Cube.fullBlock().translate(getParent().getWorldRef().getPosition().toVect3d().add(0, 1, 0)));
        for (IEntity e : list) {
            if (e instanceof IEntityItem) {
                IItemStack stack = ((IEntityItem) e).getItemStack();
                if (hasValidRecipe(stack) && InventoryUtils.canInsertSomething(inv, 0, stack)) {
                    IItemStack output = inv.insertItemStack(0, stack, false);
                    if (output == null || output.getAmount() <= 0) {
                        e.setDead();
                    } else {
                        ((IEntityItem) e).setItemStack(output);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void loadData(IDataCompound data){
        inv.load(data, "inv");
    }

    @Override
    public void saveData(IDataCompound data){
        inv.save(data, "inv");
    }

    @Override
    public void onDelete(){
        super.onDelete();
        if (StaticAccess.GAME.isServer()){
            MiscUtils.dropItem(getParent().getWorldRef(), new Vect3d(0.5, 0.5, 0.5), inv.getStackInSlot(0));
            inv.setStackInSlot(0, null);
        }
    }
}
