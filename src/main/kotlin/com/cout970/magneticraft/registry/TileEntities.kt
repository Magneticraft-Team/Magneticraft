package com.cout970.magneticraft.registry

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.tileentity.TileBox
import com.cout970.magneticraft.tileentity.core.TileBase
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by cout970 on 2017/06/12.
 */

fun initTileEntities(){
    val map = mutableMapOf<Class<out TileBase>, String>()

    map += TileBox::class.java to "box"

    map.forEach { clazz, name ->
        GameRegistry.registerTileEntity(clazz, "${MOD_ID}_$name")
    }
}