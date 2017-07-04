package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.*
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.player.sendMessage
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.multiblock.MultiblockSolarPanel
import com.cout970.magneticraft.multiblock.core.Multiblock
import com.cout970.magneticraft.multiblock.core.MultiblockContext
import com.cout970.magneticraft.multiblock.core.MultiblockManager
import com.cout970.magneticraft.tileentity.TileMultiblockGap
import com.cout970.magneticraft.tileentity.TileSolarPanel
import com.cout970.magneticraft.util.resource
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
object Multiblocks : IBlockMaker {

    val PROPERTY_MULTIBLOCK_ORIENTATION = PropertyEnum.create("multiblock_orientation",
            MultiblockOrientation::class.java)!!

    lateinit var gap: BlockBase private set
    lateinit var solarPanel: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
            hasCustomModel = true
            tileConstructor = { a, b, c, d ->
                BlockBase.states_ = b
                BlockMultiblock(c, d, a)
            }
            states = MultiblockOrientation.values().toList()
        }

        gap = builder.withName("multiblock_gap").copy {
            states = null
            factory = factoryOf(::TileMultiblockGap)
        }.build()

        solarPanel = builder.withName("solar_panel").copy {
            factory = factoryOf(::TileSolarPanel)
            generateDefaultItemModel = false
            customModels = listOf(
                    "model" to resource("models/block/mcx/solar_panel.mcx")
            )
            onActivated = defaultOnActivated({ MultiblockSolarPanel })
            onBlockPlaced = Multiblocks::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        return itemBlockListOf(gap, solarPanel)
    }

    fun placeWithOrientation(it: OnBlockPlacedArgs): IBlockState {
        val placer = it.placer ?: return it.defaultValue
        return it.defaultValue.withProperty(PROPERTY_MULTIBLOCK_ORIENTATION,
                MultiblockOrientation.of(placer.horizontalFacing, false))
    }

    fun defaultOnActivated(multiblock: () -> Multiblock): (OnActivatedArgs) -> Boolean {
        return func@ {
            if (it.hand != EnumHand.MAIN_HAND) return@func false
            val state = it.state[PROPERTY_MULTIBLOCK_ORIENTATION] ?: return@func false
            if (it.worldIn.isServer) {
                if (!state.active) {
                    val context = MultiblockContext(
                            multiblock = multiblock(),
                            world = it.worldIn,
                            center = it.pos,
                            facing = state.facing,
                            player = it.playerIn
                    )
                    activateMultiblock(context)
                }
            }
            !state.active
        }
    }

    fun activateMultiblock(context: MultiblockContext) {
        val errors = MultiblockManager.checkMultiblockStructure(context)
        val playerIn = context.player!!
        if (errors.isNotEmpty()) {
            playerIn.sendMessage("text.magneticraft.multiblock.error_count", errors.size, color = TextFormatting.AQUA)
            if (errors.size > 2) {
                playerIn.sendMessage("text.magneticraft.multiblock.first_errors", 2, color = TextFormatting.AQUA)
                var count = 0
                errors.forEach {
                    if (count >= 2) return@forEach
                    playerIn.sendMessage(it.apply { style.color = TextFormatting.DARK_RED })
                    count++
                }
            } else {
                playerIn.sendMessage("text.magneticraft.multiblock.all_errors", color = TextFormatting.DARK_AQUA)
                errors.forEach {
                    playerIn.sendMessage(it.apply { style.color = TextFormatting.DARK_RED })
                }
            }
        } else {
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

        override fun getBlockState(block: Block): IBlockState {
            return block.defaultState.withProperty(PROPERTY_MULTIBLOCK_ORIENTATION, this)
        }

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