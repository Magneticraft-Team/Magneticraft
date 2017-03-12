package com.cout970.magneticraft.item.hammers

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

abstract class ItemHammer(type: String, val material: Item.ToolMaterial) : ItemMod("${type}_hammer") {
    abstract val damage: Int

    fun onHit(stack: ItemStack, hitBy: EntityLivingBase) {
        stack.damageItem(1, hitBy)
    }

    override fun getItemStackLimit() = 1

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    override fun hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase): Boolean {
        stack.damageItem(2, attacker)
        target.attackEntityFrom(DamageSource.generic, 2.0f + this.material.damageVsEntity)
        return true
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    override fun isFull3D(): Boolean {
        return true
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    override fun getItemEnchantability(): Int {
        return this.material.enchantability
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    override fun getIsRepairable(toRepair: ItemStack, repair: ItemStack): Boolean {
        val mat = this.material.repairItemStack
        if (mat != null && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true
        return super.getIsRepairable(toRepair, repair)
    }
}