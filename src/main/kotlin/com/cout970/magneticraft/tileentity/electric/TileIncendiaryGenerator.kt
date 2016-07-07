package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 04/07/2016.
 */
class TileIncendiaryGenerator : TileElectricBase() {

    var mainNode = ElectricNode({ world }, { pos }, capacity = 1.25)

    override fun getMainNode(): IElectricNode = mainNode

    override fun update() {
        node.applyPower((1 - interpolate(node.voltage, 120.0, 125.0)) * 500.0)
        super.update()
    }

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit
}