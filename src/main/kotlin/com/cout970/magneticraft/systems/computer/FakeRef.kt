package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.core.ITileRef
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object FakeRef : ITileRef {
    override fun getWorld(): World = error("Not available in emulation mode")
    override fun getPos(): BlockPos = error("Not available in emulation mode")
}
