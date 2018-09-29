package com.cout970.magneticraft.systems.tilerenderers

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.Sprite
import com.cout970.magneticraft.misc.vector.xf
import com.cout970.magneticraft.misc.vector.yf
import com.cout970.magneticraft.misc.vector.zf
import com.cout970.modelloader.api.IRenderCache
import com.cout970.modelloader.api.ModelCache
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/08/29.
 */

class MutableCubeCache : IRenderCache {

    private var cache: IRenderCache? = null

    var pos: IVector3 = Vec3d.ZERO
        set(value) {
            if (value != field) {
                field = value
                close()
            }
        }
    var size: IVector3 = Vec3d.ZERO
        set(value) {
            if (value != field) {
                field = value
                close()
            }
        }

    var sprites: List<Sprite> = emptyList()
        set(value) {
            if (!compare(field, value)) {
                field = value
                close()
            }
        }

    var excluded: Set<EnumFacing> = emptySet()
        set(value) {
            if (value != field) {
                field = value
                close()
            }
        }

    fun compare(a: List<Sprite>, b: List<Sprite>): Boolean {
        if (a.size != b.size) return false
        a.zip(b).forEach {
            if (it.first.iconName != it.second.iconName) return false
        }
        return true
    }

    override fun render() {
        if (cache == null) {
            cache = CubeFactory(pos, size, sprites, excluded).build()
        }
        cache?.render()
    }

    override fun close() {
        cache?.close()
        cache = null
    }
}

class CubeFactory(val pos: IVector3, val size: IVector3, sprites: List<Sprite>, val excluded: Set<EnumFacing>) {

    val icons: List<Sprite> = sprites.let {
        if (it.isEmpty()) emptyList() else if (it.size != 6) listOf(it[0], it[0], it[0], it[0], it[0], it[0]) else it
    }

    fun build(): IRenderCache {
        if (icons.isEmpty()) return ModelCache {}
        return ModelCache {
            val width = size.xf
            val height = size.yf
            val deep = size.zf
            val x = pos.xf
            val y = pos.yf
            val z = pos.zf

            val tess = Tessellator.getInstance()
            val t = tess.buffer
            t.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL)
            if (EnumFacing.UP !in excluded) drawTop(t, x, z, deep + x, width + z, height + y)
            if (EnumFacing.DOWN !in excluded) drawBottom(t, x, z, deep + x, width + z, y)
            if (EnumFacing.NORTH !in excluded) drawNorth(t, x, y, deep + x, height + y, z)
            if (EnumFacing.SOUTH !in excluded) drawSouth(t, x, y, deep + x, height + y, width + z)
            if (EnumFacing.WEST !in excluded) drawWest(t, z, y, width + z, height + y, deep + x)
            if (EnumFacing.EAST !in excluded) drawEast(t, z, y, width + z, height + y, x)
            tess.draw()
        }
    }

    @SideOnly(Side.CLIENT)
    private fun drawBottom(t: BufferBuilder, x: Float, z: Float, x1: Float, z1: Float, y: Float) {
        t.pos(x1.toDouble(), y.toDouble(), z.toDouble())
            .tex(icons[0].maxU.toDouble(), icons[0].minV.toDouble())
            .normal(0f, -1f, 0f)
            .endVertex()
        t.pos(x1.toDouble(), y.toDouble(), z1.toDouble())
            .tex(icons[0].minU.toDouble(), icons[0].minV.toDouble())
            .normal(0f, -1f, 0f)
            .endVertex()
        t.pos(x.toDouble(), y.toDouble(), z1.toDouble())
            .tex(icons[0].minU.toDouble(), icons[0].maxV.toDouble())
            .normal(0f, -1f, 0f)
            .endVertex()
        t.pos(x.toDouble(), y.toDouble(), z.toDouble())
            .tex(icons[0].maxU.toDouble(), icons[0].maxV.toDouble())
            .normal(0f, -1f, 0f)
            .endVertex()
    }

    @SideOnly(Side.CLIENT)
    private fun drawTop(t: BufferBuilder, x: Float, z: Float, x1: Float, z1: Float, y: Float) {
        t.pos(x.toDouble(), y.toDouble(), z.toDouble())
            .tex(icons[1].maxU.toDouble(), icons[1].maxV.toDouble())
            .normal(0f, 1f, 0f)
            .endVertex()
        t.pos(x.toDouble(), y.toDouble(), z1.toDouble())
            .tex(icons[1].minU.toDouble(), icons[1].maxV.toDouble())
            .normal(0f, 1f, 0f)
            .endVertex()
        t.pos(x1.toDouble(), y.toDouble(), z1.toDouble())
            .tex(icons[1].minU.toDouble(), icons[1].minV.toDouble())
            .normal(0f, 1f, 0f)
            .endVertex()
        t.pos(x1.toDouble(), y.toDouble(), z.toDouble())
            .tex(icons[1].maxU.toDouble(), icons[1].minV.toDouble())
            .normal(0f, 1f, 0f)
            .endVertex()
    }

    @SideOnly(Side.CLIENT)
    private fun drawNorth(t: BufferBuilder, x: Float, y: Float, x1: Float, y1: Float, z: Float) {
        t.pos(x.toDouble(), y.toDouble(), z.toDouble())
            .tex(icons[2].maxU.toDouble(), icons[2].maxV.toDouble())
            .normal(0f, 0f, -1f)
            .endVertex()
        t.pos(x.toDouble(), y1.toDouble(), z.toDouble())
            .tex(icons[2].minU.toDouble(), icons[2].maxV.toDouble())
            .normal(0f, 0f, -1f)
            .endVertex()
        t.pos(x1.toDouble(), y1.toDouble(), z.toDouble())
            .tex(icons[2].minU.toDouble(), icons[2].minV.toDouble())
            .normal(0f, 0f, -1f)
            .endVertex()
        t.pos(x1.toDouble(), y.toDouble(), z.toDouble())
            .tex(icons[2].maxU.toDouble(), icons[2].minV.toDouble())
            .normal(0f, 0f, -1f)
            .endVertex()
    }

    @SideOnly(Side.CLIENT)
    private fun drawSouth(t: BufferBuilder, x: Float, y: Float, x1: Float, y1: Float, z: Float) {
        t.pos(x1.toDouble(), y.toDouble(), z.toDouble())
            .tex(icons[3].maxU.toDouble(), icons[3].maxV.toDouble())
            .normal(0f, 0f, 1f)
            .endVertex()
        t.pos(x1.toDouble(), y1.toDouble(), z.toDouble())
            .tex(icons[3].minU.toDouble(), icons[3].maxV.toDouble())
            .normal(0f, 0f, 1f)
            .endVertex()
        t.pos(x.toDouble(), y1.toDouble(), z.toDouble())
            .tex(icons[3].minU.toDouble(), icons[3].minV.toDouble())
            .normal(0f, 0f, 1f)
            .endVertex()
        t.pos(x.toDouble(), y.toDouble(), z.toDouble())
            .tex(icons[3].maxU.toDouble(), icons[3].minV.toDouble())
            .normal(0f, 0f, 1f)
            .endVertex()
    }

    @SideOnly(Side.CLIENT)
    private fun drawWest(t: BufferBuilder, z: Float, y: Float, z1: Float, y1: Float, x: Float) {
        t.pos(x.toDouble(), y.toDouble(), z.toDouble())
            .tex(icons[5].maxU.toDouble(), icons[5].maxV.toDouble())
            .normal(-1f, 0f, 0f)
            .endVertex()
        t.pos(x.toDouble(), y1.toDouble(), z.toDouble())
            .tex(icons[5].minU.toDouble(), icons[5].maxV.toDouble())
            .normal(-1f, 0f, 0f)
            .endVertex()
        t.pos(x.toDouble(), y1.toDouble(), z1.toDouble())
            .tex(icons[5].minU.toDouble(), icons[5].minV.toDouble())
            .normal(-1f, 0f, 0f)
            .endVertex()
        t.pos(x.toDouble(), y.toDouble(), z1.toDouble())
            .tex(icons[5].maxU.toDouble(), icons[5].minV.toDouble())
            .normal(-1f, 0f, 0f)
            .endVertex()
    }

    @SideOnly(Side.CLIENT)
    private fun drawEast(t: BufferBuilder, z: Float, y: Float, z1: Float, y1: Float, x: Float) {
        t.pos(x.toDouble(), y.toDouble(), z1.toDouble())
            .tex(icons[4].maxU.toDouble(), icons[4].maxV.toDouble())
            .normal(1f, 0f, 0f)
            .endVertex()
        t.pos(x.toDouble(), y1.toDouble(), z1.toDouble())
            .tex(icons[4].minU.toDouble(), icons[4].maxV.toDouble())
            .normal(1f, 0f, 0f)
            .endVertex()
        t.pos(x.toDouble(), y1.toDouble(), z.toDouble())
            .tex(icons[4].minU.toDouble(), icons[4].minV.toDouble())
            .normal(1f, 0f, 0f)
            .endVertex()
        t.pos(x.toDouble(), y.toDouble(), z.toDouble())
            .tex(icons[4].maxU.toDouble(), icons[4].minV.toDouble())
            .normal(1f, 0f, 0f)
            .endVertex()
    }
}