package com.cout970.magneticraft.tilerenderer.core

import com.cout970.magneticraft.Sprite
import com.cout970.modelloader.ModelData
import com.cout970.modelloader.QuadProvider
import com.cout970.modelloader.QuadStorage
import com.cout970.vector.api.IVector2

object ModelTransform {

    fun updateModelUvs(provider: QuadProvider, sprite: Sprite): QuadProvider {
        val quads = QuadStorage(
                provider.modelData.quads.pos,
                provider.modelData.quads.tex.map { sprite.mapUv(it) },
                provider.modelData.quads.indices
        )
        val modelData = ModelData(
                provider.modelData.useAmbientOcclusion,
                provider.modelData.use3dInGui,
                provider.modelData.particleTexture,
                provider.modelData.parts,
                quads
        )
        return QuadProvider(
                modelData,
                provider.particles,
                provider.bakedQuads.flatMap { it.value }
        )
    }

    fun Sprite.mapUv(uv: IVector2): IVector2 {
        return com.cout970.vector.extensions.vec2Of(
                getInterpolatedU(uv.xd * 16),
                getInterpolatedV(uv.yd * 16)
        )
    }
}