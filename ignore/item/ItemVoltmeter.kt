package com.cout970.magneticraft.item

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.misc.player.sendMessage
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import org.lwjgl.input.Keyboard

/**
 * Created by cout970 on 20/07/2016.
 */
object ItemVoltmeter : ItemMod("voltmeter") {

    override fun onItemUse(stack: ItemStack?, playerIn: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {

        //DEBUG
        if (Debug.DEBUG && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            val tile = worldIn.getTileEntity(pos)
            playerIn.sendMessage("Server: ${worldIn.isServer}, Tile: ${tile?.serializeNBT()}\n")
        }

        //NO DEBUG
        if (worldIn.isServer) {
            val tile = worldIn.getTileEntity(pos)
            if (tile != null) {
                val handler = ELECTRIC_NODE_HANDLER!!.fromTile(tile)
                if (handler != null) {
                    for (i in handler.nodes) {
                        if (i is IElectricNode) {
                            playerIn.addChatComponentMessage(TextComponentString("%.2fV %.2fA %.2fW".format(i.voltage, i.amperage, i.voltage * i.amperage)))
                        }
                    }
                }
            }
        }
        return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    }
}