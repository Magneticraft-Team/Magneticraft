package com.cout970.magneticraft.systems.tilerenderers

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.systems.tileentities.TileBase
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer

/**
 * Created by cout970 on 2017/06/16.
 */
abstract class TileRenderer<T : TileBase> : TileEntitySpecialRenderer<T>() {

    override fun render(tile: T, x: Double, y: Double, z: Double, tick: Float,
                        destroyStage: Int, alpha: Float) {

        renderTileEntityAt(tile, x, y, z, tick, destroyStage)
    }

    abstract fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int)

    open fun onModelRegistryReload() = Unit

    inline fun stackMatrix(func: () -> Unit) {
        pushMatrix()
        func()
        popMatrix()
    }

    fun pushMatrix() = GlStateManager.pushMatrix()
    fun popMatrix() = GlStateManager.popMatrix()

    inline fun rotationCenter(x: Number, y: Number, z: Number, func: () -> Unit) {
        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
        func()
        GlStateManager.translate(-x.toFloat(), -y.toFloat(), -z.toFloat())
    }

    inline fun rotationCenter(x: Double, y: Double, z: Double, func: () -> Unit) {
        GlStateManager.translate(x, y, z)
        func()
        GlStateManager.translate(-x, -y, -z)
    }

    inline fun rotationCenter(x: Float, y: Float, z: Float, func: () -> Unit) {
        GlStateManager.translate(x, y, z)
        func()
        GlStateManager.translate(-x, -y, -z)
    }

    inline fun rotationCenter(vec: IVector3, func: () -> Unit) {
        GlStateManager.translate(vec.xd, vec.yd, vec.zd)
        func()
        GlStateManager.translate(-vec.xd, -vec.yd, -vec.zd)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun translate(x: Number, y: Number, z: Number) {
        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun translate(x: Double, y: Double, z: Double) = GlStateManager.translate(x, y, z)
    fun translate(x: Float, y: Float, z: Float) = GlStateManager.translate(x, y, z)
    fun translate(vec: IVector3) = GlStateManager.translate(vec.xd, vec.yd, vec.zd)

    fun rotate(angle: Float, x: Float, y: Float, z: Float) = GlStateManager.rotate(angle, x, y, z)
    fun rotate(angle: Float, axis: IVector3) = GlStateManager.rotate(angle, axis.xf, axis.yf, axis.zf)

    @Suppress("NOTHING_TO_INLINE")
    inline fun rotate(angle: Number, x: Number, y: Number, z: Number) {
        GlStateManager.rotate(angle.toFloat(), x.toFloat(), y.toFloat(), z.toFloat())
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun scale(x: Number, y: Number, z: Number) = GlStateManager.scale(x.toFloat(), y.toFloat(), z.toFloat())

    fun scale(n: Double) = GlStateManager.scale(n, n, n)
    fun scale(x: Double, y: Double, z: Double) = GlStateManager.scale(x, y, z)
    fun scale(x: Float, y: Float, z: Float) = GlStateManager.scale(x, y, z)
    fun scale(vec: IVector3) = GlStateManager.scale(vec.xd, vec.yd, vec.zd)
}