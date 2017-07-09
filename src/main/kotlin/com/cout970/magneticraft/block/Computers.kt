package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.CommonMethods
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.tileentity.TileComputer
import com.cout970.magneticraft.util.resource
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock

/**
 * Created by cout970 on 2017/07/07.
 */
object Computers : IBlockMaker {

    lateinit var computer: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
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
//            onActivated = ::onComputerActivated
        }.build()

        return itemBlockListOf(computer)
    }

//    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
//        if (!playerIn.isSneaking) {
//
//            if (worldIn.isServer) {
//                var block = true
//                if (heldItem != null) {
//                    val cap = ITEM_FLOPPY_DISK!!.fromItem(heldItem)
//                    if (cap != null) {
//                        val tile = worldIn.getTile<TileComputer>(pos)
//                        if (tile != null && tile.inv[0] == null) {
//                            val index = playerIn.inventory.currentItem
//                            if (index in 0..8) {
//                                tile.inv[0] = playerIn.inventory.removeStackFromSlot(index)
//                            }
//                        } else {
//                            block = false
//                        }
//                    } else {
//                        block = false
//                    }
//                } else {
//                    block = false
//                }
//
//                if (!block) {
//                    playerIn.openGui(Magneticraft, -1, worldIn, pos.x, pos.y, pos.z)
//                }
//            }
//            return true
//        } else {
//            if (worldIn.isServer && hand == EnumHand.MAIN_HAND && heldItem == null) {
//                val tile = worldIn.getTile<TileComputer>(pos)
//                if (tile != null && tile.inv[0] != null) {
//                    playerIn.inventory.addItemStackToInventory(tile.inv.extractItem(0, 64, false))
//                }
//            }
//        }
//        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ)
//    }
}