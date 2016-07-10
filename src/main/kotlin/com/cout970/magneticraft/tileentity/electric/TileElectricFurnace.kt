package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 04/07/2016.
 */
class TileElectricFurnace : TileElectricBase() {

    var mainNode = ElectricNode({ world }, { pos }, capacity = 1.25)

    override fun getMainNode(): IElectricNode = mainNode

    override fun update() {
        node.applyPower(interpolate(node.voltage, 50.0, 60.0) * -100.0)
        super.update()
    }

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit
}