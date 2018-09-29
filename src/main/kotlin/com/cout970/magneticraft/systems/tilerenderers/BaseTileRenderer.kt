package com.cout970.magneticraft.systems.tilerenderers

import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.modelloader.api.IRenderCache
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation

abstract class BaseTileRenderer<T : TileBase> : TileRenderer<T>() {

    private val models = mutableMapOf<String, IRenderCache>()
    protected var time: Double = 0.0
    var ticks: Float = 0f

    abstract fun init()

    abstract fun render(te: T)

    override fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        stackMatrix {
            translate(x, y, z)
            ticks = partialTicks
            time = (te.world.totalWorldTime and 0xFF_FFFF).toDouble() + partialTicks
            render(te)
        }
    }

    override fun onModelRegistryReload() {
        models.forEach { entry -> entry.value.close() }
        models.clear()
        init()
    }


    fun createModel(block: Block, vararg filters: ModelSelector) {
        createModel(block, filters.toList())
    }

    fun createModel(block: Block, filters: List<ModelSelector>, variant: String = "model", createDefault: Boolean = true) {
        models += ModelCacheFactory.createModel(
            loc = ModelResourceLocation(block.registryName!!, variant),
            filters = filters,
            useTextures = true,
            time = { time },
            createDefault = createDefault
        )
    }

    fun createModelWithoutTexture(block: Block, vararg filters: ModelSelector) {
        models += ModelCacheFactory.createModel(
            loc = ModelResourceLocation(block.registryName!!, "model"),
            filters = filters.toList(),
            useTextures = false,
            time = { time }
        )
    }

    fun renderModel(key: String) {
        models[key]?.render()
    }

    fun getModel(key: String) = models[key]
}