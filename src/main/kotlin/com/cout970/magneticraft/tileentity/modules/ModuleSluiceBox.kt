package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipe
import com.cout970.magneticraft.block.core.IOnActivated
import com.cout970.magneticraft.block.core.OnActivatedArgs
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.inventory.set
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.sounds
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.toBlockPos
import net.minecraft.client.Minecraft
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.SoundCategory

/**
 * Created by cout970 on 2017/07/11.
 */
class ModuleSluiceBox(
        val facingGetter: () -> EnumFacing,
        val invModuleInventory: ModuleInventory,
        override val name: String = "module_sluice_box"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer

    companion object {
        @JvmStatic
        val MAX_ITEMS = 10
        @JvmStatic
        val MAX_PROGRESS = 80
    }

    val level: Int get() = invModuleInventory.inventory[0].count
    var progressLeft = 0
    var chainDelay = 0

    override fun onActivated(args: OnActivatedArgs): Boolean {

        val heldItem = args.heldItem
        if (heldItem.isEmpty) return false
        if (heldItem.item == Items.WATER_BUCKET && progressLeft == 0) {
            if (!args.playerIn.isCreative) {
                args.playerIn.setHeldItem(args.hand, heldItem.item.getContainerItem(heldItem))
            }
            activateChain()
        } else if (canAccept(heldItem)) {
            val stack = invModuleInventory.inventory[0]
            if (stack.isEmpty) {
                val extracted = heldItem.splitStack(MAX_ITEMS)
                if (extracted.isNotEmpty) {
                    invModuleInventory.inventory[0] = extracted
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
        val stack = invModuleInventory.inventory[0]
        if (stack.isEmpty) return
        val recipe = getRecipe(stack) ?: return
        val pos = this.pos + facingGetter().toBlockPos()

        if (recipe.primaryOutput.isNotEmpty) {
            world.dropItem(recipe.primaryOutput, pos, false)
        }
        recipe.secondaryOutput.forEach { (item, prob) ->
            if (world.rand.nextFloat() < prob) {
                world.dropItem(item, pos, false)
            }
        }
        invModuleInventory.inventory[0] = ItemStack.EMPTY
        container.sendUpdateToNearPlayers()
    }

    fun playSounds() {
        if (world.isClient) {
            val sound = if (getNextSluice() == null) sounds["water_flow_end"] else sounds["water_flow"]
            world.playSound(Minecraft.getMinecraft().player, pos, sound, SoundCategory.BLOCKS, 1F, 1F)
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