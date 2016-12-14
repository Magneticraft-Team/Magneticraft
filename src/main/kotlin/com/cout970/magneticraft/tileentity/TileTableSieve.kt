package com.cout970.magneticraft.tileentity

import coffee.cypher.mcextlib.extensions.inventories.get
import com.cout970.magneticraft.api.internal.registries.machines.tablesieve.TableSieveRecipeManager
import com.cout970.magneticraft.api.registries.machines.tablesieve.ITableSieveRecipe
import com.cout970.magneticraft.util.itemInputHelper
import com.cout970.magneticraft.util.itemOutputHelper
import com.cout970.magneticraft.util.shouldTick
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.items.ItemStackHandler
import java.util.*

/**
 * Created by cout970 on 16/06/2016.
 */
class TileTableSieve : TileBase(), ITickable {

    val inventory = ItemStackHandler(1)
    val craftingTime = 40
    var progress = 0
    var size = 0


    companion object {
        val UPDATE_TIME = 20
    }

    private var recipeCache: ITableSieveRecipe? = null
    private var inputCache: ItemStack? = null

    fun getRecipe(input: ItemStack): ITableSieveRecipe? {
        if (input === inputCache) return recipeCache
        val recipe = TableSieveRecipeManager.findRecipe(input)
        if (recipe != null) {
            recipeCache = recipe
            inputCache = input
        }
        return recipe
    }

    override fun update() {
        //gets the item on top of the block
        val inputHelper = itemInputHelper(world, AxisAlignedBB(pos, pos.up().add(1.0, 1.0, 1.0)), inventory)
        if (shouldTick(UPDATE_TIME)) {
            inputHelper.suckItems()
        }

        if (inventory[0] != null) {
            //waits until the item crafting is done
            if (progress > craftingTime) {
                progress = 0
                craftItem()
            } else {
                progress++
            }
        }
    }

    fun craftItem() {
        val outputHelper = itemOutputHelper(world, pos, vec3Of(0, -1, 0))
        val stack = inventory[0]!!
        val recipe = getRecipe(stack) ?: return
        if (stack.stackSize < recipe.input.stackSize) return
        val primary = recipe.primaryOutput
        val secondary = if (Random().nextFloat() < recipe.probability) recipe.secondaryOutput else null
        if (outputHelper.ejectItems(primary, true) == null && outputHelper.ejectItems(secondary, true) == null) {
            outputHelper.ejectItems(primary, false)
            outputHelper.ejectItems(secondary, false)
            stack.stackSize -= recipe.input.stackSize
            if (stack.stackSize <= 0) {
                inventory.setStackInSlot(0, null)
            }
        }
    }

    override fun save(): NBTTagCompound =
            NBTTagCompound().apply { setTag("inventory", inventory.serializeNBT()) }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
    }

    override fun onBreak() {
        super.onBreak()
        if (!worldObj.isRemote) {
            if (inventory[0] != null) {
                dropItem(inventory[0]!!, pos)
            }
        }
    }
}