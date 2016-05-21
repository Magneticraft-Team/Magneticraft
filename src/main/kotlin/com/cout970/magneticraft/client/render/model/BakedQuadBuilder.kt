package com.cout970.magneticraft.client.render.model

import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.getXf
import com.cout970.magneticraft.util.vector.getYf
import com.cout970.magneticraft.util.vector.getZf
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 14/05/2016.
 */

class BakedQuadBuilder(val side: EnumFacing, val texture: TextureAtlasSprite) {

    val FACE_BRIGHTNESS = listOf(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F)
    val vertex = mutableListOf<Vec3d>()
    val uv = mutableListOf<Vec2d>()
    var shade = false
    var applyDiffuseLighting = false
    var aux = 0

    fun addVertex(pos: Vec3d, uv: Vec2d) {
        if (aux >= 4) {
            throw IllegalStateException("Unable to add more than 4 vertex to a BakedQuadBuilder");
        }
        this.vertex.add(pos);
        this.uv.add(uv);
        aux++;
    }

    fun build(): BakedQuad {
        val data: IntArray = IntArray(7 * 4, { 0 })
        val color = if (!shade) -1 else getFaceShadeColor()
        for (i in 0..3)
            fillVertexData(data, i, vertex[i], color, uv[i]);
        fillNormal(data, side)
        return BakedQuad(data, -1, side, texture, applyDiffuseLighting, DefaultVertexFormats.ITEM);
    }

    private fun fillNormal(data: IntArray, side: EnumFacing) {
        val v1 = side.directionVec
        val x = (v1.x * 127).toInt() and 0xFF
        val y = (v1.y * 127).toInt() and 0xFF
        val z = (v1.z * 127).toInt() and 0xFF
        for (i in 0..3) {
            data[i * 7 + 6] = x or (y shl 0x08) or (z shl 0x10)
        }
    }

    fun fillVertexData(faceData: IntArray, storeIndex: Int, position: Vec3d, shadeColor: Int, uv: Vec2d) {
        val l = storeIndex * 7;
        faceData[l + 0] = java.lang.Float.floatToRawIntBits(position.getXf());
        faceData[l + 1] = java.lang.Float.floatToRawIntBits(position.getYf());
        faceData[l + 2] = java.lang.Float.floatToRawIntBits(position.getZf());
        faceData[l + 3] = shadeColor;
        faceData[l + 4] = java.lang.Float.floatToRawIntBits(uv.getXf());
        faceData[l + 5] = java.lang.Float.floatToRawIntBits(uv.getYf());
    }

    fun getFaceShadeColor(): Int {
        val f = FACE_BRIGHTNESS[side.index];
        var i = MathHelper.clamp_int((f * 255.0F).toInt(), 0, 255);
        val j = 0xFF000000.toInt()
        return j or (i shl 16) or (i shl 8) or i;
    }
}