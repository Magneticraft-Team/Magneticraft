package com.cout970.magneticraft.item.hammers

object ItemIronHammer : ItemHammer("iron", ToolMaterial.IRON) {
    override val damage = 10

    override fun getMaxDamage() = 250
}