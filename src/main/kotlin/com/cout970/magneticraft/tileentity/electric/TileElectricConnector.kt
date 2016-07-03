package com.cout970.magneticraft.tileentity.electric

import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 29/06/2016.
 */
class TileElectricConnector : TileElectricBase() {

    var state = 0

    override fun update() {
        if (state == 1) {
            node.applyPower((1 - interpolate(node.voltage, 115.0, 120.0)) * 500.0)
        } else if (state == 2) {
            node.applyPower(interpolate(node.voltage, 80.0, 90.0) * -100.0)
        }
        super.update()
    }

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit
}