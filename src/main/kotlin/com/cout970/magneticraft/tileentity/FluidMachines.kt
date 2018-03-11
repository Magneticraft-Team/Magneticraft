package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModuleBucketIO
import com.cout970.magneticraft.tileentity.modules.ModuleFluidHandler
import com.cout970.magneticraft.tileentity.modules.ModulePipe
import com.cout970.magneticraft.tileentity.modules.pipe.PipeType
import com.cout970.magneticraft.tilerenderer.core.MutableCubeCache
import net.minecraft.util.ITickable

/**
 * Created by cout970 on 2017/08/28.
 */

@RegisterTileEntity("copper_tank")
class TileCopperTank : TileBase(), ITickable {

    val tank = Tank(32_000)

    val fluidModule = ModuleFluidHandler(tank)
    var amountCache = 0

    val bucketIoModule = ModuleBucketIO(tank)
    val fluidRenderer = MutableCubeCache()

    init {
        initModules(fluidModule, bucketIoModule)
    }

    @DoNotRemove
    override fun update() {
        if (world.isServer && container.shouldTick(10)) {
            if (amountCache != tank.fluidAmount) {
                amountCache = tank.fluidAmount
                container.sendUpdateToNearPlayers()
            }
        }
        super.update()
    }

    override fun shouldRenderInPass(pass: Int): Boolean {
        return pass == 0 || pass == 1
    }
}

@RegisterTileEntity("iron_pipe")
class TileIronPipe : TileBase(), ITickable {

    val pipeModule = ModulePipe(PipeType.IRON)


    init {
        initModules(pipeModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}