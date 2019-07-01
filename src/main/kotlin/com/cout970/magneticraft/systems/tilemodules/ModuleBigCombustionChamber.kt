package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.misc.ConversionTable
import com.cout970.magneticraft.misc.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.tileentity.WorkingIndicator
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.DATA_ID_BURNING_TIME
import com.cout970.magneticraft.systems.gui.DATA_ID_MAX_BURNING_TIME
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import com.cout970.magneticraft.systems.tilerenderers.px
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.world.World
import kotlin.math.roundToInt

class ModuleBigCombustionChamber(
    val facing: () -> EnumFacing,
    val heatNode: IHeatNode,
    val inventory: Inventory,
    val tank: Tank,
    val maxHeat: Double,
    override val name: String = "module_big_combustion_chamber"
) : IModule {

    override lateinit var container: IModuleContainer
    var working = WorkingIndicator(this)
    var burningTime = 0
    var burningPower = ConversionTable.FUEL_TO_J
    var maxBurningTime = 0

    override fun update() {
        if (world.isClient) {
            spawnParticles()
            return
        }
        if (maxBurningTime > 0) {
            if (burningTime > maxBurningTime) {
                burningTime -= maxBurningTime
                maxBurningTime = 0
            } else {
                if (heatNode.temperature < maxHeat) {
                    val speed = Config.bigCombustionChamberMaxSpeed.toInt()
                    burningTime += speed
                    heatNode.applyHeat(speed * burningPower)
                    working.onWork()
                }
            }
        }
        while (maxBurningTime <= 0) {
            if (!consumeFuel()) break
        }

        if (maxBurningTime <= 0 && heatNode.temperature > STANDARD_AMBIENT_TEMPERATURE) {
            heatNode.applyHeat(-10.0)
        }

        working.tick()
    }

    fun spawnParticles() {
        if (working() && Config.enableMachineParticles == 1) {
            val flameLocalArea = AABB((-8).px, 2.px, 12.px, 24.px, 14.px, 16.px)
            val flameArea = facing().rotateBox(pos.toVec3d() + vec3Of(0.5), flameLocalArea.offset(pos))
            world.spawnParticles(EnumParticleTypes.FLAME, flameArea, 2, vec3Of(0, 0.005, 0), 0.01f)

            val smokeLocalArea = AABB(4.px, 30.px, (-36).px, 12.px, 32.px, (-44).px)
            val smokeArea = facing().rotateBox(pos.toVec3d() + vec3Of(0.5), smokeLocalArea.offset(pos))
            world.spawnParticles(EnumParticleTypes.SMOKE_LARGE, smokeArea, 2, vec3Of(0, 0.01, 0), 0.0025f)
        }
    }

    fun World.spawnParticles(particle: EnumParticleTypes, area: AABB, amount: Int, direction: IVector3, variability: Float) {
        val size = area.size()

        repeat(amount) {
            val pos = vec3Of(
                area.minX + rand.nextFloat() * size.xf,
                area.minY + rand.nextFloat() * size.yf,
                area.minZ + rand.nextFloat() * size.zf
            )
            val dir = vec3Of(
                direction.xf + (rand.nextFloat() * 2 - 1) * variability,
                direction.yf + (rand.nextFloat() * 2 - 1) * variability,
                direction.zf + (rand.nextFloat() * 2 - 1) * variability
            )

            spawnParticle(particle, pos.x, pos.y, pos.z, dir.x, dir.y, dir.z)
        }
    }

    fun AABB.size() = vec3Of(maxX - minX, maxY - minY, maxZ - minZ)

    fun consumeFuel(): Boolean = burnSolidFuel() || burnLiquidFuel()

    fun burnSolidFuel(): Boolean {
        val stack = inventory[0]
        if (stack.isEmpty) return false
        val time = TileEntityFurnace.getItemBurnTime(stack)
        if (time > 0) {
            stack.shrink(1)
            burningPower = ConversionTable.FUEL_TO_J
            maxBurningTime = time
            return true
        }
        return false
    }

    fun burnLiquidFuel(): Boolean {
        val fluid = tank.fluid ?: return false

        val fluidAmount = tank.fluidAmount.coerceAtMost(100)
        if (fluidAmount <= 0) return false

        val fuel = MagneticraftApi.getFluidFuelManager().findFuel(fluid) ?: return false
        val fraction = (fluidAmount * fuel.totalBurningTime / 1000f).roundToInt()
        if (fraction <= 0) return false
        tank.drain(fluidAmount, true)
        burningPower = fuel.powerPerCycle
        maxBurningTime = fraction
        return true
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("burningTime", burningTime)
        add("maxBurningTime", maxBurningTime)
        add("working", working.serializeNBT())
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        burningTime = nbt.getInteger("burningTime")
        maxBurningTime = nbt.getInteger("maxBurningTime")
        working.deserializeNBT(nbt.getCompoundTag("working"))
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(
            IntSyncVariable(DATA_ID_BURNING_TIME, { burningTime }, { burningTime = it }),
            IntSyncVariable(DATA_ID_MAX_BURNING_TIME, { maxBurningTime }, { maxBurningTime = it })
        )
    }
}