package com.cout970.magneticraft.registry

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.tileentity.TileBox
import com.cout970.magneticraft.tileentity.TileConveyorBelt
import com.cout970.magneticraft.tileentity.TileCrushingTable
import com.cout970.magneticraft.tileentity.core.TileBase
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by cout970 on 2017/06/12.
 */

fun initTileEntities(){
    val map = mutableMapOf<Class<out TileBase>, String>()

    map += TileBox::class.java to "box"
    map += TileCrushingTable::class.java to "crushing_table"
    map += TileConveyorBelt::class.java to "conveyor_belt"

    map.forEach { clazz, name ->
        GameRegistry.registerTileEntity(clazz, "${MOD_ID}_$name")
    }
}