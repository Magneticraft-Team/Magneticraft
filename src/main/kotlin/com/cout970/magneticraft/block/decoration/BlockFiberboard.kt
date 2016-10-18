package com.cout970.magneticraft.block.decoration

import com.cout970.magneticraft.block.BlockBase
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.fuel.IFuel
import net.minecraft.block.Block
import net.minecraft.block.material.Material

object BlockFiberboard : BlockBase(Material.WOOD, "fiberboard_block"), IFuel<Block> {
    override fun getBurnTime(): Int = Config.fiberboardBurnTime
}