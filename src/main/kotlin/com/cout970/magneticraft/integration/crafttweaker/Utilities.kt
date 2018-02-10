package com.cout970.magneticraft.integration.crafttweaker

import com.blamejared.mtlib.helpers.InputHelper
import crafttweaker.CraftTweakerAPI
import crafttweaker.api.item.IItemStack
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 2017/08/11.
 */


inline fun ItemStack.ifEmpty(action: () -> Unit) {
    if (this.isEmpty) {
        action()
    }
}

fun applyAction(desc: String, func: () -> Unit) {
    CraftTweakerAPI.apply(Action(func, desc))
}

fun ctLogError(msg: String) {
    CraftTweakerAPI.logError(msg)
}

fun IItemStack.toStack(): ItemStack = InputHelper.toStack(this)