package com.cout970.magneticraft.block;

import com.cout970.magneticraft.api.access.RecipeCrushingTable;
import com.cout970.magneticraft.api.tool.IHammer;
import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.TileCrushingTable;
import net.darkaqua.blacksmith.api.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.block.methods.BlockMethod;
import net.darkaqua.blacksmith.api.entity.IPlayer;
import net.darkaqua.blacksmith.api.inventory.IInventoryHandler;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.render.model.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleBlockModelProvider;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.*;
import net.darkaqua.blacksmith.api.world.IWorld;

/**
 * Created by cout970 on 16/12/2015.
 */
public class BlockCrushingTable extends BlockModeled implements IBlockContainerDefinition, BlockMethod.OnActivated {

    @Override
    public String getBlockName() {
        return "crushing_table";
    }

    @Override
    public Cube getBounds() {
        return new Cube(0, 0, 0, 1, 0.875f, 1);
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld world, IBlockData state) {
        return new TileCrushingTable();
    }

    @Override
    public boolean onActivated(WorldRef ref, IBlockData state, IPlayer p, Direction side, Vect3d vector3d) {
        if (p != null) {
            ITileEntity t = ref.getTileEntity();
            if (!(t.getTileEntityDefinition() instanceof TileCrushingTable)) {
                return true;
            }
            TileCrushingTable tile = (TileCrushingTable) t.getTileEntityDefinition();

            IItemStack i = p.getSelectedItemStack();
            IInventoryHandler playerInv = p.getInventory();
            if (i != null) {
                IHammer hammer = (IHammer) ObjectScanner.findInItem(i.getItem(), IHammer.IDENTIFIER, null);
                if (hammer != null) {
                    if (tile.canWork()) {
                        if (hammer.canHammer(i, ref)) {
                            tile.tick(hammer.getMaxHits(i, ref));
                            IItemStack stack = hammer.tick(i, ref);
                            p.setSelectedItemStack(stack);
                            return true;
                        }
                    } else {
                        if (tile.getContent() == null) {
                            for (int j = 0; j < playerInv.getSlots(null); j++) {
                                IItemStack stack = playerInv.getStackInSlot(null, j);
                                if (stack != null && stack.getAmount() > 0 && RecipeCrushingTable.getRecipe(stack) != null) {
                                    tile.setContent(playerInv.extractItemStack(null, j, 1, false));
                                    t.setModified();
                                    return true;
                                }
                            }
                        } else {
                            boolean inserted = false;
                            for (int j = 0; j < playerInv.getSlots(null); j++) {
                                if (playerInv.insertItemStack(null, j, tile.getContent(), true) == null) {
                                    playerInv.insertItemStack(null, j, tile.getContent(), false);
                                    inserted = true;
                                    break;
                                }
                            }
                            if (inserted) {
                                tile.setContent(null);
                            }
                        }
                        t.setModified();

                        return true;
                    }

                } else if ((tile.getContent() == null) && (i.getAmount() > 0)) {
                    IItemStack split = i.split(1);
                    p.setSelectedItemStack((i.getAmount() > 0) ? i : null);
                    tile.setContent(split);

                    t.setModified();

                    return true;
                }
            }
            if (tile.getContent() != null) {
                boolean inserted = false;
                for (int j = 0; j < playerInv.getSlots(null); j++) {
                    if (playerInv.insertItemStack(null, j, tile.getContent(), true) == null) {
                        playerInv.insertItemStack(null, j, tile.getContent(), false);
                        inserted = true;
                        break;
                    }
                }
                if (inserted) {
                    tile.setContent(null);
                }
            }
            t.setModified();
        }
        return true;
    }

    public IBlockModelProvider getModelProvider() {
        return new SimpleBlockModelProvider(ModelConstants.ofTechne(ModelConstants.CRUSHING_TABLE));
    }
}
