package com.cout970.magneticraft.item.hammers

object ItemIronHammer : ItemHammer("iron") {
    override val damage = 8

    override fun getMaxDamage() = 250
}