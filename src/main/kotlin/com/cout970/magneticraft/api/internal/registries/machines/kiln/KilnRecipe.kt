package com.cout970.magneticraft.api.internal.registries.machines.kiln

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipe
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by Yurgen on 22/08/2016.
 */
data class KilnRecipe(
    private val input: ItemStack,
    private val itemOutput: ItemStack,
    private val blockOutput: IBlockState?,
    private val duration: Int,
    private val minTemp: Double,
    private val maxTemp: Double,
    private val oreDict: Boolean
) : IKilnRecipe {

    override fun getInput(): ItemStack = input.copy()

    override fun getItemOutput(): ItemStack = itemOutput.copy()
    override fun getBlockOutput(): IBlockState? = blockOutput
    override fun getBlockOutputAsItem(): ItemStack =
        if (blockOutput == null) ItemStack.EMPTY else ItemStack(Item.getItemFromBlock(blockOutput.block), 1, blockOutput.block.damageDropped(blockOutput))

    override fun isItemRecipe(): Boolean = itemOutput.isNotEmpty
    override fun isBlockRecipe(): Boolean = blockOutput != null

    override fun getDuration(): Int = duration

    override fun getMaxTemp(): Double = maxTemp

    override fun getMinTemp(): Double = minTemp

    override fun matches(input: ItemStack): Boolean {
        if (ApiUtils.equalsIgnoreSize(input, this.input)) return true
        if (oreDict) {
            val ids = OreDictionary.getOreIDs(this.input)
            return OreDictionary.getOreIDs(input).any { it in ids }
        }
        return false
    }
}