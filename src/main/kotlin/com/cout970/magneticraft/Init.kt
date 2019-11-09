package com.cout970.magneticraft

import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.registry.registerMisc
import com.cout970.magneticraft.registry.registerRecipes
import com.cout970.magneticraft.systems.blocks.IBlockMaker
import com.cout970.magneticraft.systems.config.ConfigHandler
import com.cout970.magneticraft.systems.gui.GuiConfig
import com.cout970.magneticraft.systems.gui.components.CompBookRenderer
import com.cout970.magneticraft.systems.integration.IntegrationHandler
import com.cout970.magneticraft.systems.items.IItemMaker
import com.cout970.magneticraft.systems.network.MagneticraftNetwork
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilerenderers.TileRenderer
import com.cout970.magneticraft.systems.worldgen.WorldGenerator
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.IForgeRegistry
import java.util.function.Supplier

object Init {

    fun init(modEventBus: IEventBus) {
        modEventBus.addListener(Init::commonSetup)
        modEventBus.addListener(Init::clientSetup)
        modEventBus.addListener(Init::loadComplete)
        modEventBus.addListener(Init::serverStart)
        MinecraftForge.EVENT_BUS.register(RegistryEvents)
    }

    /**
     * Common setup event, called in the client and the server when the game starts
     */
    fun commonSetup(@Suppress("UNUSED_PARAMETER") e: FMLCommonSetupEvent) {
        info("Starting CommonSetup")

        logTime("CommonSetup done in") {

            // Enables the custom creative tab
            logTime("CreativeTab created in") { CreativeTabMg }

            info("Loading config...")
            logTime("Config loaded in") { ConfigHandler.init() }

            logTime("GuiConfig loaded in") { GuiConfig.loadAll() }

            logTime("Inter Mod integration:") { IntegrationHandler.init() }

            logTime("Task registerMisc:") {
                registerMisc()
            }

//            //Init recipes
            logTime("Task registerRecipes:") {
                registerRecipes()
            }

            //World generator
            logTime("Task registerWorldGenerator:") {
                WorldGenerator.init()
                // TODO register ore generator
//                GameRegistry.registerWorldGenerator(WorldGenerator, 10)
            }

            logTime("Task registerNetworkMessages:") {
                MagneticraftNetwork.init()
            }

            //Gui
//            logTime("Task registerGuiHandler:") {
//                INSTANCE.registerGuiHandler(Magneticraft, GuiHandler)
//            }

            // @formatter:off
//            logTime("Task registerCapabilities:")         { registerCapabilities() }
//            logTime("Task initTileEntities:")             { initTileEntities() }
//            logTime("Task initFluids:")                   { initFluids() }
//            logTime("Task registerOreDictionaryEntries:") { registerOreDictionaryEntries() }
//            logTime("Task registerOreGenerations:")       { registerOreGenerations() }
//            logTime("Task registerMultiblocks:")          { MultiblockManager.registerDefaults() }
            // @formatter:on
        }
    }

    /**
     * Client setup event, called in the client when the game starts
     */
    fun clientSetup(@Suppress("UNUSED_PARAMETER") e: FMLClientSetupEvent) {
        // Model loaders
        OBJLoader.INSTANCE.addDomain(MOD_ID)

        // Force to load the mod manual from disk
        logTime("Task load manual pages:") { CompBookRenderer.book }

        //Item renders
//        logTime("Task registerItemModels:") { registerItemModels() }

        //ItemBlock renders
//        logTime("Task registerBlockAndItemBlockModels:") { registerBlockAndItemBlockModels() }

        //TileEntity renderers
//        logTime("Task registerTileEntityRenderers:") { registerTileEntityRenderers() }

        searchAndRegisterAnnotated()
    }

    /**
     * Load comple event, called after all initialization is done
     */
    fun loadComplete(@Suppress("UNUSED_PARAMETER") e: FMLLoadCompleteEvent) {
        logTime("[FMLLoadCompleteEvent] Inter Mod integration:") {
            IntegrationHandler.postInit()
        }

        if (Debug.DEBUG) {
            Debug.printBlocksWithoutRecipe()
        }
    }

    /**
     * Called while the server is starting
     */
    fun serverStart(@Suppress("UNUSED_PARAMETER") e: FMLServerStartingEvent) {
        if (Debug.DEBUG) {
            Debug.MgCommand.register(e.commandDispatcher)
        }
    }

    fun searchAndRegisterAnnotated() {
        Asm.findAnnotated(RegisterRenderer::class.java).forEach { (clazz, annotation) ->
            try {
                @Suppress("UNCHECKED_CAST")
                val tileClass = annotation["tileEntity"] as Class<TileBase>
                @Suppress("UNCHECKED_CAST")
                val renderer = clazz.kotlin.objectInstance as TileRenderer<TileBase>

                ClientRegistry.bindTileEntitySpecialRenderer(tileClass, renderer)
                RegistryEvents.tileEntityRenderers.add(renderer)

                if (Debug.DEBUG) {
                    info("Registering TESR: Tile = ${clazz.simpleName}, Renderer = ${renderer.javaClass.simpleName}")
                }
            } catch (e: Exception) {
                logError("Unable to register class with @RegisterRenderer: $annotation, class: $clazz")
                e.printStackTrace()
            }
        }

        // TODO update to new library
//        fun registerItemModels() {
//            items.forEach { i ->
//                (i as? ItemBase)?.let { item ->
//                    item.variants.forEach { variant ->
//                        ModelLoader.setCustomModelResourceLocation(
//                            item,
//                            variant.key,
//                            item.registryName!!.toModel(variant.value)
//                        )
//                    }
//
//                    item.customModels.forEach { (state, location) ->
//                        ModelLoaderApi.registerModelWithDecorator(
//                            ModelResourceLocation(item.registryName!!, state),
//                            location,
//                            DefaultBlockDecorator
//                        )
//                    }
//                }
//            }
//        }
//
//        fun registerBlockAndItemBlockModels() {
//            blocks.forEach { (block, itemBlock) ->
//                if (itemBlock == null) return@forEach
//                (block as? BlockBase)?.let { blockBase ->
//                    if (blockBase.generateDefaultItemModel) {
//                        blockBase.inventoryVariants.forEach {
//                            ModelLoader.setCustomModelResourceLocation(
//                                itemBlock,
//                                it.key,
//                                itemBlock.registryName!!.toModel(it.value)
//                            )
//                        }
//                    } else {
//                        ModelLoader.setCustomModelResourceLocation(
//                            itemBlock,
//                            0,
//                            itemBlock.registryName!!.toModel("inventory")
//                        )
//                    }
//                    val mapper = blockBase.getCustomStateMapper()
//                    if (mapper != null) {
//                        ModelLoader.setCustomStateMapper(blockBase, mapper)
//                    }
//                    blockBase.customModels.forEach { (state, location) ->
//                        if (state == "inventory" || blockBase.forceModelBake) {
//                            ModelLoaderApi.registerModelWithDecorator(
//                                modelId = ModelResourceLocation(blockBase.registryName!!, state),
//                                modelLocation = location,
//                                decorator = DefaultBlockDecorator
//                            )
//                        } else {
//                            ModelLoaderApi.registerModel(
//                                modelId = ModelResourceLocation(blockBase.registryName!!, state),
//                                modelLocation = location,
//                                bake = false
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }

    fun initBlocks(registry: IForgeRegistry<Block>) {
        val blocks = mutableListOf<Block>()
        val items = mutableListOf<BlockItem>()

        Asm.forEachAnnotatedObjects<IBlockMaker>(RegisterItems::class.java) { instance ->
            instance.initBlocks().forEach { (block, itemBlock) ->
                registry.register(block)
                blocks += block
                if (itemBlock != null) items += itemBlock
            }

            if (Debug.DEBUG) {
                info("Registering Blocks: ${instance.javaClass.canonicalName}")
            }
        }

        RegistryEvents.blocks.addAll(blocks)
        RegistryEvents.items.addAll(items)
    }

    fun initItems(registry: IForgeRegistry<Item>) {
        val items = mutableListOf<Item>()

        Asm.forEachAnnotatedObjects<IItemMaker>(RegisterItems::class.java) { instance ->

            instance.initItems().forEach { item ->
                registry.register(item)
                items += item
            }

            if (Debug.DEBUG) {
                info("Registering Items: ${instance.javaClass.canonicalName}")
            }
        }

        // ItemBlocks are already in the item list
        RegistryEvents.items.addAll(items)

        RegistryEvents.items.forEach {
            registry.register(it)
        }
    }

    fun initTiles(registry: IForgeRegistry<TileEntityType<*>>) {
        Asm.forEachAnnotatedClass(RegisterTileEntity::class.java) { clazz, annotation ->
            @Suppress("UNCHECKED_CAST")
            val blocks = (annotation["blocks"] as Array<String>)
                .map { ForgeRegistries.BLOCKS.getValue(ResourceLocation(it))!! }
                .toTypedArray()

            @Suppress("UNCHECKED_CAST")
            val tileClass = clazz as Class<TileEntity>

            val type = TileEntityType.Builder
                .create(Supplier { tileClass.newInstance() }, *blocks)
                .build(null)

            type.registryName = resource(annotation["name"] as String)

            registry.register(type)
            RegistryEvents.tiles[clazz] = type

            if (Debug.DEBUG) {
                info("Registering Tile: ${clazz.canonicalName}")
            }
        }
    }
}