package com.cout970.magneticraft.client.render.model

import com.cout970.magneticraft.client.render.ModelLoader
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.Vec3d
import com.google.common.base.Function
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import org.lwjgl.util.vector.Vector3f
import java.util.*

/**
 * Created by cout970 on 13/05/2016.
 */
data class BlockModel(
        val textureList: List<ResourceLocation>
) : ModelLoader.Model {

    override fun getIModel(): IModel? {
        return Model(textureList)
    }
}

val transform : ItemCameraTransforms = ItemCameraTransforms(
        ItemTransformVec3f.DEFAULT,//thirdperson_left
        ItemTransformVec3f(Vector3f(75f, 45f, 0f), Vector3f(0f, 2.5f, 0f).scale(0.0625f) as Vector3f, Vector3f(0.375f, 0.375f, 0.375f)),//thirdperson_right
        ItemTransformVec3f(Vector3f(0f, 225f, 0f), Vector3f(), Vector3f(0.40f, 0.40f, 0.40f)),//firstperson_left
        ItemTransformVec3f(Vector3f(0f, 45f, 0f), Vector3f(), Vector3f(0.40f, 0.40f, 0.40f)),//firstperson_right
        ItemTransformVec3f.DEFAULT,//head
        ItemTransformVec3f(Vector3f(30f, 225f, 0f), Vector3f(), Vector3f(0.625f, 0.625f, 0.625f)),//gui
        ItemTransformVec3f(Vector3f(), Vector3f(0f, 3f, 0f).scale(0.0625f) as Vector3f, Vector3f(0.25f, 0.25f, 0.25f)),//ground
        ItemTransformVec3f(Vector3f(), Vector3f(), Vector3f(0.5f, 0.5f, 0.5f)))//fixed


class Model(val textures: List<ResourceLocation>) : IModel {

    override fun bake(state: IModelState?, format: VertexFormat?, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>?): IBakedModel? {
        if (bakedTextureGetter == null) return null
        val quads = mutableMapOf<EnumFacing, MutableList<BakedQuad>>()
        for (i in EnumFacing.values()) {
            quads.put(i, mutableListOf())
        }
        for (i in 0..5) {
            val j = if (i < textures.size) i else textures.size - 1
            val baked = cube[i].toBakedQuad(bakedTextureGetter.apply(textures[j]) ?:
                    bakedTextureGetter.apply(ModelResourceLocation("builtin/missing", "missing"))!!)
            quads[EnumFacing.values()[i]]?.add(baked)
        }

        return SimpleBakedModel(listOf(), quads, true, true, bakedTextureGetter.apply(textures[0]), transform, ItemOverrideList.NONE)
    }

    override fun getTextures(): MutableCollection<ResourceLocation>? = ArrayList(textures)

    override fun getDefaultState(): IModelState? = null

    override fun getDependencies(): MutableCollection<ResourceLocation>? = mutableListOf()
}

data class Vertex(val x: Int, val y: Int, val z: Int, val u: Int, val v: Int) {

    fun toVec3d(): Vec3d = Vec3d(x, y, z)

    fun toVec2d(tex: TextureAtlasSprite): Vec2d = Vec2d(tex.getInterpolatedU(u*16.0), tex.getInterpolatedV(v*16.0))
}

data class Quad(val a: Vertex, val b: Vertex, val c: Vertex, val d: Vertex, val side: EnumFacing) {

    fun toBakedQuad(tex: TextureAtlasSprite): BakedQuad {
        val builder = BakedQuadBuilder(side, tex)
        builder.addVertex(a.toVec3d(), a.toVec2d(tex))
        builder.addVertex(b.toVec3d(), b.toVec2d(tex))
        builder.addVertex(c.toVec3d(), c.toVec2d(tex))
        builder.addVertex(d.toVec3d(), d.toVec2d(tex))
//        builder.shade = true;
        //aparently 1.9 remplace shade with applyDiffuseLighting
        builder.applyDiffuseLighting = true
        return builder.build()
    }
}

val cube = listOf<Quad>(
        Quad(
                Vertex(0, 0, 0, 0, 1),
                Vertex(1, 0, 0, 1, 1),
                Vertex(1, 0, 1, 1, 0),
                Vertex(0, 0, 1, 0, 0),
                EnumFacing.DOWN),
        Quad(
                Vertex(0, 1, 0, 0, 0),
                Vertex(0, 1, 1, 0, 1),
                Vertex(1, 1, 1, 1, 1),
                Vertex(1, 1, 0, 1, 0),
                EnumFacing.UP),
        Quad(
                Vertex(0, 0, 0, 1, 1),
                Vertex(0, 1, 0, 1, 0),
                Vertex(1, 1, 0, 0, 0),
                Vertex(1, 0, 0, 0, 1),
                EnumFacing.NORTH),
        Quad(
                Vertex(0, 0, 1, 0, 1),
                Vertex(1, 0, 1, 1, 1),
                Vertex(1, 1, 1, 1, 0),
                Vertex(0, 1, 1, 0, 0),
                EnumFacing.SOUTH),
        Quad(
                Vertex(0, 0, 0, 0, 1),
                Vertex(0, 0, 1, 1, 1),
                Vertex(0, 1, 1, 1, 0),
                Vertex(0, 1, 0, 0, 0),
                EnumFacing.WEST),
        Quad(
                Vertex(1, 0, 0, 1, 1),
                Vertex(1, 1, 0, 1, 0),
                Vertex(1, 1, 1, 0, 0),
                Vertex(1, 0, 1, 0, 1),
                EnumFacing.EAST)
);