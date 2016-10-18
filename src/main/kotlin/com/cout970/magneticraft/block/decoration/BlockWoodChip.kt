package com.cout970.magneticraft.block.decoration

import com.cout970.magneticraft.block.BlockFallingBase
import com.cout970.magneticraft.fuel.IFuel
import com.cout970.magneticraft.item.ItemWoodChip
import net.minecraft.block.Block

object BlockWoodChip : BlockFallingBase("wood_chip_block"), IFuel<Block> {
    override fun getBurnTime(): Int = ItemWoodChip.getBurnTime() * 10
}