package com.cout970.magneticraft.systems.tilerenderers

import com.cout970.magneticraft.misc.addPostfix
import com.cout970.magneticraft.misc.addPrefix
import com.cout970.magneticraft.misc.logError
import com.cout970.magneticraft.misc.warn
import com.cout970.modelloader.api.*
import com.cout970.modelloader.api.animation.AnimatedModel
import com.cout970.modelloader.api.formats.gltf.GltfAnimationBuilder
import com.cout970.modelloader.api.formats.gltf.GltfStructure
import com.cout970.modelloader.api.formats.mcx.McxModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureMap

/**
 * Created by cout970 on 2017/06/16.
 */
object ModelCacheFactory {

    fun createModel(loc: ModelResourceLocation, filters: List<ModelSelector>,
                    useTextures: Boolean, time: () -> Double, createDefault: Boolean = true): Map<String, IRenderCache> {

        val models = mutableMapOf<String, IRenderCache>()

        val (baked, model) = ModelLoaderApi.getModelEntry(loc) ?: return models

        when (model) {
            is Model.Mcx -> processMcx(model, filters, useTextures, models, createDefault)
            is Model.Gltf -> processGltf(model, filters, useTextures, time, models, createDefault)
            is Model.Obj -> {
                if (createDefault) {
                    if (baked != null) {
                        val cache = ModelCache { ModelUtilities.renderModel(baked) }
                        models["default"] = if (useTextures) TextureModelCache(TextureMap.LOCATION_BLOCKS_TEXTURE, cache) else cache
                    } else {
                        logError("Error: trying to render a obj model that is not backed, this is not supported!")
                    }
                }
            }
            Model.Missing -> {
                warn("Model for $loc not found")
            }
        }

        return models
    }

    private fun removeTextures(model: AnimatedModel): AnimatedModel {
        return AnimatedModel(model.rootNodes.map { removeTextures(it) }, model.channels)
    }

    private fun removeTextures(node: AnimatedModel.Node): AnimatedModel.Node {
        return node.copy(
            children = node.children.map { removeTextures(it) },
            cache = removeTextures(node.cache)
        )
    }

    private fun removeTextures(renderCache: IRenderCache): IRenderCache {
        return when (renderCache) {
            is ModelGroupCache -> ModelGroupCache(*renderCache.cache.map { removeTextures(it) }.toTypedArray())
            is TextureModelCache -> ModelGroupCache(*renderCache.cache)
            else -> renderCache
        }
    }

    private fun processMcx(model: Model.Mcx, filters: List<ModelSelector>, useTextures: Boolean,
                           models: MutableMap<String, IRenderCache>, createDefault: Boolean) {

        val sections = filters.map { (name, filterFunc) ->
            val parts = model.data.parts.filter { filterFunc(it.name, FilterTarget.LEAF) }
            name to parts
        }

        val notUsed = if (filters.any { it.animationFilter != IGNORE_ANIMATION }) {
            model.data.parts
        } else {
            val used = sections.flatMap { it.second }.toSet()
            model.data.parts.filter { it !in used }
        }

        fun store(name: String, partList: List<McxModel.Part>) {
            partList.groupBy { it.texture }.forEach { tex, parts ->
                val cache = ModelCache { ModelUtilities.renderModelParts(model.data, parts) }
                val texture = tex.addPrefix("textures/").addPostfix(".png")
                val obj = if (useTextures) TextureModelCache(texture, cache) else cache

                if (name in models) {
                    val old = models[name]!!

                    if (old is ModelGroupCache) {
                        models[name] = ModelGroupCache(*old.cache, obj)
                    } else {
                        models[name] = ModelGroupCache(old, obj)
                    }
                } else {
                    models[name] = obj
                }
            }
        }

        sections.forEach { store(it.first, it.second) }
        if (createDefault) store("default", notUsed)
    }

    private fun processGltf(model: Model.Gltf, filters: List<ModelSelector>, useTextures: Boolean, time: () -> Double, models: MutableMap<String, IRenderCache>,
                            createDefault: Boolean) {

        val scene = model.data.structure.scenes[0]
        val namedAnimations = model.data.structure.animations.mapIndexed { index, animation ->
            animation.name ?: index.toString()
        }

        val sections = filters.map { (name, filter, animation) ->
            Triple(name, scene.nodes.flatMap { it.recursiveFilter(filter) }.toSet(), animation)
        }

        val allNodes = (0 until model.data.definition.nodes.size).toSet()

        fun store(name: String, nodes: Set<Int>, filter: Filter) {
            val exclusions = (0 until model.data.definition.nodes.size).filter { it !in nodes }.toSet()
            val validAnimations = namedAnimations.filter { filter(it, FilterTarget.ANIMATION) }

            val builder = GltfAnimationBuilder()
                .also { it.excludedNodes = exclusions }
                .also { it.transformTexture = { tex -> tex.addPrefix("textures/").addPostfix(".png") } }

            val newModel = if (validAnimations.isEmpty()) {
                builder.buildPlain(model.data)
            } else {
                builder.build(model.data).first { it.first in validAnimations }.second
            }

            val obj = if (useTextures) {
                AnimationRenderCache(newModel, time)
            } else {
                AnimationRenderCache(removeTextures(newModel), time)
            }

            if (name in models) {
                val old = models[name]!!

                if (old is ModelGroupCache) {
                    models[name] = ModelGroupCache(*old.cache, obj)
                } else {
                    models[name] = ModelGroupCache(old, obj)
                }
            } else {
                models[name] = obj
            }
        }

        sections.forEach { store(it.first, it.second, it.third) }
        if (createDefault) store("default", allNodes, IGNORE_ANIMATION)
    }

    private fun GltfStructure.Node.recursiveFilter(filter: Filter): Set<Int> {
        val children = children.flatMap { it.recursiveFilter(filter) }.toSet()
        val all = children + setOf(index)
        val name = name ?: return all
        val type = if (mesh == null) FilterTarget.BRANCH else FilterTarget.LEAF

        return if (filter(name, type)) all else emptySet()
    }
}

class AnimationRenderCache(val model: AnimatedModel, val time: () -> Double) : IRenderCache {
    override fun render() = model.render(time())
    override fun close() = model.rootNodes.close()

    private fun List<AnimatedModel.Node>.close(): Unit = forEach {
        it.cache.close()
        it.children.close()
    }
}