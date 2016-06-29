package com.cout970.magneticraft.tileentity

import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import coffee.cypher.mcextlib.extensions.vectors.*
import com.cout970.magneticraft.api.registries.machines.crushingtable.CrushingTableRegistry
import com.cout970.magneticraft.client.sounds.sounds
import com.cout970.magneticraft.util.ITEM_HANDLER
import com.cout970.magneticraft.util.vector.Vec3d
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
import net.minecraft.util.SoundCategory
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemStackHandler

class TileCrushingTable : TileBase() {
    companion object {
        val CRUSHING_DAMAGE = 40
    }

    var damageTaken = 0
    private val _inventory = CrushingTableInventory()
    val inventory: ItemStackHandler = _inventory

    fun getStack() = inventory[0]?.copy()

    fun setStack(stack: ItemStack?) {
        inventory[0] = stack?.copy()
    }

    fun canDamage() = getStack() != null && CrushingTableRegistry.getRecipe(getStack()!!) != null

    fun doDamage(amount: Int) {
        if (!canDamage()) {
            return
        }

        damageTaken += amount

        if (damageTaken >= CRUSHING_DAMAGE) {
            if (world.isRemote) {
                world.playSound(Minecraft.getMinecraft().thePlayer, pos, sounds["crushing_final"], SoundCategory.BLOCKS, 1F, 1F)
                spawnParticles()
            }

            _inventory.setResult(CrushingTableRegistry.getRecipe(getStack()!!)!!)
        } else if (world.isRemote) {
            world.playSound(Minecraft.getMinecraft().thePlayer, pos, sounds["crushing_hit"], SoundCategory.BLOCKS, 1F, 1F)
            spawnParticles()
        }
    }

    @SideOnly(Side.CLIENT)
    private fun spawnParticles() {
        val center = pos.toDoubleVec() + Vec3d(0.5, 0.95, 0.5)
        val stack = getStack() ?: return
        val item = stack.item ?: return

        if (item is ItemBlock) {
            val state = item.block.getStateFromMeta(stack.metadata)
            val factory = ParticleDigging.Factory()

            val particle = factory.getEntityFX(EnumParticleTypes.BLOCK_DUST.particleID, world, center.x, center.y, center.z, 0.0, 1.0, 0.0, Block.getStateId(state))
            Minecraft.getMinecraft().effectRenderer.addEffect(particle)
        } else {
            val factory = ParticleBreaking.Factory()

            val particle = factory.getEntityFX(EnumParticleTypes.BLOCK_DUST.particleID, world, center.x, center.y, center.z, 0.0, 1.0, 0.0, Item.getIdFromItem(item), stack.itemDamage)
            Minecraft.getMinecraft().effectRenderer.addEffect(particle)
        }
    }

    override fun writeToNBT(compound: NBTTagCompound) = super.writeToNBT(compound).apply {
        if (getStack() != null) {
            setTag("stack", NBTTagCompound().apply { getStack()?.writeToNBT(this) })
        }
        setInteger("damage", damageTaken)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        inventory[0] = if (compound.hasKey("stack"))
            ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack"))
        else
            null

        damageTaken = compound.getInteger("damage")
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?) =
        (capability == ITEM_HANDLER) || super.hasCapability(capability, facing)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?) =
        if (capability == ITEM_HANDLER)
            inventory as T
        else
            super.getCapability(capability, facing)

    override fun onBreak() {
        super.onBreak()
        if (!worldObj.isRemote) {
            if (inventory[0] != null) {
                dropItem(inventory[0]!!, pos)
            }
        }
    }

    private inner class CrushingTableInventory : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            damageTaken = 0
        }

        override fun setStackInSlot(slot: Int, stack: ItemStack?) {
            if (stack == null || (stack.stackSize == 1 && CrushingTableRegistry.getRecipe(stack) != null)) {
                super.setStackInSlot(slot, stack)
            }
        }

        override fun insertItem(slot: Int, stack: ItemStack?, simulate: Boolean): ItemStack? {
            if (stack == null) {
                return null
            }

            if (this[0] != null) {
                return stack
            }

            if (stack.stackSize == 1 && CrushingTableRegistry.getRecipe(stack) != null) {
                if (!simulate) {
                    setStackInSlot(slot, stack)
                }

                return null
            }

            return stack
        }

        fun setResult(stack: ItemStack) {
            super.setStackInSlot(0, stack)
        }
    }
}