package com.cout970.magneticraft.misc.tileentity

import com.cout970.magneticraft.api.energy.INodeHandler
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/02/26.
 */
interface IManualWireConnect {

    fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean
}