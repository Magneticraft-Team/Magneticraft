package com.cout970.magneticraft.block.decoration

import com.cout970.magneticraft.block.BlockBase
import net.minecraft.block.material.Material

/**
 * Created by cout970 on 27/08/2016.
 */
object BlockGlazedBrick : BlockBase(Material.ROCK, "glazed_brick") {
    init {
        setHardness(1.5F)
        setResistance(45.0F)
    }
}