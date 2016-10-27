package com.cout970.magneticraft.block.heat

import com.cout970.magneticraft.tileentity.electric.TileHeatReservoir
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockHeatReservoir : BlockHeatBase(Material.ROCK, "heat_reservoir"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileHeatReservoir()

    @SideOnly(Side.CLIENT)
    fun colorMultiplier(p_176337_0_: Int): Int {
        val f = p_176337_0_.toFloat() / 15.0f
        var f1 = f * 0.6f + 0.4f

        if (p_176337_0_ == 0) {
            f1 = 0.3f
        }

        var f2 = f * f * 0.7f - 0.5f
        var f3 = f * f * 0.6f - 0.7f

        if (f2 < 0.0f) {
            f2 = 0.0f
        }

        if (f3 < 0.0f) {
            f3 = 0.0f
        }

        val i = MathHelper.clamp_int((f1 * 255.0f).toInt(), 0, 255)
        val j = MathHelper.clamp_int((f2 * 255.0f).toInt(), 0, 255)
        val k = MathHelper.clamp_int((f3 * 255.0f).toInt(), 0, 255)
        return -16777216 or i shl 16 or j shl 8 or k
    }

}