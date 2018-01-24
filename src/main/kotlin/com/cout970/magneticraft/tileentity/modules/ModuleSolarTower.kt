package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.toBlockPos
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

class ModuleSolarTower(
        val facingGetter: () -> EnumFacing,
        override val name: String = "module_solar_tower"
) : IModule {

    override lateinit var container: IModuleContainer
    var mirrors: List<BlockPos> = emptyList()

    override fun update() {
        if (world.isServer && container.shouldTick(20)) {
            orientateNearMirrors()
        }
    }

    fun orientateNearMirrors() {
        val area = BlockPos(25, 20, 25)
        val center = pos + facingGetter().toBlockPos()
        val mirrors = mutableListOf<BlockPos>()

        for (x in -area.x..area.x) {
            for (y in 0..area.y) {
                for (z in -area.z..area.z) {
                    val otherPos = center + BlockPos(x, -y, z)
                    val mod = world.getModule<ModuleSolarMirror>(otherPos) ?: continue

                    mod.setTargetTower(center)
                    mirrors += otherPos
                }
            }
        }

        this.mirrors = mirrors
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        list("mirrors") {
            mirrors.forEach { newNbt { add("pos", it) } }
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        if (nbt.hasKey("mirrors")) {
            val newMirrors = mutableListOf<BlockPos>()
            val list = nbt.getList("mirrors")
            list.forEachTag {
                newMirrors += it.getBlockPos("pos")
            }
            mirrors = newMirrors
        }
    }
}