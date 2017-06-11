package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.set
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.sounds
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.*
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.ParticleBreaking
import net.minecraft.client.particle.ParticleDigging
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.ITickable
import net.minecraft.util.SoundCategory
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemStackHandler
import java.util.*

@TileRegister("crushing_table")
class TileCrushingTable : TileBase(), ITickable {

    val inventory: CrushingTableInventory = CrushingTableInventory()
    var damageTaken = 0

    private var recipeCache: ICrushingTableRecipe? = null
    private var inputCache: ItemStack? = null

    fun getRecipe(input: ItemStack): ICrushingTableRecipe? {
        if (input === inputCache) return recipeCache
        val recipe = CrushingTableRecipeManager.findRecipe(input)
        if (recipe != null) {
            recipeCache = recipe
            inputCache = input
        }
        return recipe
    }

    override fun update() {
        if (worldObj.isServer && shouldTick(100)) {
            sendUpdateToNearPlayers()
        }
    }

    fun getStack() = inventory[0]?.copy()

    fun setStack(stack: ItemStack?) {
        inventory[0] = stack?.copy()
    }

    fun canDamage() = getStack() != null && getRecipe(getStack()!!) != null

    fun doDamage(amount: Int) {
        if (!canDamage()) {
            return
        }

        damageTaken += amount

        if (damageTaken >= CRUSHING_DAMAGE) {
            if (world.isClient) {
                world.playSound(Minecraft.getMinecraft().thePlayer, pos, sounds["crushing_final"], SoundCategory.BLOCKS, 1F, 1F)
                spawnParticles()
            }

            inventory.setResult(getRecipe(getStack()!!)!!.output)
        } else if (world.isClient) {
            world.playSound(Minecraft.getMinecraft().thePlayer, pos, sounds["crushing_hit"], SoundCategory.BLOCKS, 1F, 1F)
            spawnParticles()
        }
        sendUpdateToNearPlayers()
    }

    @SideOnly(Side.CLIENT)
    private fun spawnParticles() {
        val center = pos.toVec3d() + vec3Of(0.5, 0.95, 0.5)
        val stack = getStack() ?: return
        val item = stack.item ?: return

        if (item is ItemBlock) {
            val state = item.block.getStateFromMeta(stack.metadata)
            val factory = ParticleDigging.Factory()
            val rand = Random()

            for (i in 0..5) {
                val particle = factory.createParticle(EnumParticleTypes.BLOCK_DUST.particleID, world, center.xd, center.yd, center.zd,
                        (rand.nextDouble() - 0.5) * 0.15, rand.nextDouble() * 0.2, (rand.nextDouble() - 0.5) * 0.15, Block.getStateId(state))
                Minecraft.getMinecraft().effectRenderer.addEffect(particle)
            }
        } else {
            val factory = ParticleBreaking.Factory()
            val rand = Random()

            for (i in 0..5) {
                val particle = factory.createParticle(EnumParticleTypes.BLOCK_DUST.particleID, world, center.xd, center.yd, center.zd,
                        (rand.nextDouble() - 0.5) * 0.15, rand.nextDouble() * 0.2, (rand.nextDouble() - 0.5) * 0.15, Item.getIdFromItem(item), stack.itemDamage)
                Minecraft.getMinecraft().effectRenderer.addEffect(particle)
            }
        }
    }

    override fun save(): NBTTagCompound {
        val nbt = newNbt {
            if (getStack() != null) {
                add("stack",getStack()!!.serializeNBT())
            }
            add("damage", damageTaken)
        }
        return super.save().also { it.merge(nbt) }
    }

    override fun load(nbt: NBTTagCompound) {
        inventory[0] = if (nbt.hasKey("stack")) {
            ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack"))
        } else {
            null
        }
        damageTaken = nbt.getInteger("damage")
        super.load(nbt)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) =
            (capability == ITEM_HANDLER) || super.hasCapability(capability, facing)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == ITEM_HANDLER) {
            inventory as T
        } else {
            super.getCapability(capability, facing)
        }
    }

    override fun onBreak() {
        super.onBreak()
        if (worldObj.isServer) {
            if (inventory[0] != null) {
                dropItem(inventory[0]!!, pos)
            }
        }
    }

    inner class CrushingTableInventory : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            damageTaken = 0
        }

        override fun getStackLimit(slot: Int, stack: ItemStack): Int = 1

        fun setResult(stack: ItemStack) {
            super.setStackInSlot(0, stack)
        }
    }

    companion object {
        val CRUSHING_DAMAGE = 40
    }
}