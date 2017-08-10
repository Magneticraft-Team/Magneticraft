package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.tileentity.core.TileBase
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterTileEntity("tube_light")
class TileTubeLight : TileBase() {

    val facing: EnumFacing get() = getBlockState().getOrientation()
}
