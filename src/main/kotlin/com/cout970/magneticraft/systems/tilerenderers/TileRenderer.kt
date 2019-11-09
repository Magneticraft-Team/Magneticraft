package com.cout970.magneticraft.systems.tilerenderers

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.misc.render.GL
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.tileentity.TileEntityRenderer

/**
 * Created by cout970 on 2017/06/16.
 */
abstract class TileRenderer<T : TileBase> : TileEntityRenderer<T>() {

    override fun render(tile: T, x: Double, y: Double, z: Double, tick: Float, destroyStage: Int) {
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
        GL.translate(x.toFloat(), y.toFloat(), z.toFloat())
        func()
        GL.translate(-x.toFloat(), -y.toFloat(), -z.toFloat())
    }

    inline fun rotationCenter(x: Double, y: Double, z: Double, func: () -> Unit) {
        GL.translate(x, y, z)
        func()
        GL.translate(-x, -y, -z)
    }

    inline fun rotationCenter(x: Float, y: Float, z: Float, func: () -> Unit) {
        GL.translate(x, y, z)
        func()
        GL.translate(-x, -y, -z)
    }

    inline fun rotationCenter(vec: IVector3, func: () -> Unit) {
        GL.translate(vec.xd, vec.yd, vec.zd)
        func()
        GL.translate(-vec.xd, -vec.yd, -vec.zd)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun translate(x: Number, y: Number, z: Number) = GL.translate(x, y, z)

    fun translate(x: Double, y: Double, z: Double) = GL.translate(x, y, z)
    fun translate(x: Float, y: Float, z: Float) = GL.translate(x, y, z)
    fun translate(vec: IVector3) = GL.translate(vec.xf, vec.yf, vec.zf)

    @Suppress("NOTHING_TO_INLINE")
    inline fun rotate(angle: Number, x: Number, y: Number, z: Number) = GL.rotate(angle, x, y, z)

    fun rotate(angle: Double, x: Double, y: Double, z: Double) = GL.rotate(angle, x, y, z)
    fun rotate(angle: Float, x: Float, y: Float, z: Float) = GL.rotate(angle, x, y, z)
    fun rotate(angle: Float, axis: IVector3) = GL.rotate(angle, axis.xf, axis.yf, axis.zf)

    @Suppress("NOTHING_TO_INLINE")
    inline fun scale(x: Number, y: Number, z: Number) = GL.scale(x, y, z)

    fun scale(n: Double) = GL.scale(n, n, n)
    fun scale(n: Float) = GL.scale(n, n, n)

    fun scale(x: Double, y: Double, z: Double) = GL.scale(x, y, z)
    fun scale(x: Float, y: Float, z: Float) = GL.scale(x, y, z)
    fun scale(vec: IVector3) = GL.scale(vec.xf, vec.yf, vec.zf)

    val mc: Minecraft get() = Minecraft.getInstance()
}