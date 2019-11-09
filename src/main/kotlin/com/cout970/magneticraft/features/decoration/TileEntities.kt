package com.cout970.magneticraft.features.decoration

import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.TileType
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.systems.tileentities.TileBase


/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterTileEntity("tube_light")
class TileTubeLight(type: TileType) : TileBase(type) {

    val facing: EnumFacing get() = getBlockState().getOrientation()
}
