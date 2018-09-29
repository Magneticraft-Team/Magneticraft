package com.cout970.magneticraft.features.decoration

import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.RegisterBlocks
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.vector.createAABBUsing
import com.cout970.magneticraft.systems.blocks.*
import com.cout970.magneticraft.systems.itemblocks.itemBlockListOf
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 2017/06/08.
 */
@RegisterBlocks
object Blocks : IBlockMaker {

    val PROPERTY_LIMESTONE_KIND = PropertyEnum.create("limestone_kind", LimestoneKind::class.java)
    val PROPERTY_INVERTED = PropertyEnum.create("inverted", TileInverted::class.java)

    lateinit var limestone: BlockBase private set
    lateinit var burnLimestone: BlockBase private set
    lateinit var tileLimestone: BlockBase private set
    lateinit var tubeLight: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.ROCK
            creativeTab = CreativeTabMg
            states = LimestoneKind.values().toList()
        }

        limestone = builder.withName("limestone").build()
        burnLimestone = builder.withName("burnt_limestone").build()

        tileLimestone = builder.withName("tile_limestone").copy {
            states = TileInverted.values().toList()
        }.build()

        tubeLight = builder.withName("tube_light").copy {
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileTubeLight)
            lightEmission = 1f
            hasCustomModel = true
            generateDefaultItemBlockModel = false
            alwaysDropDefault = true
            customModels = listOf(
                "model" to resource("models/block/mcx/tube_light.mcx"),
                "inventory" to resource("models/block/mcx/tube_light.mcx")
            )
            //methods
            boundingBox = CommonMethods.updateBoundingBoxWithOrientation {
                listOf(Vec3d(PIXEL * 3, 1.0, 0.0) createAABBUsing Vec3d(1.0 - PIXEL * 3, 1.0 - PIXEL * 4, 1.0))
            }
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        return itemBlockListOf(limestone, burnLimestone, tileLimestone, tubeLight)
    }

    enum class LimestoneKind(override val stateName: String,
                             override val isVisible: Boolean) : IStatesEnum, IStringSerializable {

        NORMAL("normal", true),
        BRICK("brick", true),
        COBBLE("cobble", true);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_LIMESTONE_KIND)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_LIMESTONE_KIND, this)
        }
    }

    enum class TileInverted(override val stateName: String,
                            override val isVisible: Boolean) : IStatesEnum, IStringSerializable {

        NORMAL("normal", true),
        INVERTED("inverted", true);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_INVERTED)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_INVERTED, this)
        }
    }
}