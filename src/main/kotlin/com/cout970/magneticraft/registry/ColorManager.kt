package com.cout970.magneticraft.registry

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.block.heat.BlockHeatPipe
import com.cout970.magneticraft.block.heat.BlockHeatReservoir
import com.cout970.magneticraft.block.heat.BlockHeatSink
import com.cout970.magneticraft.tileentity.heat.TileHeatBase
import com.cout970.magneticraft.util.MAX_EMISSION_TEMP
import com.cout970.magneticraft.util.MIN_EMISSION_TEMP
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.color.BlockColors
import net.minecraft.client.renderer.color.IBlockColor
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

/**
 * Created by Yurgen on 26/10/2016.
 */
fun registerColorHandlers() {
    val minecraft: Minecraft = Minecraft.getMinecraft()
    val blockColors: BlockColors = minecraft.blockColors

    registerBlockColorHandlers(blockColors)
}

private fun registerBlockColorHandlers(blockColors: BlockColors) {
    blockColors.registerBlockColorHandler(HeatColorHandler(), BlockHeatReservoir, BlockHeatPipe, BlockHeatSink)
}

@SideOnly(Side.CLIENT)
class HeatColorHandler : IBlockColor {
    override fun colorMultiplier(state: IBlockState?, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int): Int {
        if (tintIndex < 0) return 0
        if (pos == null) return 0
        val temp = worldIn?.getTile<TileHeatBase>(pos)?.heatNodes?.first()?.temperature ?: return 0
        val color = Color((interpolate(temp, MIN_EMISSION_TEMP, MAX_EMISSION_TEMP) * 255).toInt(), 0, 0)
        return color.rgb
    }

    fun interpolate(v: Double, min: Double, max: Double): Double {
        if (v < min) return 0.0
        if (v > max) return 1.0
        return (v - min) / (max - min)
    }
}