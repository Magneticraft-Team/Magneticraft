package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.CommonMethods
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.tileentity.TileComputer
import com.cout970.magneticraft.tileentity.TileMiningRobot
import com.cout970.magneticraft.util.resource
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock

/**
 * Created by cout970 on 2017/07/07.
 */
object Computers : IBlockMaker {

    lateinit var computer: BlockBase private set
    lateinit var miningRobot: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
            alwaysDropDefault = true
        }

        computer = builder.withName("computer").copy {
            factory = factoryOf(::TileComputer)
            states = CommonMethods.Orientation.values().toList()
            hasCustomModel = true
            generateDefaultItemModel = false
            customModels = listOf(
                    "model" to resource("models/block/mcx/computer.mcx"),
                    "inventory" to resource("models/block/mcx/computer.mcx")
            )
            //methods
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
            onActivated = CommonMethods::delegateToModule
        }.build()

        miningRobot = builder.withName("mining_robot").copy {
            factory = factoryOf(::TileMiningRobot)
            states = CommonMethods.Facing.values().toList()
            hasCustomModel = true
            generateDefaultItemModel = false
            customModels = listOf(
                    "model" to resource("models/block/mcx/mining_robot.mcx"),
                    "inventory" to resource("models/block/mcx/mining_robot.mcx")
            )
            //methods
            onBlockPlaced = CommonMethods::placeWithFacing
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
            onActivated = CommonMethods::delegateToModule
        }.build()

        return itemBlockListOf(computer, miningRobot)
    }
}