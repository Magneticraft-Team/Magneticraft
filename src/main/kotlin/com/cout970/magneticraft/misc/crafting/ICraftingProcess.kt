package com.cout970.magneticraft.misc.crafting

import net.minecraft.world.World

/**
 * Created by cout970 on 2017/07/01.
 */
interface ICraftingProcess {

    fun craft(world: World)

    fun duration(): Float

    fun canCraft(world: World): Boolean
}