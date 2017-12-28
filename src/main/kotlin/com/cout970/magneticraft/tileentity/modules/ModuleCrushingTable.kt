package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager
import com.cout970.magneticraft.api.tool.IHammer
import com.cout970.magneticraft.block.core.IOnActivated
import com.cout970.magneticraft.block.core.OnActivatedArgs
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.inventory.withSize
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.ITEM_HAMMER
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.registry.sounds
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.vector.*
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.ParticleBreaking
import net.minecraft.client.particle.ParticleDigging
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * Created by cout970 on 2017/06/12.
 */
class ModuleCrushingTable(
        val inventory: Inventory,
        override val name: String = "module_crushing_table"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer

    companion object {
        val CRUSHING_DAMAGE = 40
    }

    var damageTaken = 0

    var lastItem: ItemStack = ItemStack.EMPTY

    var storedItem
        get() = inventory.getStackInSlot(0)
        set(value) = inventory.setStackInSlot(0, value)

    override fun onActivated(args: OnActivatedArgs): Boolean = args.run {
        if (side != EnumFacing.UP || hand == EnumHand.OFF_HAND) return false

        if (storedItem.isNotEmpty) {
            useHammer(worldIn, pos, playerIn, heldItem)
            return true
        } else {
            return placeItemFromPlayer(playerIn, heldItem, hand)
        }
    }

    fun placeItemFromPlayer(playerIn: EntityPlayer, heldItem: ItemStack, hand: EnumHand): Boolean {
        if (heldItem.isNotEmpty) {
            val hammer = ITEM_HAMMER!!.fromItem(heldItem)
            if (hammer != null) {
                if (lastItem.isNotEmpty) {
                    for (slot in 0 until playerIn.inventory.sizeInventory) {
                        val stack = playerIn.inventory.getStackInSlot(slot)

                        if (stack.isNotEmpty &&
                            CrushingTableRecipeManager.findRecipe(stack) != null &&
                            lastItem.isItemEqual(stack)) {

                            storedItem = stack.withSize(1)
                            stack.count--

                            if (stack.count <= 0) {
                                playerIn.inventory.setInventorySlotContents(slot, ItemStack.EMPTY)
                            }
                            container.sendUpdateToNearPlayers()
                            return true
                        }
                    }
                }

            } else {
                storedItem = heldItem.copy().apply { count = 1 }
                heldItem.count--

                if (heldItem.count <= 0) {
                    playerIn.setHeldItem(hand, ItemStack.EMPTY)
                }
                container.sendUpdateToNearPlayers()
                return true
            }
        }
        return false
    }

    fun hasWork(): Boolean {
        if (storedItem.isEmpty) return false
        return CrushingTableRecipeManager.findRecipe(storedItem) != null
    }

    @Suppress("DEPRECATION")
    fun useHammer(world: World, pos: BlockPos, playerIn: EntityPlayer, heldItem: ItemStack) {
        if (hasWork() && heldItem.hasCapability(ITEM_HAMMER!!, null)) {

            val hammer = heldItem.getCapability(ITEM_HAMMER!!, null) as IHammer
            val item = storedItem.item
            if (item is ItemBlock) {
                val blockState = item.block.getStateFromMeta(storedItem.metadata)
                if (item.block.getHarvestLevel(blockState) > hammer.miningLevel) {
                    return
                }
            }
            crushItem(world, pos, playerIn, hammer.breakingSpeed)
            if (Config.crushingTableCausesFire && storedItem.isItemEqual(ItemStack(Items.BLAZE_ROD))) {
                playerIn.setFire(5)
            }
            hammer.applyDamage(heldItem, playerIn)
        } else {
            if (playerIn.inventory.addItemStackToInventory(storedItem)) {
                storedItem = ItemStack.EMPTY
                container.sendUpdateToNearPlayers()
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun crushItem(world: World, pos: BlockPos, playerIn: EntityPlayer, damage: Int) {

        val recipe = CrushingTableRecipeManager.findRecipe(storedItem) ?: return
        damageTaken += damage

        if (damageTaken >= CRUSHING_DAMAGE) {
            if (world.isClient) {
                world.playSound(pos.xd, pos.yd, pos.zd, sounds["crushing_final"], SoundCategory.BLOCKS, 1F, 1F, false)
                spawnParticles(world, pos)
            }

            lastItem = storedItem
            storedItem = recipe.output
            damageTaken = 0
        } else if (world.isClient) {
            world.playSound(pos.xd, pos.yd, pos.zd, sounds["crushing_hit"], SoundCategory.BLOCKS, 1F, 1F, false)
            spawnParticles(world, pos)
        }
        container.sendUpdateToNearPlayers()
    }

    @Suppress("DEPRECATION")
    fun spawnParticles(world: World, pos: BlockPos) {
        val center = pos.toVec3d() + vec3Of(0.5, 0.95, 0.5)
        val item = storedItem.item

        if (item is ItemBlock) {
            val state = item.block.getStateFromMeta(storedItem.metadata)
            val factory = ParticleDigging.Factory()
            val rand = Random()

            for (i in 0..5) {
                val particle = factory.createParticle(EnumParticleTypes.BLOCK_DUST.particleID, world,
                        center.xd, center.yd, center.zd,
                        (rand.nextDouble() - 0.5) * 0.15, rand.nextDouble() * 0.2, (rand.nextDouble() - 0.5) * 0.15,
                        Block.getStateId(state))!!

                Minecraft.getMinecraft().effectRenderer.addEffect(particle)
            }
        } else {
            val factory = ParticleBreaking.Factory()
            val rand = Random()

            for (i in 0..5) {
                val particle = factory.createParticle(EnumParticleTypes.BLOCK_DUST.particleID, world,
                        center.xd, center.yd, center.zd,
                        (rand.nextDouble() - 0.5) * 0.15, rand.nextDouble() * 0.2, (rand.nextDouble() - 0.5) * 0.15,
                        Item.getIdFromItem(item), storedItem.itemDamage)!!

                Minecraft.getMinecraft().effectRenderer.addEffect(particle)
            }
        }
    }
}