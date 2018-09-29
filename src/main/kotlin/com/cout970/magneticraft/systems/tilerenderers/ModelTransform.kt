package com.cout970.magneticraft.systems.tilerenderers

import com.cout970.magneticraft.Sprite
import com.cout970.modelloader.api.formats.gltf.GltfAttribute
import com.cout970.modelloader.api.formats.gltf.GltfModel
import com.cout970.modelloader.api.formats.gltf.GltfStructure
import com.cout970.modelloader.api.formats.mcx.McxModel
import com.cout970.modelloader.api.formats.mcx.Mesh
import com.cout970.vector.api.IVector2
import net.minecraft.client.renderer.texture.TextureMap

object ModelTransform {

    fun updateModelUvs(provider: GltfModel, sprite: Sprite): GltfModel {
        val visitor = GltfStructureVisitor { prim ->

            val buffer = prim.attributes[GltfAttribute.TEXCOORD_0] ?: return@GltfStructureVisitor prim
            val newAttribs = prim.attributes.toMutableMap()

            val newBuffer = buffer.copy(data = buffer.data.map { it as IVector2 }.map { sprite.mapUv(it) })

            newAttribs[GltfAttribute.TEXCOORD_0] = newBuffer
            GltfStructure.Primitive(newAttribs, prim.indices, prim.mode, TextureMap.LOCATION_BLOCKS_TEXTURE)
        }

        val structure = visitor.visitStructure(provider.structure)
        return GltfModel(provider.location, provider.definition, structure)
    }

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

class GltfStructureVisitor(val func: (GltfStructure.Primitive) -> GltfStructure.Primitive) {

    fun visitStructure(file: GltfStructure.File): GltfStructure.File {
        return GltfStructure.File(file.scenes.map { visitScene(it) }, file.animations)
    }

    fun visitScene(scene: GltfStructure.Scene): GltfStructure.Scene {
        return GltfStructure.Scene(scene.nodes.map { visitNode(it) })
    }

    fun visitNode(node: GltfStructure.Node): GltfStructure.Node {
        return GltfStructure.Node(node.index, node.children.map { visitNode(it) }, node.transform, visitMesh(node.mesh), node.name)
    }

    fun visitMesh(mesh: GltfStructure.Mesh?): GltfStructure.Mesh? {
        if (mesh == null) return null
        return GltfStructure.Mesh(mesh.primitives.map { visitPrimitive(it) })
    }

    fun visitPrimitive(prim: GltfStructure.Primitive): GltfStructure.Primitive = func(prim)
}