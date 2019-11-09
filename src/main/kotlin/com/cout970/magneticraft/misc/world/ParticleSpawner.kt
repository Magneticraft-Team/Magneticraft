package com.cout970.magneticraft.misc.world

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IBlockState
import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.misc.vector.xd
import com.cout970.magneticraft.misc.vector.yd
import com.cout970.magneticraft.misc.vector.zd
import net.minecraft.particles.IParticleData
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class ParticleSpawner(
    val particlesPerSecond: Double,
    val particle: IParticleData,
    val block: IBlockState,
    val speed: () -> IVector3 = { Vec3d.ZERO },
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
        world.addParticle(particle,
            point.xd, point.yd, point.zd,
            dir.xd, dir.yd, dir.zd
        )
    }

    fun Double.interp(other: Double, point: Double) = this + (other - this) * point
}