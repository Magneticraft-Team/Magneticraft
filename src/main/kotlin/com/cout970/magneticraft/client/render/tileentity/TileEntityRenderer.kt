package com.cout970.magneticraft.client.render.tileentity

import com.cout970.magneticraft.tileentity.TileBase
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer

/**
 * Created by cout970 on 16/07/2016.
 */
abstract class TileEntityRenderer<T: TileBase> : TileEntitySpecialRenderer<T>() {

    abstract override fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int)

    open fun onModelRegistryReload(){}
}