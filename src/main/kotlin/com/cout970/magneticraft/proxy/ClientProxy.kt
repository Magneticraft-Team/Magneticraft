package com.cout970.magneticraft.proxy

import coffee.cypher.mcextlib.extensions.blocks.item
import com.cout970.magneticraft.blocks
import com.cout970.magneticraft.client.render.registerInvRender
import com.cout970.magneticraft.items
import com.cout970.magneticraft.tileentity.TileTableSieve
import com.cout970.magneticraft.tilerender.TileRendererTableSieve
import com.cout970.magneticraft.util.MODID
import net.minecraft.block.Block
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.fml.client.registry.ClientRegistry

class ClientProxy : CommonProxy() {

    override fun preInit() {
        super.preInit()

        OBJLoader.INSTANCE.addDomain(MODID)

        (items + blocks.map(Block::item)).forEach {
            it.registerInvRender()
        }

        ClientRegistry.bindTileEntitySpecialRenderer(TileTableSieve::class.java, TileRendererTableSieve)
    }

    override fun postInit() {
        super.postInit()
    }
}