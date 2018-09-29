package com.cout970.magneticraft.registry

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.info
import com.cout970.magneticraft.misc.logError
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.systems.tileentities.TileBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.datafix.FixTypes
import net.minecraft.util.datafix.IFixableData
import net.minecraftforge.fml.common.FMLCommonHandler
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
            if (Debug.DEBUG) {
                info("Registering TileEntity: ${clazz.simpleName}")
            }
        } catch (e: Exception) {
            logError("Error auto-registering tileEntity: $it")
            e.printStackTrace()
        }
    }


    val datafixer = FMLCommonHandler.instance().dataFixer.init(MOD_ID, 2)

    datafixer.registerFix(FixTypes.BLOCK_ENTITY, FixTileEntityId)

    map.forEach { clazz, name ->
        GameRegistry.registerTileEntity(clazz, resource(name))
    }
}

object FixTileEntityId : IFixableData {

    override fun getFixVersion(): Int = 2

    override fun fixTagCompound(compound: NBTTagCompound): NBTTagCompound {
        val oldId = compound.getString("id")
        val loc = ResourceLocation(oldId)

        if (loc.resourceDomain != MOD_ID && loc.resourcePath.startsWith("${MOD_ID}_")) {
            val newId = resource(loc.resourcePath.replaceFirst("${MOD_ID}_", ""))

            compound.setString("id", newId.toString())
        }
        return compound
    }
}

