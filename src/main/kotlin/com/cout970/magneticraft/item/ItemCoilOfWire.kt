package com.cout970.magneticraft.item

import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.MANUAL_CONNECTION_HANDLER
import com.cout970.magneticraft.registry.fromBlock
import com.cout970.magneticraft.util.checkNBT
import com.cout970.magneticraft.util.getBlockPos
import com.cout970.magneticraft.util.hasKey
import com.cout970.magneticraft.util.setBlockPos
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World

/**
 * Created by cout970 on 17/07/2016.
 */
object ItemCoilOfWire : ItemMod("coil_of_wire") {

    val POSITION_KEY = "Position"

    override fun getItemStackDisplayName(stack: ItemStack?): String {
        val name = super.getItemStackDisplayName(stack)
        if (stack!!.hasKey(POSITION_KEY)) {
            val basePos = stack.getBlockPos(POSITION_KEY)
            return name + " [${TextFormatting.AQUA}Position: ${basePos.x}, ${basePos.y}, ${basePos.z}]"
        }
        return name
    }

    override fun onItemRightClick(itemStackIn: ItemStack?, worldIn: World?, playerIn: EntityPlayer?, hand: EnumHand?): ActionResult<ItemStack>? {
        if (playerIn!!.isSneaking && itemStackIn != null) {
            itemStackIn.checkNBT()
            itemStackIn.tagCompound?.removeTag(POSITION_KEY)
        }
        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand)
    }

    override fun onItemUse(stack: ItemStack?, playerIn: EntityPlayer?, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (stack == null) return EnumActionResult.PASS

        val block = worldIn.getBlockState(pos)
        val handler = MANUAL_CONNECTION_HANDLER!!.fromBlock(block.block)
        if (handler != null) {
            if (playerIn!!.isSneaking) {
                val basePos = handler.getBasePos(pos, worldIn, playerIn, facing, stack)
                if (basePos != null) {
                    stack.setBlockPos(POSITION_KEY, basePos)
                    return EnumActionResult.SUCCESS
                }
            } else {
                if (stack.hasKey(POSITION_KEY)) {
                    val basePos = stack.getBlockPos(POSITION_KEY)
                    if (handler.connectWire(basePos, pos, worldIn, playerIn, facing, stack)) {
                        if (worldIn.isServer) {
                            playerIn.addChatComponentMessage(TextComponentTranslation("text.magneticraft.wire_connect.success"))
                        }
                    } else {
                        if (worldIn.isServer) {
                            playerIn.addChatComponentMessage(TextComponentTranslation("text.magneticraft.wire_connect.fail"))
                        }
                    }
                    return EnumActionResult.SUCCESS
                } else {
                    if (worldIn.isServer) {
                        playerIn.addChatComponentMessage(TextComponentTranslation("text.magneticraft.wire_connect.no_other_connector"))
                    }
                }
            }
        }
        return EnumActionResult.PASS
    }
}