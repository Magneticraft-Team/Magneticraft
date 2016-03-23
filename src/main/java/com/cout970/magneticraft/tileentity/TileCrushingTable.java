package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.api.access.RecipeCrushingTable;
import com.cout970.magneticraft.api.access.RecipeRegister;
import com.cout970.magneticraft.util.MiscUtils;
import net.darkaqua.blacksmith.Game;
import net.darkaqua.blacksmith.inventory.SimpleInventoryHandler;
import net.darkaqua.blacksmith.storage.IDataCompound;
import net.darkaqua.blacksmith.util.ResourceReference;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.items.IItemHandler;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileCrushingTable extends TileBase {

    private SimpleInventoryHandler inventory;
    private int progress;

    public TileCrushingTable() {
        inventory = new SimpleInventoryHandler(1) {

            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                super.setStackInSlot(slot, stack);
                sendUpdateToClient();
            }
        };
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (Game.isServer()) {
            MiscUtils.dropItem(getWorldRef(), new Vect3d(0.5, 0.5, 0.5), inventory.getStackInSlot(0), true);
            inventory.setStackInSlot(0, null);
        }
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

    public ItemStack getContent() {
        return inventory.getStackInSlot(0);
    }

    public void setContent(ItemStack input) {
        inventory.setStackInSlot(0, input);
    }

    public boolean canWork() {
        return (inventory.getStackInSlot(0) != null) &&
                (RecipeRegister.getCrushingTableRecipe(inventory.getStackInSlot(0)) != null);
    }

    public ItemStack getOutput() {
        RecipeCrushingTable rec = RecipeRegister.getCrushingTableRecipe(inventory.getStackInSlot(0));
        if (rec == null) { return null; }
        return rec.getOutput().copy();
    }

    public void tick(int maxHits) {
        progress++;
        if (Game.isClient()) {
            addParticles();
            if (progress != maxHits) { addHitSound(); }
        }
        if (progress >= maxHits) {
            progress = 0;
            setContent(getOutput().copy());
            addFinalSound();
        }
    }

    private void addHitSound() {
        ResourceReference res = new ResourceReference(Magneticraft.ID, "sounds.crushing_hit");
        Vect3d pos = new Vect3d(getPos()).add(0.5, 0.5, 0.5);
        ISound sound = PositionedSoundRecord.create(res.toResourceLocation(), (float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }

    private void addFinalSound() {
        ResourceReference res = new ResourceReference(Magneticraft.ID, "sounds.crushing_final");
        Vect3d pos = new Vect3d(getPos()).add(0.5, 0.5, 0.5);
        ISound sound = PositionedSoundRecord.create(res.toResourceLocation(), (float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }

    private void addParticles() {
        if (getContent() == null) { return; }
        EffectRenderer eff = Minecraft.getMinecraft().effectRenderer;
        Vect3d pos = new Vect3d(getPos()).add(0.5, 0.5, 0.5);
        for (int i = 0; i < 20; i++) {
            Vect3d motion = Vect3d.randomVector().multiply(1 / 32d);
            EntityFX particle;
            if (getContent().getItem() instanceof ItemBlock) {
                particle = eff.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), pos.getX(), pos.getY(), pos.getZ(),
                        motion.getX(), motion.getY(), motion.getZ(),  Block.getStateId(((ItemBlock) getContent().getItem()).block.getDefaultState()));
            } else {
                particle = eff.spawnEffectParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(), pos.getX(), pos.getY(), pos.getZ(),
                        motion.getX(), motion.getY(), motion.getZ(), Item.getIdFromItem( getContent().getItem()),  getContent().getItemDamage());
            }
            particle.multipleParticleScaleBy(0.5f);
        }
    }
}
