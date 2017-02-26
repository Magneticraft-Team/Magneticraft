package com.cout970.magneticraft.misc.tileentity

import com.cout970.magneticraft.tileentity.TileBase
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/02/21.
 */
abstract class TileTrait(val tile: TileBase) : ITileTrait {

    override fun getPos(): BlockPos = tile.pos
    override fun getWorld(): World = tile.world

    private var firstTick = false

    override fun update() {
        if (!firstTick){
            firstTick = true
            onFullyLoad()
        }
    }

    open fun onFullyLoad() = Unit
}