package com.cout970.magneticraft.features.multiblocks

import com.cout970.magneticraft.features.multiblocks.structures.*
import com.cout970.magneticraft.features.multiblocks.tileentities.*
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.RegisterBlocks
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.player.sendMessage
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.systems.blocks.*
import com.cout970.magneticraft.systems.itemblocks.blockListOf
import com.cout970.magneticraft.systems.itemblocks.itemBlockListOf
import com.cout970.magneticraft.systems.multiblocks.Multiblock
import com.cout970.magneticraft.systems.multiblocks.MultiblockContext
import com.cout970.magneticraft.systems.multiblocks.MultiblockManager
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.ModuleMultiblockGap
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.text.TextFormatting

/**
 * Created by cout970 on 2017/07/03.
 */
@RegisterBlocks
object Blocks : IBlockMaker {

    val PROPERTY_MULTIBLOCK_ORIENTATION: PropertyEnum<MultiblockOrientation> = PropertyEnum.create(
        "multiblock_orientation", MultiblockOrientation::class.java)

    lateinit var gap: BlockBase private set
    lateinit var solarPanel: BlockBase private set
    lateinit var shelvingUnit: BlockBase private set
    lateinit var steamEngine: BlockBase private set
    lateinit var grinder: BlockBase private set
    lateinit var sieve: BlockBase private set
    lateinit var solarTower: BlockBase private set
    lateinit var solarMirror: BlockBase private set
    lateinit var container: BlockBase private set
    lateinit var pumpjack: BlockBase private set
    lateinit var hydraulicPress: BlockBase private set
    lateinit var oilHeater: BlockBase private set
    lateinit var refinery: BlockBase private set
    lateinit var bigCombustionChamber: BlockBase private set
    lateinit var bigSteamBoiler: BlockBase private set
    lateinit var steamTurbine: BlockBase private set
    lateinit var bigElectricFurnace: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock?>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
            hasCustomModel = true
            tileConstructor = { a, b, c, d ->
                BlockBase.states_ = b
                BlockMultiblock(c, d, a)
            }
            states = MultiblockOrientation.values().toList()
            alwaysDropDefault = true
        }

        gap = builder.withName("multiblock_gap").copy {
            states = null
            factory = factoryOf(::TileMultiblockGap)
            onDrop = { emptyList() }
            onActivated = func@{
                val tile = it.worldIn.getTile<TileMultiblockGap>(it.pos) ?: return@func false
                val module = tile.multiblockModule
                val storedPos = module.centerPos ?: return@func false
                val mainBlockPos = it.pos.subtract(storedPos)
                val actionable = it.worldIn.getModule<IOnActivated>(mainBlockPos) ?: return@func false
                return@func actionable.onActivated(it)
            }
            onBlockBreak = func@{
                val mod = it.worldIn.getModule<ModuleMultiblockGap>(it.pos) ?: return@func
                if (mod.multiblock == null) {
                    val relPos = mod.centerPos ?: return@func
                    val absPos = it.pos + relPos
                    it.worldIn.setBlockToAir(absPos)
                }
            }
        }.build()

        solarPanel = builder.withName("solar_panel").copy {
            factory = factoryOf(::TileSolarPanel)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/mcx/solar_panel.mcx")
            )
            onActivated = defaultOnActivated { MultiblockSolarPanel }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        shelvingUnit = builder.withName("shelving_unit").copy {
            factory = factoryOf(::TileShelvingUnit)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/mcx/shelving_unit.mcx")
            )
            onActivated = defaultOnActivated { MultiblockShelvingUnit }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        steamEngine = builder.withName("steam_engine").copy {
            factory = factoryOf(::TileSteamEngine)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/gltf/steam_engine.gltf")
            )
            onActivated = defaultOnActivated { MultiblockSteamEngine }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        grinder = builder.withName("grinder").copy {
            factory = factoryOf(::TileGrinder)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/gltf/grinder.gltf")
            )
            onActivated = defaultOnActivated { MultiblockGrinder }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        sieve = builder.withName("sieve").copy {
            factory = factoryOf(::TileSieve)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/gltf/sieve.gltf")
            )
            onActivated = defaultOnActivated { MultiblockSieve }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        solarTower = builder.withName("solar_tower").copy {
            factory = factoryOf(::TileSolarTower)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/mcx/solar_tower.mcx")
            )
            onActivated = defaultOnActivated { MultiblockSolarTower }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        solarMirror = builder.withName("solar_mirror").copy {
            factory = factoryOf(::TileSolarMirror)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/mcx/solar_mirror.mcx")
            )
            onActivated = defaultOnActivated { MultiblockSolarMirror }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        container = builder.withName("container").copy {
            factory = factoryOf(::TileContainer)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/mcx/container.mcx")
            )
            onActivated = defaultOnActivated { MultiblockContainer }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        pumpjack = builder.withName("pumpjack").copy {
            factory = factoryOf(::TilePumpjack)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/mcx/pumpjack.mcx")
            )
            onActivated = defaultOnActivated { MultiblockPumpjack }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        hydraulicPress = builder.withName("hydraulic_press").copy {
            factory = factoryOf(::TileHydraulicPress)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/gltf/hydraulic_press.gltf")
            )
            onActivated = defaultOnActivated { MultiblockHydraulicPress }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        oilHeater = builder.withName("oil_heater").copy {
            factory = factoryOf(::TileOilHeater)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/mcx/oil_heater.mcx")
            )
            onActivated = defaultOnActivated { MultiblockOilHeater }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        refinery = builder.withName("refinery").copy {
            factory = factoryOf(::TileRefinery)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/mcx/refinery.mcx")
            )
            onActivated = defaultOnActivated { MultiblockRefinery }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        bigCombustionChamber = builder.withName("big_combustion_chamber").copy {
            factory = factoryOf(::TileBigCombustionChamber)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/gltf/big_combustion_chamber.gltf")
            )
            onActivated = defaultOnActivated { MultiblockBigCombustionChamber }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        bigSteamBoiler = builder.withName("big_steam_boiler").copy {
            factory = factoryOf(::TileBigSteamBoiler)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/gltf/big_steam_boiler.gltf")
            )
            onActivated = defaultOnActivated { MultiblockBigSteamBoiler }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        steamTurbine = builder.withName("steam_turbine").copy {
            factory = factoryOf(::TileSteamTurbine)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/gltf/steam_turbine.gltf")
            )
            onActivated = defaultOnActivated { MultiblockSteamTurbine }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        bigElectricFurnace = builder.withName("big_electric_furnace").copy {
            factory = factoryOf(::TileBigElectricFurnace)
            generateDefaultItemBlockModel = false
            customModels = listOf(
                "model" to resource("models/block/gltf/big_electric_furnace.gltf")
            )
            onActivated = defaultOnActivated { MultiblockBigElectricFurnace }
            onBlockPlaced = Blocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        return itemBlockListOf(
            solarPanel, shelvingUnit, steamEngine, grinder, sieve, solarTower, solarMirror,
            container, pumpjack, hydraulicPress, oilHeater, refinery, bigCombustionChamber,
            bigSteamBoiler, steamTurbine, bigElectricFurnace
        ) + blockListOf(gap)
    }

    fun placeWithOrientation(it: OnBlockPlacedArgs): IBlockState {
        val placer = it.placer ?: return it.defaultValue
        return it.defaultValue.withProperty(PROPERTY_MULTIBLOCK_ORIENTATION,
            MultiblockOrientation.of(placer.horizontalFacing, false))
    }

    fun defaultOnActivated(multiblock: () -> Multiblock): (OnActivatedArgs) -> Boolean {
        return func@{ args ->
            val state = args.state[PROPERTY_MULTIBLOCK_ORIENTATION] ?: return@func false
            if (state.active) {
                val tile = args.worldIn.getTile<TileBase>(args.pos) ?: return@func false
                val actionables = tile.container.modules.filterIsInstance<IOnActivated>()
                return@func actionables.any { it.onActivated(args) }
            } else {
                if (args.hand != EnumHand.MAIN_HAND) return@func false
                if (args.worldIn.isServer) {
                    val context = MultiblockContext(
                        multiblock = multiblock(),
                        world = args.worldIn,
                        center = args.pos,
                        facing = state.facing,
                        player = args.playerIn
                    )
                    activateMultiblock(context)
                }
                return@func true
            }
        }
    }

    fun activateMultiblock(context: MultiblockContext) {
        val errors = MultiblockManager.checkMultiblockStructure(context)
        val playerIn = context.player!!
        if (errors.isEmpty()) {
            MultiblockManager.activateMultiblockStructure(context)
            playerIn.sendMessage("text.magneticraft.multiblock.activate", color = TextFormatting.GREEN)
        }
    }

    enum class MultiblockOrientation(
        override val stateName: String,
        override val isVisible: Boolean,
        val active: Boolean,
        val facing: EnumFacing) : IStatesEnum, IStringSerializable {

        INACTIVE_NORTH("inactive_north", true, false, EnumFacing.NORTH),
        INACTIVE_SOUTH("inactive_south", false, false, EnumFacing.SOUTH),
        INACTIVE_WEST("inactive_west", false, false, EnumFacing.WEST),
        INACTIVE_EAST("inactive_east", false, false, EnumFacing.EAST),
        ACTIVE_NORTH("active_north", false, true, EnumFacing.NORTH),
        ACTIVE_SOUTH("active_south", false, true, EnumFacing.SOUTH),
        ACTIVE_WEST("active_west", false, true, EnumFacing.WEST),
        ACTIVE_EAST("active_east", false, true, EnumFacing.EAST);

        override fun getName() = name.toLowerCase()
        override val properties: List<IProperty<*>> get() = listOf(PROPERTY_MULTIBLOCK_ORIENTATION)

        override fun getBlockState(block: Block): IBlockState =
            block.defaultState.withProperty(PROPERTY_MULTIBLOCK_ORIENTATION, this)

        companion object {
            fun of(facing: EnumFacing, active: Boolean) = when {
                facing == EnumFacing.NORTH && !active -> INACTIVE_NORTH
                facing == EnumFacing.SOUTH && !active -> INACTIVE_SOUTH
                facing == EnumFacing.WEST && !active -> INACTIVE_WEST
                facing == EnumFacing.EAST && !active -> INACTIVE_EAST
                facing == EnumFacing.NORTH && active -> ACTIVE_NORTH
                facing == EnumFacing.SOUTH && active -> ACTIVE_SOUTH
                facing == EnumFacing.WEST && active -> ACTIVE_WEST
                facing == EnumFacing.EAST && active -> ACTIVE_EAST
                else -> INACTIVE_NORTH
            }
        }
    }
}