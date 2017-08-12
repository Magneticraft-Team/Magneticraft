package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.block.core.IOnActivated
import com.cout970.magneticraft.block.core.OnActivatedArgs
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.multiblock.core.IMultiblockModule
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.*
import com.cout970.vector.extensions.times
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 2017/08/12.
 */

class ModuleSteamEngineMb(
        val facingGetter: () -> EnumFacing,
        val energyModule: ModuleElectricity,
        override val name: String = "module_steam_engine_mb"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer
    val facing get() = facingGetter()
    var lidOpen = false
    var auxTime = 0

    companion object {
        val lidBoxes = listOf(
                Vec3d(-15.000, 8.000, -24.000) * PIXEL toAABBWith Vec3d(-13.00, 14.00, -12.000) * PIXEL,
                Vec3d(-15.000, 14.00, -24.000) * PIXEL toAABBWith Vec3d(-5.000, 16.00, -12.000) * PIXEL,
                Vec3d(-15.500, 6.000, -19.000) * PIXEL toAABBWith Vec3d(-14.50, 9.000, -17.000) * PIXEL
        ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(1, 0, 1) }

        val shaftBox = listOf(
                Vec3d(-9.000, 10.000, -24.000) * PIXEL toAABBWith Vec3d(-8.000, 11.000, -12.000) * PIXEL
        ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(1, 0, 1) }

        val gearboxShell = listOf(
                Vec3d(-5.000, 5.000, -30.000) * PIXEL toAABBWith Vec3d(-1.000, 16.000, -8.000) * PIXEL,
                Vec3d(-15.000, 5.000, -12.000) * PIXEL toAABBWith Vec3d(-5.000, 16.000, -8.000) * PIXEL,
                Vec3d(-15.000, 5.000, -30.000) * PIXEL toAABBWith Vec3d(-5.000, 16.000, -24.000) * PIXEL,
                Vec3d(-15.000, 5.000, -24.000) * PIXEL toAABBWith Vec3d(-13.000, 8.000, -12.000) * PIXEL
        ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(1, 0, 1) }
    }

    override fun update() {
        if(auxTime > 0){
            auxTime--
        }
    }

    override fun onActivated(args: OnActivatedArgs): Boolean {
        if (world.isClient) return true

        val index = getBoxFromHit(args)
        if (index != -1) {
            if (lidOpen) {
                if (index < shaftBox.size) {
                    //place gear
                } else {
                    lidOpen = false
                    auxTime = 20
                    container.sendUpdateToNearPlayers()
                }
            } else {
                if(index < lidBoxes.size){
                    lidOpen = true
                    auxTime = 20
                    container.sendUpdateToNearPlayers()
                }
            }
            return true
        }
        return false
    }

    fun getBoxFromHit(args: OnActivatedArgs): Int {
        val mod = world.getModule<IMultiblockModule>(args.pos) ?: return -1
        val relPos = mod.centerPos?.unaryMinus() ?: return -1

        val boxes = getDynamicCollisionBoxes(args.pos).map {
            val origin = EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it)
            facing.rotateBox(vec3Of(0.5), origin)
        }.map { it.offset(relPos) }

        val hits = boxes.mapIndexedNotNull { index, box ->
            if (box.isHitBy(args.hit)) index else null
        }
        return hits.firstOrNull() ?: return -1
    }


    @Suppress("UNUSED_PARAMETER")
    fun getDynamicCollisionBoxes(otherPos: BlockPos): List<AABB> {
        return (if (lidOpen) shaftBox else lidBoxes) + gearboxShell
    }

    fun getCapability(cap: Capability<*>, side: EnumFacing?, relPos: BlockPos): Any? {
        if (cap == ELECTRIC_NODE_HANDLER && side == EnumFacing.UP) {
            val rel = facing.opposite.rotatePoint(BlockPos.ORIGIN, BlockPos(2, 0, 2))
            if (relPos == rel) {
                return energyModule
            }
        }
        return null
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("lidOpen", lidOpen)
        add("auxTime", auxTime)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        lidOpen = nbt.getBoolean("lidOpen")
        auxTime = nbt.getInteger("auxTime")
    }
}