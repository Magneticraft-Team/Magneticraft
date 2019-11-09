package com.cout970.magneticraft.api.pneumatic;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Box traveling through the tube system, contains a item and additional metadata
 */
public class PneumaticBox implements INBTSerializable<CompoundNBT> {

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
    private Direction side;
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
        side = Direction.UP;
        output = false;
        inRoute = true;
        mode = PneumaticMode.TRAVELING;
    }

    public PneumaticBox(ItemStack item) {
        this();
        this.item = item;
    }

    public PneumaticBox(CompoundNBT nbt) {
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

    public Direction getSide() {
        return side;
    }

    public void setSide(Direction side) {
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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("item", item.serializeNBT());
        nbt.putInt("progress", progress);
        nbt.putInt("side", side.ordinal());
        nbt.putBoolean("output", output);
        nbt.putInt("mode", mode.ordinal());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        item = ItemStack.read(nbt.getCompound("item"));
        progress = nbt.getInt("progress");
        side = Direction.byIndex(nbt.getInt("side"));
        output = nbt.getBoolean("output");
        mode = PneumaticMode.values()[nbt.getInt("mode")];
    }
}
