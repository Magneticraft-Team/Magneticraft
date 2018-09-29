package com.cout970.magneticraft.features.decoration

import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.systems.tileentities.TileBase
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterTileEntity("tube_light")
class TileTubeLight : TileBase() {

    val facing: EnumFacing get() = getBlockState().getOrientation()
}
