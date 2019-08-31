package com.cout970.magneticraft.features.electric_machines

import com.cout970.magneticraft.features.heat_machines.TileElectricHeater
import com.cout970.magneticraft.features.heat_machines.TileRfHeater
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.RegisterBlocks
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.systems.blocks.*
import com.cout970.magneticraft.systems.blocks.CommonMethods.propertyOf
import com.cout970.magneticraft.systems.itemblocks.blockListOf
import com.cout970.magneticraft.systems.itemblocks.itemBlockListOf
import com.cout970.magneticraft.systems.tilemodules.ModuleWindTurbine
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.IStringSerializable

/**
 * Created by cout970 on 2017/06/29.
 */
@RegisterBlocks
object Blocks : IBlockMaker {

    val PROPERTY_DECAY_MODE: PropertyEnum<DecayMode> = propertyOf("decay_mode")
    val PROPERTY_WORKING_MODE: PropertyEnum<WorkingMode> = propertyOf("working_mode")

    lateinit var battery: BlockBase private set
    lateinit var electricFurnace: BlockBase private set
    lateinit var infiniteEnergy: BlockBase private set
    lateinit var airLock: BlockBase private set
    lateinit var airBubble: BlockBase private set
    lateinit var thermopile: BlockBase private set
    lateinit var windTurbine: BlockBase private set
    lateinit var windTurbineGap: BlockBase private set
    lateinit var electricHeater: BlockBase private set
    lateinit var rfHeater: BlockBase private set
    lateinit var rfTransformer: BlockBase private set
    lateinit var electricEngine: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock?>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        battery = builder.withName("battery").copy {
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileBattery)
            alwaysDropDefault = true
            generateDefaultItemBlockModel = false
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

        electricFurnace = builder.withName("electric_furnace").copy {
            material = Material.ROCK
            states = CommonMethods.OrientationActive.values().toList()
            factory = factoryOf(::TileElectricFurnace)
            alwaysDropDefault = true
            //methods
            onBlockPlaced = CommonMethods::placeInactiveWithOppositeOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        infiniteEnergy = builder.withName("infinite_energy").copy {
            factory = factoryOf(::TileInfiniteEnergy)
        }.build()

        airBubble = builder.withName("air_bubble").copy {
            states = DecayMode.values().toList()
            material = Material.GLASS
            tickRandomly = true
            blockLayer = BlockRenderLayer.CUTOUT
            hasCustomModel = true
            tickRate = 1
            onNeighborChanged = {
                if (it.state[PROPERTY_DECAY_MODE]?.enable == true && it.worldIn.rand.nextBoolean()) {
                    it.worldIn.setBlockToAir(it.pos)
                }
            }
            onUpdateTick = {
                if (it.state[PROPERTY_DECAY_MODE]?.enable == true) {
                    it.world.setBlockToAir(it.pos)
                }
            }
            onDrop = { emptyList() }
            collisionBox = { null }
            shouldSideBeRendered = func@{
                val state = it.blockAccess.getBlockState(it.pos.offset(it.side))
                if (state.block === it.state.block) false
                else !state.doesSideBlockRendering(it.blockAccess, it.pos.offset(it.side), it.side.opposite)
            }
        }.build()

        airLock = builder.withName("airlock").copy {
            factory = factoryOf(::TileAirLock)
            onActivated = CommonMethods::openGui
        }.build()

        thermopile = builder.withName("thermopile").copy {
            factory = factoryOf(::TileThermopile)
            //methods
            onActivated = CommonMethods::openGui
        }.build()

        windTurbine = builder.withName("wind_turbine").copy {
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileWindTurbine)
            alwaysDropDefault = true
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            customModels = listOf(
                "model" to resource("models/block/mcx/wind_turbine.mcx")
            )
            //methods
            onActivated = CommonMethods::openGui
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        windTurbineGap = builder.withName("wind_turbine_gap").copy {
            factory = factoryOf(::TileWindTurbineGap)
            hasCustomModel = true
            onDrop = { emptyList() }
            onBlockBreak = func@{
                val thisTile = it.worldIn.getTile<TileWindTurbineGap>(it.pos) ?: return@func
                val pos = thisTile.centerPos ?: return@func
                val mod = it.worldIn.getModule<ModuleWindTurbine>(pos) ?: return@func
                if (mod.hasTurbineHitbox) {
                    mod.removeTurbineHitbox()
                }
                it.worldIn.removeTileEntity(it.pos)
            }
            blockFaceShape = { BlockFaceShape.UNDEFINED }
        }.build()

        electricHeater = builder.withName("electric_heater").copy {
            states = WorkingMode.values().toList()
            factory = factoryOf(::TileElectricHeater)
            alwaysDropDefault = true
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        rfHeater = builder.withName("rf_heater").copy {
            states = WorkingMode.values().toList()
            factory = factoryOf(::TileRfHeater)
            alwaysDropDefault = true
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        rfTransformer = builder.withName("rf_transformer").copy {
            factory = factoryOf(::TileRfTransformer)
            onActivated = CommonMethods::openGui
        }.build()

        electricEngine = builder.withName("electric_engine").copy {
            states = CommonMethods.Facing.values().toList()
            factory = factoryOf(::TileElectricEngine)
            alwaysDropDefault = true
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/gltf/electric_engine.gltf"),
                    "inventory" to resource("models/block/gltf/electric_engine.gltf")
            )
            //methods
            onActivated = CommonMethods::openGui
            onBlockPlaced = CommonMethods::placeWithOppositeFacing
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        return itemBlockListOf(battery, electricFurnace, infiniteEnergy, airBubble, airLock, thermopile,
            windTurbine, electricHeater, rfHeater, rfTransformer, electricEngine) + blockListOf(windTurbineGap)
    }

    enum class WorkingMode(
        override val stateName: String,
        override val isVisible: Boolean,
        val enable: Boolean
    ) : IStatesEnum, IStringSerializable {

        OFF("off", true, false),
        ON("on", false, true);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_WORKING_MODE)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_WORKING_MODE, this)
        }
    }

    enum class DecayMode(
        override val stateName: String,
        override val isVisible: Boolean,
        val enable: Boolean
    ) : IStatesEnum, IStringSerializable {

        OFF("off", true, false),
        ON("on", false, true);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_DECAY_MODE)

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_DECAY_MODE, this)
        }
    }
}