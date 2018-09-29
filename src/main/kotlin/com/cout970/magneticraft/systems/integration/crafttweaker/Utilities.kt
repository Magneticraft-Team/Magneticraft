package com.cout970.magneticraft.systems.integration.crafttweaker

import com.blamejared.mtlib.helpers.InputHelper
import crafttweaker.CraftTweakerAPI
import crafttweaker.api.item.IItemStack
import crafttweaker.api.liquid.ILiquidStack
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 2017/08/11.
 */


inline fun ItemStack.ifEmpty(action: () -> Unit) {
    if (this.isEmpty) {
        action()
    }
}

inline fun ItemStack.ifNonEmpty(action: (ItemStack) -> Unit) {
    if (!this.isEmpty) {
        action(this)
    }
}

fun applyAction(desc: String, func: () -> Unit) {
    CraftTweakerAPI.apply(Action(func, desc))
}

fun ctLogError(msg: String) {
    CraftTweakerAPI.logError(msg)
}

fun IItemStack.toStack(): ItemStack = InputHelper.toStack(this)
fun ILiquidStack.toStack(): FluidStack? = InputHelper.toFluid(this)