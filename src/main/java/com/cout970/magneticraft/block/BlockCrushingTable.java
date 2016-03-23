package com.cout970.magneticraft.block;

import com.cout970.magneticraft.ManagerApi;
import com.cout970.magneticraft.api.access.RecipeRegister;
import com.cout970.magneticraft.api.tool.IHammer;
import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.TileCrushingTable;
import net.darkaqua.blacksmith.modelloader.techne.TechneModelFactory;
import net.darkaqua.blacksmith.raytrace.Cube;
import net.darkaqua.blacksmith.render.providers.IBlockModelProvider;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.darkaqua.blacksmith.render.providers.block.UniqueModelProvider;
import net.darkaqua.blacksmith.scanner.ObjectScanner;
import net.darkaqua.blacksmith.util.WorldRef;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

/**
 * Created by cout970 on 16/12/2015.
 */
public class BlockCrushingTable extends BlockModeled implements ITileEntityProvider {

    @Override
    public Cube getBounds(IBlockAccess worldAccess, BlockPos pos, IBlockState state){
        return new Cube(0, 0, 0, 1, 0.875f, 1);
    }

    @Override
    public String getBlockName() {
        return "crushing_table";
    }


    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCrushingTable();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer p, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (p != null) {
            TileEntity t = worldIn.getTileEntity(pos);
            if (!(t instanceof TileCrushingTable)) {
                return true;
            }
            TileCrushingTable tile = (TileCrushingTable) t;

            WorldRef ref = new WorldRef(worldIn, pos);
            ItemStack i = p.getCurrentEquippedItem();
            IItemHandler playerInv = new PlayerMainInvWrapper(p.inventory);
            if (i != null) {
                IHammer hammer = ObjectScanner.findInItem(i.getItem(), ManagerApi.HAMMER, null);
                if (hammer != null) {
                    if (tile.canWork()) {//punch
                        if (hammer.canHammer(i, ref)) {
                            tile.tick(hammer.getMaxHits(i, ref));
                            ItemStack stack = hammer.tick(i, ref);
                            p.setCurrentItemOrArmor(0, stack);
                            return true;
                        }
                    } else {//remove or add content
                        if (tile.getContent() == null) {//add content
                            for (int j = 0; j < playerInv.getSlots(); j++) {
                                ItemStack stack = playerInv.getStackInSlot(j);
                                if (stack != null && stack.stackSize > 0 && RecipeRegister.getCrushingTableRecipe(stack) != null) {
                                    tile.setContent(playerInv.extractItem(j, 1, false));
                                    t.markDirty();
                                    return true;
                                }
                            }
                        } else {//remove content
                            boolean inserted = false;
                            for (int j = 0; j < playerInv.getSlots(); j++) {
                                if (playerInv.insertItem(j, tile.getContent(), true) == null) {
                                    playerInv.insertItem(j, tile.getContent(), false);
                                    inserted = true;
                                    break;
                                }
                            }
                            if (inserted) {
                                tile.setContent(null);
                            }
                        }
                        t.markDirty();

                        return true;
                    }

                } else if ((tile.getContent() == null) && (i.stackSize > 0)) {
                    ItemStack split = i.splitStack(1);
                    p.setCurrentItemOrArmor(0, (i.stackSize > 0) ? i : null);
                    tile.setContent(split);
                    t.markDirty();
                    return true;
                }
            }
            if (tile.getContent() != null) {
                boolean inserted = false;
                for (int j = 0; j < playerInv.getSlots(); j++) {
                    if (playerInv.insertItem(j, tile.getContent(), true) == null) {
                        playerInv.insertItem(j, tile.getContent(), false);
                        inserted = true;
                        break;
                    }
                }
                if (inserted) {
                    tile.setContent(null);
                }
            }
            t.markDirty();
        }
        return true;
    }

    @Override
    public IModelFactory getModelFactory() {
        return new TechneModelFactory(ModelConstants.CRUSHING_TABLE);
    }

    @Override
    public IBlockModelProvider getModelProvider() {
        IModelFactory factory = getModelFactory();
        return new UniqueModelProvider(factory);
    }
}
