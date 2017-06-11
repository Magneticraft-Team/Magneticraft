package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 23/07/2016.
 */
@TileRegister("infinite_water")
class TileInfiniteWater : TileBase(), ITickable {

    override fun update() {
        for(i in EnumFacing.values()){
            val tile = worldObj.getTileEntity(pos.offset(i))
            if(tile != null){
                val handler = FLUID_HANDLER!!.fromTile(tile, i.opposite)
                if(handler != null){
                    val water = FluidStack(FluidRegistry.WATER, 10)
                    handler.fill(water, true)
                }
            }
        }
    }
}