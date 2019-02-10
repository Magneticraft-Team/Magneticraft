package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.systems.blocks.IOnActivated
import com.cout970.magneticraft.systems.blocks.OnActivatedArgs
import com.cout970.magneticraft.systems.multiblocks.IMultiblockModule
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 2017/08/12.
 */

class ModuleSteamEngineMb(
    val facingGetter: () -> EnumFacing,
    val steamProduction: ValueAverage,
    val guiModule: ModuleOpenGui,
    override val name: String = "module_steam_engine_mb"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer
    val facing get() = facingGetter()
    var lidOpen = false
    var auxTime = 0

    companion object {
        val lidBoxes = listOf(
            Vec3d(-15.000, 7.000, -24.000) * PIXEL createAABBUsing Vec3d(-13.000, 13.000, -12.000) * PIXEL,
            Vec3d(-15.000, 13.000, -24.000) * PIXEL createAABBUsing Vec3d(-5.000, 15.000, -12.000) * PIXEL,
            Vec3d(-15.500, 5.000, -19.000) * PIXEL createAABBUsing Vec3d(-14.500, 8.000, -17.000) * PIXEL
        ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(1, 0, 1) }

        val shaftBox = listOf(
            Vec3d(-9.000, 9.000, -24.000) * PIXEL createAABBUsing Vec3d(-8.000, 10.000, -12.000) * PIXEL
        ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(1, 0, 1) }

        val gearboxShell = listOf(
            Vec3d(-5.000, 4.000, -30.000) * PIXEL createAABBUsing Vec3d(-1.000, 15.000, -8.000) * PIXEL,
            Vec3d(-15.000, 4.000, -12.000) * PIXEL createAABBUsing Vec3d(-5.000, 15.000, -8.000) * PIXEL,
            Vec3d(-15.000, 4.000, -30.000) * PIXEL createAABBUsing Vec3d(-5.000, 15.000, -24.000) * PIXEL,
            Vec3d(-15.000, 4.000, -24.000) * PIXEL createAABBUsing Vec3d(-13.000, 7.000, -12.000) * PIXEL
        ).map { EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it) + vec3Of(1, 0, 1) }
    }

    override fun update() {
        if (auxTime > 0) {
            auxTime--
        }
        if (world.isServer && container.shouldTick(40)) {
            container.sendUpdateToNearPlayers()
        }
        if (world.isClient && steamProduction.storage > 0) {
            if (world.rand.nextFloat() > 0.85) {
                val toFront = facing.opposite.directionVec.toVec3d()
                val particlePos = pos.toVec3d() + vec3Of(0.5, 1, 0.5) + toFront * 0.25

                val randVec = vec3Of(world.rand.nextFloat(), world.rand.nextFloat(), world.rand.nextFloat()) * 2 - 1
                val randDir = randVec * vec3Of(0.00625) + vec3Of(0, 0.0625, 0)

                world.spawnParticle(EnumParticleTypes.CLOUD,
                    particlePos.x, particlePos.y, particlePos.z,
                    randDir.x, randDir.y, randDir.z)
            }
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
                if (index < lidBoxes.size) {
                    lidOpen = true
                    auxTime = 20
                    container.sendUpdateToNearPlayers()
                }
            }
            return true
        }
        return guiModule.onActivated(args)
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

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("lidOpen", lidOpen)
        add("auxTime", auxTime)
        add("energyProduction", steamProduction.average)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        lidOpen = nbt.getBoolean("lidOpen")
        auxTime = nbt.getInteger("auxTime")
        steamProduction.storage = nbt.getFloat("energyProduction")
    }
}