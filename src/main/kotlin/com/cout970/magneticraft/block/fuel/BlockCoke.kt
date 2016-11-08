package com.cout970.magneticraft.block.fuel

import com.cout970.magneticraft.block.BlockBase
import com.cout970.magneticraft.fuel.IFuel
import com.cout970.magneticraft.item.ItemCoke
import net.minecraft.block.Block
import net.minecraft.block.material.Material

object BlockCoke : BlockBase(
        material = Material.ROCK,
        registryName = "block_coal_coke"), IFuel<Block> {

    override fun getBurnTime(): Int = ItemCoke.getBurnTime() * 9
}