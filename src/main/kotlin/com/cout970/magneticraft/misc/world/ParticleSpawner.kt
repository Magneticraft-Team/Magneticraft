package com.cout970.magneticraft.misc.world

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.misc.vector.xd
import com.cout970.magneticraft.misc.vector.yd
import com.cout970.magneticraft.misc.vector.zd
import com.cout970.vector.extensions.Vector3
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumParticleTypes
import net.minecraft.world.World

class ParticleSpawner(
    val particlesPerSecond: Double,
    val particle: EnumParticleTypes,
    val block: IBlockState,
    val speed: () -> IVector3 = { Vector3.ORIGIN },
    val area: () -> AABB
) {

    fun spawn(world: World) {
        val perTick = (particlesPerSecond / 20)
        val integer = perTick.toInt()
        val fraction = perTick - integer

        repeat(integer) {
            forceSpawn(world)
        }
        if (fraction > 0 && world.rand.nextFloat() <= fraction) {
            forceSpawn(world)
        }
    }

    fun forceSpawn(world: World) {
        if (world.isServer) return

        val aabb = area()
        val point = vec3Of(
            aabb.minX.interp(aabb.maxX, world.rand.nextDouble()),
            aabb.minY.interp(aabb.maxY, world.rand.nextDouble()),
            aabb.minZ.interp(aabb.maxZ, world.rand.nextDouble())
        )
        val dir = speed()
        world.spawnParticle(particle, false,
            point.xd, point.yd, point.zd,
            dir.xd, dir.yd, dir.zd,
            Block.getStateId(block)
        )
    }

    fun Double.interp(other: Double, point: Double) = this + (other - this) * point
}