package com.cout970.magneticraft.tilerenderer.core

import com.cout970.magneticraft.Sprite
import com.cout970.modelloader.api.formats.mcx.McxModel
import com.cout970.modelloader.api.formats.mcx.Mesh
import com.cout970.vector.api.IVector2

object ModelTransform {

    fun updateModelUvs(provider: McxModel, sprite: Sprite): McxModel {
        val quads = Mesh(
                provider.quads.pos,
                provider.quads.tex.map { sprite.mapUv(it) },
                provider.quads.indices
        )
        return McxModel(
                provider.useAmbientOcclusion,
                provider.use3dInGui,
                provider.particleTexture,
                provider.parts,
                quads
        )
    }

    fun Sprite.mapUv(uv: IVector2): IVector2 {
        return com.cout970.vector.extensions.vec2Of(
                getInterpolatedU(uv.xd * 16),
                getInterpolatedV(uv.yd * 16)
        )
    }
}