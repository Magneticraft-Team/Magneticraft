package com.cout970.magneticraft.item

import com.cout970.magneticraft.util.MODID
import com.cout970.magneticraft.util.resource
import net.minecraft.item.Item

abstract class ItemBase(
    registryName: String,
    unlocalizedName: String = registryName
) : Item() {
    init {
        this.registryName = resource(registryName)
        this.unlocalizedName = "$MODID.$unlocalizedName"
    }
}