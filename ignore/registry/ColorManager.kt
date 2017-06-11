package com.cout970.magneticraft.registry

//i'm pretty sure this needs a file:SideOnly

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.block.heat.BlockHeatPipe
import com.cout970.magneticraft.block.heat.BlockHeatReservoir
import com.cout970.magneticraft.block.heat.BlockHeatSink
import com.cout970.magneticraft.misc.energy.getHeatHandler
import com.cout970.magneticraft.util.MAX_EMISSION_TEMP
import com.cout970.magneticraft.util.MIN_EMISSION_TEMP
import com.cout970.magneticraft.util.interpolate
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

    override fun colorMultiplier(state: IBlockState, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int): Int {
        if (tintIndex < 0) return 0
        if (worldIn == null || pos == null) return 0
        val handler = worldIn.getHeatHandler(pos) ?: return 0
        val nodes = handler.nodes.mapNotNull { if (it is IHeatNode) it else null }
        if (nodes.isEmpty()) return 0
        val temp = nodes.sumByDouble { it.temperature } / nodes.size
        val color = Color((interpolate(temp, MIN_EMISSION_TEMP, MAX_EMISSION_TEMP) * 255).toInt(), 0, 0)
        return color.rgb
    }
}