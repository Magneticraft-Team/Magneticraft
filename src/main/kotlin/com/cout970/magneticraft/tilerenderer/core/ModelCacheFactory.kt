package com.cout970.magneticraft.tilerenderer.core

import com.cout970.modelloader.ModelData
import com.cout970.modelloader.api.ModelLoaderApi
import com.cout970.vector.extensions.*
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/06/16.
 */
object ModelCacheFactory {

    fun createCache(loc: ModelResourceLocation, filter: (String) -> Boolean): ModelCache? {
        val model = ModelLoaderApi.getModel(loc) ?: return null

        return ModelCache {
            renderModelParts(model.modelData.parts.filter { filter(it.name) }, model.modelData)
        }
    }

    fun renderModelParts(parts: List<ModelData.Part>, model: ModelData) {
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer
        val storage = model.quads

        buffer.apply {
            begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL)
            parts.forEach { part ->
                val indices = storage.indices.subList(part.from, part.to)
                indices.forEach { index ->
                    val pos0 = storage.pos[index.a]
                    val pos1 = storage.pos[index.b]
                    val pos2 = storage.pos[index.c]
                    val pos3 = storage.pos[index.d]

                    val tex0 = storage.tex[index.at]
                    val tex1 = storage.tex[index.bt]
                    val tex2 = storage.tex[index.ct]
                    val tex3 = storage.tex[index.dt]

                    val normal = ((pos2 - pos0) cross (pos3 - pos1)).normalize()


                    pos(pos0.xd, pos0.yd, pos0.zd).tex(tex0.xd, tex0.yd).normal(normal.xf, normal.yf,
                            normal.zf).endVertex()
                    pos(pos1.xd, pos1.yd, pos1.zd).tex(tex1.xd, tex1.yd).normal(normal.xf, normal.yf,
                            normal.zf).endVertex()
                    pos(pos2.xd, pos2.yd, pos2.zd).tex(tex2.xd, tex2.yd).normal(normal.xf, normal.yf,
                            normal.zf).endVertex()
                    pos(pos3.xd, pos3.yd, pos3.zd).tex(tex3.xd, tex3.yd).normal(normal.xf, normal.yf,
                            normal.zf).endVertex()
                }
            }
            setTranslation(0.0, 0.0, 0.0)
            tessellator.draw()
        }

    }

//    fun BufferBuilder.setPos(xyz: IVector3): BufferBuilder {
//        pos(xyz.xCoord, xyz.yCoord, xyz.zCoord)
//        return this
//    }
//
//    fun BufferBuilder.setTex(xy: IVector2): BufferBuilder {
//        tex(xy.xd, xy.yd)
//        return this
//    }
//
//    fun BufferBuilder.setNorm(xyz: IVector3): BufferBuilder {
//        normal(xyz.xf, xyz.yf, xyz.zf)
//        return this
//    }
}