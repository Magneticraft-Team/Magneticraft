package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipe
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.fluid.VoidFluidHandler
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.inventory.set
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.Sounds
import com.cout970.magneticraft.systems.blocks.IOnActivated
import com.cout970.magneticraft.systems.blocks.OnActivatedArgs
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.SoundCategory
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil

/**
 * Created by cout970 on 2017/07/11.
 */
class ModuleSluiceBox(
    val facingGetter: () -> EnumFacing,
    val inventory: Inventory,
    override val name: String = "module_sluice_box"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer

    companion object {
        @JvmStatic
        val MAX_ITEMS = 10
        @JvmStatic
        val MAX_PROGRESS = 80
    }

    val level: Int get() = inventory[0].count
    var progressLeft = 0
    var chainDelay = 0

    override fun onActivated(args: OnActivatedArgs): Boolean {

        val heldItem = args.heldItem
        if (heldItem.isEmpty) return false
        if (isWaterContainer(heldItem) && progressLeft == 0) {
            if (!args.playerIn.isCreative) {
                args.playerIn.setHeldItem(args.hand, removeWaterFromContainer(heldItem))
            }
            activateChain()
        } else if (canAccept(heldItem)) {
            val stack = inventory[0]
            if (stack.isEmpty) {
                val extracted = heldItem.splitStack(MAX_ITEMS)
                if (extracted.isNotEmpty) {
                    inventory[0] = extracted
                    container.sendUpdateToNearPlayers()
                }
            } else if (stack.count < MAX_ITEMS && stack.isItemEqual(heldItem)) {
                val extracted = heldItem.splitStack(MAX_ITEMS - stack.count)
                if (extracted.isNotEmpty) {
                    stack.count += extracted.count
                    container.sendUpdateToNearPlayers()
                }
            }
        }
        return true
    }

    fun isWaterContainer(stack: ItemStack): Boolean {
        val fluidStack = FluidUtil.getFluidContained(stack) ?: return false
        return fluidStack.isFluidEqual(FluidStack(FluidRegistry.WATER, 1000))
    }

    fun removeWaterFromContainer(stack: ItemStack): ItemStack {
        val result = FluidUtil.tryEmptyContainer(stack, VoidFluidHandler, 1000, null, true)

        if (result.isSuccess) return result.result
        return stack
    }

    fun activateChain() {
        activate()
        chainDelay = 20
    }

    fun activate() {
        progressLeft = MAX_PROGRESS
        playSounds()
        container.sendUpdateToNearPlayers()
    }

    fun getNextSluice(): ModuleSluiceBox? {
        val otherPos = pos.offset(facingGetter(), 2).down()
        return world.getModule<ModuleSluiceBox>(otherPos)
    }

    override fun update() {
        if (chainDelay > 0) {
            chainDelay--
            if (chainDelay == 0) {
                val mod = getNextSluice() ?: return
                mod.activateChain()
            }
        }
        if (progressLeft > 0) {
            progressLeft--
            if (progressLeft == 0) {
                craft()
            }
        }
    }

    fun craft() {
        val stack = inventory[0]
        if (stack.isEmpty) return
        val recipe = getRecipe(stack) ?: return
        val pos = this.pos + facingGetter().toBlockPos()

        (0 until stack.count).forEach {
            recipe.outputs.forEach { (item, prob) ->
                if (item.isNotEmpty && world.rand.nextFloat() < prob) {
                    world.dropItem(item, pos, false)
                }
            }
        }

        inventory[0] = ItemStack.EMPTY
        container.sendUpdateToNearPlayers()
    }

    fun playSounds() {
        if (world.isClient) {
            val sound = if (getNextSluice() == null) Sounds.WATER_FLOW_END.soundEvent else Sounds.WATER_FLOW.soundEvent
            world.playSound(pos.xd, pos.yd, pos.zd, sound, SoundCategory.BLOCKS, 1F, 1F, false)
        }
    }

    fun getRecipe(stack: ItemStack): ISluiceBoxRecipe? = MagneticraftApi.getSluiceBoxRecipeManager().findRecipe(stack)
    fun canAccept(stack: ItemStack) = getRecipe(stack) != null

    override fun deserializeNBT(nbt: NBTTagCompound) {
        progressLeft = nbt.getInteger("progressLeft")
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("progressLeft", progressLeft)
    }
}