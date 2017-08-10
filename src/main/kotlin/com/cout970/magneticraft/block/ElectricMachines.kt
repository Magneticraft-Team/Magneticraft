package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.CommonMethods
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.TileElectricFurnace
import com.cout970.magneticraft.tileentity.TileInfiniteEnergy
import com.cout970.magneticraft.util.resource
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock

/**
 * Created by cout970 on 2017/06/29.
 */
object ElectricMachines : IBlockMaker {


    lateinit var battery: BlockBase private set
    lateinit var electric_furnace: BlockBase private set
    lateinit var infinite_energy: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        battery = builder.withName("battery").copy {
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileBattery)
            alwaysDropDefault = true
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/battery.mcx"),
                    "inventory" to resource("models/block/mcx/battery.mcx")
            )
            //methods
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        electric_furnace = builder.withName("electric_furnace").copy {
            material = Material.ROCK
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileElectricFurnace)
            alwaysDropDefault = true
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/electric_furnace.mcx"),
                    "inventory" to resource("models/block/mcx/electric_furnace.mcx")
            )
            //methods
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        infinite_energy = builder.withName("infinite_energy").copy {
            factory = factoryOf(::TileInfiniteEnergy)
        }.build()

        return itemBlockListOf(battery, electric_furnace, infinite_energy)
    }
}