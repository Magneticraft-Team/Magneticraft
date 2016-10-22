package com.cout970.magneticraft.item

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.api.heat.IHeatHandler
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.util.*
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
object ItemThermometer : ItemBase("thermometer") {

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
                val handler = NODE_HANDLER!!.fromTile(tile)
                if (handler is IHeatHandler) {
                    for (i in handler.nodes) {
                        if (i !is IHeatNode) continue
                        if (Config.heatUnitCelsius) playerIn.addChatComponentMessage(TextComponentString("%.2fC".format(i.temperature.toCelsius())))
                        else playerIn.addChatComponentMessage(TextComponentString("%.2fF".format(i.temperature.toFahrenheit())))
                        return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)
                    }
                }
            }
            if (Config.heatUnitCelsius) playerIn.addChatComponentMessage(TextComponentString("Ambient: %.2fC".format(worldIn.getBiome(pos).temperature.toCelsiusFromMinecraftUnits())))
            else playerIn.addChatComponentMessage(TextComponentString("Ambient: %.2fF".format(worldIn.getBiome(pos).temperature.toDouble().toFarenheitFromMinecraftUnits())))
        }
        return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    }
}