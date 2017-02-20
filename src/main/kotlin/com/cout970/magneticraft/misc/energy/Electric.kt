package com.cout970.magneticraft.misc.energy

import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode

/**
 * Created by cout970 on 2017/02/20.
 */
operator fun IElectricConnection.contains(node: IElectricNode) = this.firstNode == node || this.secondNode == node
