package com.cout970.magneticraft.features.fluid_machines

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.TileType
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.*
import com.cout970.magneticraft.systems.tilemodules.pipe.PipeType
import com.cout970.magneticraft.systems.tilerenderers.MutableCubeCache
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 2017/08/28.
 */

@RegisterTileEntity("small_tank")
class TileSmallTank(type: TileType) : TileBase(type), ITickableTileEntity {

    val tank = Tank(32_000)

    val fluidModule = ModuleFluidHandler(tank)
    var amountCache = 0

    val bucketIoModule = ModuleBucketIO(tank)
    val fluidRenderer = MutableCubeCache()

    val toggleExportModule = ModuleToggleFluidExporter(tank, {
        listOf(BlockPos(0, -1, 0) to EnumFacing.UP)
    })

    init {
        initModules(fluidModule, bucketIoModule, toggleExportModule, ModuleOpenGui())
    }

        override fun tick() {
        if (theWorld.isServer && container.shouldTick(10)) {
            if (amountCache != tank.fluidAmount) {
                amountCache = tank.fluidAmount
                container.sendUpdateToNearPlayers()
            }
        }
        super.update()
    }

    // TODO
//    override fun shouldRenderInPass(pass: Int): Boolean {
//        return pass == 0 || pass == 1
//    }
}

@RegisterTileEntity("iron_pipe")
class TileIronPipe(type: TileType) : TileBase(type), ITickableTileEntity {

    val tank = Tank(PipeType.IRON.maxRate)
    val fluidModuleHeat = ModuleFluidHandler(tank, capabilityFilter = { _, _ -> null })
    val pipeModule = ModulePipe(tank, PipeType.IRON)

    init {
        initModules(pipeModule, fluidModuleHeat)
    }

        override fun tick() {
        super.update()
        if (Debug.DEBUG) {
            container.sendUpdateToNearPlayers()
        }
    }
}