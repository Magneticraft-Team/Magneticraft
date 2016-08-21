package com.cout970.magneticraft.proxy

import com.cout970.loader.api.ModelRegistry
import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import com.cout970.magneticraft.client.render.registerInvRender
import com.cout970.magneticraft.client.render.tileentity.*
import com.cout970.magneticraft.client.sounds.registerSounds
import com.cout970.magneticraft.item.ItemBase
import com.cout970.magneticraft.registry.blocks
import com.cout970.magneticraft.registry.items
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.tileentity.TileCrushingTable
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import com.cout970.magneticraft.tileentity.TileTableSieve
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import com.cout970.magneticraft.tileentity.electric.TileElectricPole
import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import com.cout970.magneticraft.tileentity.electric.TileIncendiaryGenerator
import com.cout970.magneticraft.tileentity.multiblock.TileHydraulicPress
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
        items.forEach(ItemBase::registerInvRender)
        blocks.values.forEach(ItemBlockBase::registerInvRender)
        blocks.values.forEach {
            val mapper = it.blockBase.getCustomStateMapper()
            if (mapper != null) {
                ModelLoader.setCustomStateMapper(it.block, mapper)
            }
        }

        //tile entity renderer
        register(TileCrushingTable::class.java, TileCrushingTableRenderer)
        register(TileFeedingTrough::class.java, TileFeedingTroughRenderer)
        register(TileTableSieve::class.java, TileTableSieveRenderer)
        register(TileElectricConnector::class.java, TileElectricConnectorRenderer)
        register(TileElectricPole::class.java, TileElectricPoleRenderer)
        register(TileElectricPoleAdapter::class.java, TileElectricPoleAdapterRenderer)
        register(TileIncendiaryGenerator::class.java, TileIncendiaryGeneratorRenderer)
        register(TileHydraulicPress::class.java, TileHydraulicPressRenderer)

        //registering model bake event listener
        MinecraftForge.EVENT_BUS.register(this)
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