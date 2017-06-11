package com.cout970.magneticraft.block.decoration

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import net.minecraft.block.material.Material

/**
 * Created by cout970 on 27/08/2016.
 */
object BlockGlazedBrick : BlockMod("glazed_brick", Material.ROCK) {
    init {
        setHardness(1.5F)
        setResistance(45.0F)
    }
}