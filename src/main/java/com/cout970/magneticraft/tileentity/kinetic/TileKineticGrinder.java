package com.cout970.magneticraft.tileentity.kinetic;

import com.cout970.magneticraft.api.access.RecipeCrushingTable;
import com.cout970.magneticraft.api.access.RecipeRegister;
import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.util.MiscUtils;
import net.darkaqua.blacksmith.api.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.block.blockdata.defaults.BlockAttributeValueDirection;
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
 * Created by cout970 on 20/01/2016.
 */
public class TileKineticGrinder extends TileKineticBase {

    private SimpleInventoryHandler inv = new SimpleInventoryHandler(1, 1);
    private float progress;
    private static final int MAX_PROGRESS = 50;
    private boolean blocked;

    public void update() {
        super.update();
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
                double speed = getNetwork().getSpeed();
                progress += speed * 0.005d;

                if (progress >= MAX_PROGRESS) {
                    if (progress >= MAX_PROGRESS * 2) {
                        progress = 0;
                    } else {
                        progress -= MAX_PROGRESS;
                    }
                    blocked = true;
                }
            }
        }
    }

    @Override
    public double getLoss() {
        double loss = 0;
        if (getNetwork().getSpeed() > 400) {
            double extra = (getNetwork().getSpeed() - 400);
            loss = super.getLoss() + extra * extra * 0.015;
        } else {
            loss = super.getLoss();
        }
        if (hasValidRecipe(inv.getStackInSlot(0))) {
            loss += getNetwork().getSpeed() * 0.1;
        }
        return loss;
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

    private boolean ejectItem(IItemStack stack) {
        if (stack == null) return true;
        WorldRef ref = new WorldRef(getWorld(), getParent().getWorldRef().getPosition().down());
        ITileEntity tile = ref.getTileEntity();
        IInventoryHandler inventory = ObjectScanner.findInTileEntity(tile, IInventoryHandler.IDENTIFIER, Direction.UP);
        if (inventory != null && InventoryUtils.insertAllInInventory(inventory, stack)) {
            return true;
        }
        if (ref.getBlockData().getBlock().isPassable(ref)) {
            if (StaticAccess.GAME.isServer()) {
                MiscUtils.dropItem(getParent().getWorldRef(), new Vect3d(0.5, -0.0625f * 6, 0.5), stack, false);
            }
            return true;
        }
        return false;
    }

    private boolean hasValidRecipe(IItemStack stack) {
        if (stack == null) return false;
        return RecipeRegister.getCrushingTableRecipe(stack) != null;
    }

    private IItemStack getRecipeOutput(IItemStack stack) {
        RecipeCrushingTable r = RecipeRegister.getCrushingTableRecipe(stack);
        return r == null ? null : r.getOutput();
    }

    @Override
    public void loadData(IDataCompound data) {
        super.loadData(data);
        inv.load(data, "inv");
        progress = data.getFloat("progress");
    }

    @Override
    public void saveData(IDataCompound data) {
        super.saveData(data);
        inv.save(data, "inv");
        data.setFloat("progress", progress);
    }

    @Override
    public void onDelete() {
        super.onDelete();
        if (StaticAccess.GAME.isServer()) {
            MiscUtils.dropItem(getParent().getWorldRef(), new Vect3d(0.5, 0.5, 0.5), inv.getStackInSlot(0), true);
            inv.setStackInSlot(0, null);
        }
    }

    @Override
    public boolean isAbleToConnect(IKineticConductor cond, Vect3i offset) {
        return offset.isDirectionalOffset() && getDirection().isParallel(offset.toDirection());
    }

    public Direction getDirection() {
        IBlockData variant = parent.getWorldRef().getBlockData();
        return (Direction) variant.getValue(BlockAttributeValueDirection.HORIZONTAL_DIRECTION).getValue();
    }
}
