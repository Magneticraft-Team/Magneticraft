package com.cout970.magneticraft.item.hammers

object ItemStoneHammer : ItemHammer("stone", ToolMaterial.STONE) {
    override val damage = 8

    override fun getMaxDamage() = 130
}