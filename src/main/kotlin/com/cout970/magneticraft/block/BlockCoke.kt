package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.blocks.creativeTab
import com.cout970.magneticraft.fuel.FuelProvider
import com.cout970.magneticraft.item.ItemCoke
import com.cout970.magneticraft.misc.CreativeTabMg
import net.minecraft.block.Block
import net.minecraft.block.material.Material

object BlockCoke : BlockBase(
        material = Material.ROCK,
        registryName = "block_coke"), FuelProvider<Block> {

    init {
        creativeTab = CreativeTabMg
    }

    override fun getBurnTime(): Int = ItemCoke.getBurnTime() * 9
}