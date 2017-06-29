package com.cout970.magneticraft.tilerenderer.core

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.util.vector.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer

/**
 * Created by cout970 on 2017/06/16.
 */
abstract class TileRenderer<T : TileBase> : TileEntitySpecialRenderer<T>() {

    override fun render(tile: T, x: Double, y: Double, z: Double, tick: Float, destroyStage: Int,
                        alpha: Float) {
        renderTileEntityAt(tile, x, y, z, tick, destroyStage)
    }

    abstract fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int)

    open fun onModelRegistryReload() {

    }

    inline fun stackMatrix(func: () -> Unit) {
        pushMatrix()
        func()
        popMatrix()
    }

    fun pushMatrix() = GlStateManager.pushMatrix()
    fun popMatrix() = GlStateManager.popMatrix()

    fun translate(x: Number, y: Number, z: Number) = GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
    fun translate(x: Double, y: Double, z: Double) = GlStateManager.translate(x, y, z)
    fun translate(x: Float, y: Float, z: Float) = GlStateManager.translate(x, y, z)
    fun translate(vec: IVector3) = GlStateManager.translate(vec.xd, vec.yd, vec.zd)

    fun rotate(angle: Float, x: Float, y: Float, z: Float) = GlStateManager.rotate(angle, x, y, z)
    fun rotate(angle: Float, axis: IVector3) = GlStateManager.rotate(angle, axis.xf, axis.yf, axis.zf)
    fun rotate(angle: Number, x: Number, y: Number, z: Number) {
        GlStateManager.rotate(angle.toFloat(), x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun scale(x: Number, y: Number, z: Number) = GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
    fun scale(x: Double, y: Double, z: Double) = GlStateManager.translate(x, y, z)
    fun scale(x: Float, y: Float, z: Float) = GlStateManager.translate(x, y, z)
    fun scale(vec: IVector3) = GlStateManager.translate(vec.xd, vec.yd, vec.zd)
}