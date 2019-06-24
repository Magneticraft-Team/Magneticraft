package com.cout970.magneticraft.systems.gui.json

import com.cout970.magneticraft.MOD_ID
import com.google.gson.GsonBuilder
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.fml.common.Loader
import java.nio.file.Path

data class GuiConfig(
    val background: Background,
    val slots: List<SlotGroup>,
    val components: List<Component>,
    val playerInventory: Boolean
) {
    data class Background(
        val sizeX: Int, val sizeY: Int
    )

    data class SlotGroup(
        val startIndex: Int,
        val rows: Int,
        val columns: Int,
        val posX: Int,
        val posY: Int,
        val icon: String // default, in, out, filter
    )

    data class Component(
        val posX: Int,
        val posY: Int
    )

    companion object {

        val allConfigs = mutableMapOf<String, GuiConfig>()

        fun loadAll() {
            val container = Loader.instance().indexedModList[MOD_ID]!!
            val paths = mutableListOf<Path>()

            CraftingHelper.findFiles(
                container,
                "assets/$MOD_ID/gui",
                null,
                { _, file ->
                    if (file.toFile().isFile) {
                        paths.add(file)
                    }
                    true
                },
                false,
                false
            )

            val gson = GsonBuilder().create()

            val objects = paths.map { it.toFile() }
                .map { file ->
                    file.nameWithoutExtension to gson.fromJson(file.inputStream().bufferedReader(), GuiConfig::class.java)
                }
                .toMap()

            allConfigs.clear()
            allConfigs.putAll(objects)
        }
    }
}

