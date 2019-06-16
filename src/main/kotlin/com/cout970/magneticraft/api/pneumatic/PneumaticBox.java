package com.cout970.magneticraft.api.pneumatic;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Box traveling through the tube system, contains a item and additional metadata
 */
public class PneumaticBox implements INBTSerializable<NBTTagCompound> {

    public static final int MAX_PROGRESS = 128;

    /**
     * The item being transported
     */
    private ItemStack item;
    /**
     * The position of the box, encoded as the value between 0 and MAX_PROGRESS and the side of the tube where it is
     */
    private int progress;
    /**
     * The side of the pneumatic tube where this box is located
     */
    private EnumFacing side;
    /**
     * Stores if the side indicates the input side to the tube, or the output side
     */
    private boolean output;
    /**
     * Indicates if this item has a route or should be dropped immediately
     */
    private boolean inRoute;
    /**
     * Last mode of this item
     */
    private PneumaticMode mode;

    public PneumaticBox() {
        item = ItemStack.EMPTY;
        progress = 0;
        side = EnumFacing.UP;
        output = false;
        inRoute = true;
        mode = PneumaticMode.TRAVELING;
    }

    public PneumaticBox(ItemStack item) {
        this();
        this.item = item;
    }

    public PneumaticBox(NBTTagCompound nbt) {
        this();
        deserializeNBT(nbt);
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public EnumFacing getSide() {
        return side;
    }

    public void setSide(EnumFacing side) {
        this.side = side;
    }

    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }

    public boolean hasRoute() {
        return inRoute;
    }

    public void setInRoute(boolean inRoute) {
        this.inRoute = inRoute;
    }

    public PneumaticMode getMode() {
        return mode;
    }

    public void setMode(PneumaticMode mode) {
        this.mode = mode;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("item", item.serializeNBT());
        nbt.setInteger("progress", progress);
        nbt.setInteger("side", side.ordinal());
        nbt.setBoolean("output", output);
        nbt.setInteger("mode", mode.ordinal());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        item = new ItemStack(nbt.getCompoundTag("item"));
        progress = nbt.getInteger("progress");
        side = EnumFacing.getFront(nbt.getInteger("side"));
        output = nbt.getBoolean("output");
        mode = PneumaticMode.values()[nbt.getInteger("mode")];
    }
}
