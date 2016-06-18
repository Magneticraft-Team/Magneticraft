package com.cout970.magneticraft.item.hammers

object ItemStoneHammer : ItemHammer("stone") {
    //I don't know which part of the crushing table Cypher has done or what damage means
    override val damage = 8

    override fun getMaxDamage() = 40
}