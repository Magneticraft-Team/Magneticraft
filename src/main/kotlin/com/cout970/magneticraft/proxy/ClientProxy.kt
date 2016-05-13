package com.cout970.magneticraft.proxy

import coffee.cypher.mcextlib.extensions.blocks.item
import com.cout970.magneticraft.blocks
import com.cout970.magneticraft.client.render.registerInvRender
import com.cout970.magneticraft.items
import net.minecraft.block.Block

class ClientProxy : CommonProxy() {
    override fun preInit() {
        super.preInit()

        (items + blocks.map(Block::item)).forEach {
            it.registerInvRender()
        }
    }

    override fun postInit() {
        super.postInit()
    }
}