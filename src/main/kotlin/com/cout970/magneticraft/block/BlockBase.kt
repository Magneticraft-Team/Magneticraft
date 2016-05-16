package com.cout970.magneticraft.block

import com.cout970.magneticraft.util.MODID
import com.cout970.magneticraft.util.resource
import net.minecraft.block.Block
import net.minecraft.block.material.Material

abstract class BlockBase(
    material: Material,
    registryName: String,
    unlocalizedName: String = registryName
) : Block(material) {
    init {
        this.registryName = resource(registryName)
        this.unlocalizedName = "$MODID.$unlocalizedName"
    }
}