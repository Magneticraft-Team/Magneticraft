package com.cout970.magneticraft.proxy


import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.registry.blocks
import com.cout970.magneticraft.registry.items
import com.cout970.magneticraft.registry.registerSounds
import com.cout970.magneticraft.systems.blocks.BlockBase
import com.cout970.magneticraft.systems.gui.components.CompBookRenderer
import com.cout970.magneticraft.systems.items.ItemBase
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilerenderers.TileRenderer
import com.cout970.modelloader.api.DefaultBlockDecorator
import com.cout970.modelloader.api.ModelLoaderApi
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

/**
 * This class extends the functionality of CommonProxy but adds
 * thing only for the client: sounds, models, textures and renders
 */
@Suppress("unused")
class ClientProxy : CommonProxy() {

    // List of registered TileEntityRenderers
    val tileRenderers = mutableListOf<TileRenderer<out TileBase>>()


    @SubscribeEvent
    fun initSoundsEvent(event: RegistryEvent.Register<SoundEvent>) {
        logTime("Task registerSounds:") { registerSounds(event.registry) }
    }

    override fun postItemRegister() {
        super.postItemRegister()

        //Item renders
        logTime("Task registerItemModels:") { registerItemModels() }

        //ItemBlock renders
        logTime("Task registerBlockAndItemBlockModels:") { registerBlockAndItemBlockModels() }

        //TileEntity renderers
        logTime("Task registerTileEntityRenderers:") { registerTileEntityRenderers() }
    }

    override fun preInit() {
        super.preInit()

        //Model loaders
        OBJLoader.INSTANCE.addDomain(MOD_ID)

        // Preload guidebook
        logTime("Task loadGuideBookPages:") { CompBookRenderer.book }
    }

    fun registerItemModels() {
        items.forEach { i ->
            (i as? ItemBase)?.let { item ->
                item.variants.forEach { variant ->
                    ModelLoader.setCustomModelResourceLocation(
                        item,
                        variant.key,
                        item.registryName!!.toModel(variant.value)
                    )
                }

                item.customModels.forEach { (state, location) ->
                    ModelLoaderApi.registerModelWithDecorator(
                        ModelResourceLocation(item.registryName!!, state),
                        location,
                        DefaultBlockDecorator
                    )
                }
            }
        }
    }

    fun registerBlockAndItemBlockModels() {
        blocks.forEach { (block, itemBlock) ->
            if (itemBlock == null) return@forEach
            (block as? BlockBase)?.let { blockBase ->
                if (blockBase.generateDefaultItemModel) {
                    blockBase.inventoryVariants.forEach {
                        ModelLoader.setCustomModelResourceLocation(
                            itemBlock,
                            it.key,
                            itemBlock.registryName!!.toModel(it.value)
                        )
                    }
                } else {
                    ModelLoader.setCustomModelResourceLocation(
                        itemBlock,
                        0,
                        itemBlock.registryName!!.toModel("inventory")
                    )
                }
                val mapper = blockBase.getCustomStateMapper()
                if (mapper != null) {
                    ModelLoader.setCustomStateMapper(blockBase, mapper)
                }
                blockBase.customModels.forEach { (state, location) ->
                    if (state == "inventory" || blockBase.forceModelBake) {
                        ModelLoaderApi.registerModelWithDecorator(
                            modelId = ModelResourceLocation(blockBase.registryName!!, state),
                            modelLocation = location,
                            decorator = DefaultBlockDecorator
                        )
                    } else {
                        ModelLoaderApi.registerModel(
                            modelId = ModelResourceLocation(blockBase.registryName!!, state),
                            modelLocation = location,
                            bake = false
                        )
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun registerTileEntityRenderers() {
        val data = Magneticraft.asmData.getAll(RegisterRenderer::class.java.canonicalName)
        data.forEach {
            try {
                val clazz = Class.forName(it.className).kotlin
                val annotation = clazz.annotations.find { it is RegisterRenderer } as RegisterRenderer

                val tile = annotation.tileEntity.java as Class<TileBase>
                val renderer = clazz.objectInstance as TileRenderer<TileBase>

                register(tile, renderer)
                if (Debug.DEBUG) {
                    info("Registering TESR: Tile = ${clazz.simpleName}, Renderer = ${renderer.javaClass.simpleName}")
                }
            } catch (e: Exception) {
                logError("Unable to register class with @RegisterRenderer: $it")
                e.printStackTrace()
            }
        }
    }

    override fun getSide() = Side.CLIENT

    /**
     * Updates all the TileEntityRenderers to reload models
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    @SubscribeEvent
    fun onModelRegistryReload(event: ModelBakeEvent) {
        tileRenderers.forEach { it.onModelRegistryReload() }
    }

    /**
     * Binds a TileEntity class with a TileEntityRenderer and
     * registers the TileEntityRenderer to update it when ModelBakeEvent is fired
     */
    private fun <T : TileBase> register(tileEntityClass: Class<T>, specialRenderer: TileRenderer<T>) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, specialRenderer)
        tileRenderers.add(specialRenderer)
    }
}