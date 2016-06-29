package com.cout970.magneticraft.proxy

import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import com.cout970.magneticraft.blocks
import com.cout970.magneticraft.client.render.registerInvRender
import com.cout970.magneticraft.client.render.tileentity.TileCrushingTableRenderer
import com.cout970.magneticraft.client.render.tileentity.TileFeedingTroughRenderer
import com.cout970.magneticraft.client.render.tileentity.TileTableSieveRenderer
import com.cout970.magneticraft.item.ItemBase
import com.cout970.magneticraft.items
import com.cout970.magneticraft.tileentity.TileCrushingTable
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import com.cout970.magneticraft.tileentity.TileTableSieve
import com.cout970.magneticraft.util.MODID
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.fml.client.registry.ClientRegistry

class ClientProxy : CommonProxy() {

    override fun preInit() {
        super.preInit()

        OBJLoader.INSTANCE.addDomain(MODID)

        items.forEach(ItemBase::registerInvRender)
        blocks.values.forEach(ItemBlockBase::registerInvRender)

        ClientRegistry.bindTileEntitySpecialRenderer(TileCrushingTable::class.java, TileCrushingTableRenderer)
        ClientRegistry.bindTileEntitySpecialRenderer(TileFeedingTrough::class.java, TileFeedingTroughRenderer)
        ClientRegistry.bindTileEntitySpecialRenderer(TileTableSieve::class.java, TileTableSieveRenderer)
    }

    override fun postInit() {
        super.postInit()
    }
}