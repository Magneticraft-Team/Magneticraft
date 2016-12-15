package com.cout970.magneticraft.proxy

import coffee.cypher.mcextlib.extensions.resources.toModel
import com.cout970.loader.api.ModelRegistry
import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import com.cout970.magneticraft.gui.client.tooltipHandler
import com.cout970.magneticraft.item.ItemBase
import com.cout970.magneticraft.registry.blocks
import com.cout970.magneticraft.registry.items
import com.cout970.magneticraft.registry.registerColorHandlers
import com.cout970.magneticraft.registry.registerSounds
import com.cout970.magneticraft.tileentity.*
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import com.cout970.magneticraft.tileentity.electric.TileElectricPole
import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import com.cout970.magneticraft.tileentity.electric.TileIncendiaryGenerator
import com.cout970.magneticraft.tileentity.multiblock.*
import com.cout970.magneticraft.tilerenderer.*
import com.cout970.magneticraft.util.MOD_ID
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

/**
 * This class extends the functionality of CommonProxy but adds
 * thing only for the client: sounds, models, textures and renders
 */
class ClientProxy : CommonProxy() {

    //List of registered TileEntityRenderers
    val tileRenderers = mutableListOf<TileEntityRenderer<out TileBase>>()

    override fun preInit() {
        super.preInit()

        //Sounds
        registerSounds()

        //Model loaders
        OBJLoader.INSTANCE.addDomain(MOD_ID)
        //This is from other library
        ModelRegistry.registerDomain(MOD_ID)

        //Item renders
        items.forEach { it.registerInvRender() }
        //ItemBlock renders
        blocks.values.forEach {
            it.registerInvRender()
            val mapper = it.blockBase.getCustomStateMapper()
            if (mapper != null) {
                ModelLoader.setCustomStateMapper(it.block, mapper)
            }
        }

        //TileEntity renderers
        register(TileCrushingTable::class.java, TileRendererCrushingTable)
        register(TileFeedingTrough::class.java, TileRendererFeedingTrough)
        register(TileTableSieve::class.java, TileRendererTableSieve)
        register(TileElectricConnector::class.java, TileRendererElectricConnector)
        register(TileElectricPole::class.java, TileRendererElectricPole)
        register(TileElectricPoleAdapter::class.java, TileRendererElectricPoleAdapter)
        register(TileIncendiaryGenerator::class.java, TileRendererIncendiaryGenerator)
        register(TileHydraulicPress::class.java, TileRendererHydraulicPress)
        register(TileKiln::class.java, TileRendererKiln)
        register(TileSifter::class.java, TileRendererSifter)
        register(TileGrinder::class.java, TileRendererGrinder)
        register(TileKilnShelf::class.java, TileRendererKilnShelf)
        register(TileSolarPanel::class.java, TileRendererSolarPanel)

        //registering model bake event listener, for TESR (TileEntitySpecialRenderer) model reloading
        MinecraftForge.EVENT_BUS.register(this)

        MinecraftForge.EVENT_BUS.register(tooltipHandler())
    }

    fun ItemBase.registerInvRender() {
        variants.forEach {
            ModelLoader.setCustomModelResourceLocation(this, it.key, registryName.toModel(it.value))
        }
    }

    fun ItemBlockBase.registerInvRender() {
        blockBase.inventoryVariants.forEach {
            ModelLoader.setCustomModelResourceLocation(this, it.key, registryName.toModel(it.value))
        }
    }

    override fun init() {
        super.init()
        registerColorHandlers()
    }

    override fun postInit() {
        super.postInit()
    }

    override fun getSide() = Side.CLIENT

    /**
     * Updates all the TileEntityRenderers to reload models
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onModelRegistryReload(event: ModelBakeEvent) {
        tileRenderers.forEach { it.onModelRegistryReload() }
    }

    /**
     * Binds a TileEntity class with a TileEntityRenderer and
     * registers the TileEntityRenderer to update it when ModelBakeEvent is fired
     */
    private fun <T : TileBase> register(tileEntityClass: Class<T>, specialRenderer: TileEntityRenderer<T>) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, specialRenderer)
        tileRenderers.add(specialRenderer)
    }
}