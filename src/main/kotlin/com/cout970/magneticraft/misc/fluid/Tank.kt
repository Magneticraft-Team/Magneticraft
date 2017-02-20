package com.cout970.magneticraft.misc.fluid

import com.cout970.magneticraft.gui.common.DATA_ID_FLUID_AMOUNT
import com.cout970.magneticraft.gui.common.DATA_ID_FLUID_NAME
import com.cout970.magneticraft.misc.network.IBD
import net.minecraftforge.fluids.FluidTank

/**
 * Created by cout970 on 10/07/2016.
 */
open class Tank(capacity:Int) : FluidTank(capacity) {

    var clientFluidAmount = 0
    var clientFluidName = ""

    //server only
    fun getData(): IBD {
        val data = IBD()
        data.setInteger(DATA_ID_FLUID_AMOUNT, getFluid()?.amount ?: 0)
        data.setString(DATA_ID_FLUID_NAME, getFluid()?.fluid?.name ?: "")
        return data
    }

    //client only
    fun setData(ibd: IBD){
        ibd.getInteger(DATA_ID_FLUID_AMOUNT, { clientFluidAmount = it })
        ibd.getString(DATA_ID_FLUID_NAME, { clientFluidName = it })
    }
}