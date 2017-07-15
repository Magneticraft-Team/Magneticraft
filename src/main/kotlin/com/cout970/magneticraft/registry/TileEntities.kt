package com.cout970.magneticraft.registry

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.util.info
import com.cout970.magneticraft.util.logError
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by cout970 on 2017/06/12.
 */

fun initTileEntities() {
    val map = mutableMapOf<Class<out TileBase>, String>()

    val data = Magneticraft.asmData.getAll(RegisterTileEntity::class.java.canonicalName)

    data.forEach {
        try {
            @Suppress("UNCHECKED_CAST")
            val clazz = Class.forName(it.className) as Class<TileBase>
            map += clazz to (it.annotationInfo["name"] as String)
            if(Debug.DEBUG) {
                info("Registering TileEntity: ${clazz.simpleName}")
            }
        } catch (e: Exception) {
            logError("Error auto-registering tileEntity: $it")
            e.printStackTrace()
        }
    }

    map.forEach { clazz, name ->
        GameRegistry.registerTileEntity(clazz, "${MOD_ID}_$name")
    }
}

