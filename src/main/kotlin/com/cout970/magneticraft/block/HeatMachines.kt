package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.CommonMethods
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.tileentity.TileCombustionChamber
import com.cout970.magneticraft.tileentity.TileSteamBoiler
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.scale
import com.cout970.magneticraft.util.vector.toAABBWith
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock

/**
 * Created by cout970 on 2017/08/10.
 */
object HeatMachines : IBlockMaker {

    lateinit var combustionChamber: BlockBase private set
    lateinit var steamBoiler: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        combustionChamber = builder.withName("combustion_chamber").copy {
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileCombustionChamber)
            customModels = listOf(
                    "model" to resource("models/block/mcx/combustion_chamber.mcx"),
                    "inventory" to resource("models/block/mcx/combustion_chamber.mcx")
            )
            hasCustomModel = true
            generateDefaultItemModel = false
            alwaysDropDefault = true
            //methods
            boundingBox = CommonMethods.updateBoundingBoxWithOrientation {
                listOf(
                        (vec3Of(0, 0, 0) toAABBWith vec3Of(16, 12, 15)).scale(PIXEL),
                        (vec3Of(0, 12, 0) toAABBWith vec3Of(16, 16, 16)).scale(PIXEL),
                        (vec3Of(3, 2, 15) toAABBWith vec3Of(13, 10, 16)).scale(PIXEL)
                )
            }
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::delegateToModule
        }.build()

        steamBoiler = builder.withName("steam_boiler").copy {
            factory = factoryOf(::TileSteamBoiler)
            customModels = listOf(
                    "model" to resource("models/block/mcx/steam_boiler.mcx"),
                    "inventory" to resource("models/block/mcx/steam_boiler.mcx")
            )
            generateDefaultItemModel = false
            hasCustomModel = true
            //methods
            boundingBox = { listOf((vec3Of(1, 0, 1) toAABBWith vec3Of(15, 16, 15)).scale(PIXEL)) }
            onActivated = CommonMethods::delegateToModule
        }.build()

        return itemBlockListOf(combustionChamber, steamBoiler)
    }
}