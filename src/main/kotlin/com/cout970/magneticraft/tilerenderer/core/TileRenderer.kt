package com.cout970.magneticraft.tilerenderer.core

import com.cout970.magneticraft.tileentity.core.TileBase
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer

/**
 * Created by cout970 on 2017/06/16.
 */
abstract class TileRenderer<T: TileBase> : TileEntitySpecialRenderer<T>() {

    override fun func_192841_a(tile: T, x: Double, y: Double, z: Double, tick: Float, destroyStage: Int, unknown: Float) {
        renderTileEntityAt(tile, x, y, z, tick, destroyStage)
    }

    abstract fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int)

    open fun onModelRegistryReload(){

    }
}