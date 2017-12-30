package com.cout970.magneticraft.misc.crafting

/**
 * Created by cout970 on 2017/07/01.
 */
interface ICraftingProcess {

    fun craft()

    fun duration(): Float

    fun canCraft(): Boolean
}