package com.cout970.magneticraft.proxy

import coffee.cypher.mcextlib.extensions.resources.toModel
import com.cout970.loader.api.ModelRegistry
import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import com.cout970.magneticraft.item.ItemBase
import com.cout970.magneticraft.registry.blocks
import com.cout970.magneticraft.registry.items
import com.cout970.magneticraft.registry.registerSounds
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.tileentity.TileCrushingTable
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import com.cout970.magneticraft.tileentity.TileTableSieve
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import com.cout970.magneticraft.tileentity.electric.TileElectricPole
import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import com.cout970.magneticraft.tileentity.electric.TileIncendiaryGenerator
import com.cout970.magneticraft.tileentity.multiblock.TileHydraulicPress
import com.cout970.magneticraft.tileentity.multiblock.TileSolarPanel
import com.cout970.magneticraft.tilerenderer.*
import com.cout970.magneticraft.util.MODID
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

class ClientProxy : CommonProxy() {

    //List of registered TileEntityRenderers
    val tileRenderers = mutableListOf<TileEntityRenderer<out TileBase>>()

    override fun preInit() {
        super.preInit()

        //sounds
        registerSounds()

        //model loaders
        OBJLoader.INSTANCE.addDomain(MODID)
        ModelRegistry.registerDomain(MODID)

        //item renders
        items.forEach { it.registerInvRender() }
        blocks.values.forEach {
            it.registerInvRender()
            val mapper = it.blockBase.getCustomStateMapper()
            if (mapper != null) {
                ModelLoader.setCustomStateMapper(it.block, mapper)
            }
        }

        //tile entity renderer
        register(TileCrushingTable::class.java, TileRendererCrushingTable)
        register(TileFeedingTrough::class.java, TileRendererFeedingTrough)
        register(TileTableSieve::class.java, TileRendererTableSieve)
        register(TileElectricConnector::class.java, TileRendererElectricConnector)
        register(TileElectricPole::class.java, TileRendererElectricPole)
        register(TileElectricPoleAdapter::class.java, TileRendererElectricPoleAdapter)
        register(TileIncendiaryGenerator::class.java, TileRendererIncendiaryGenerator)
        register(TileHydraulicPress::class.java, TileRendererHydraulicPress)
        register(TileSolarPanel::class.java, TileRendererSolarPanel)

        //registering model bake event listener
        MinecraftForge.EVENT_BUS.register(this)
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

    override fun postInit() {
        super.postInit()
    }

    /**
     * The side of the proxy
     */
    override fun getSide() = Side.CLIENT

    /**
     * Updates all the TileEntityRenderer to reload models
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