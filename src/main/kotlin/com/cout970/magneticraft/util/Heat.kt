@file:Suppress("unused")

package com.cout970.magneticraft.util

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.render.CacheNode
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 07/07/2016.
 */

//values stored in kelvin
val STANDARD_TEMPERATURE = 0.toKelvinFromCelsius()
val STANDARD_AMBIENT_TEMPERATURE = 25.toKelvinFromCelsius()
val DEFAULT_COOKING_TEMPERATURE = 180.toKelvinFromCelsius()
val DEFAULT_SMELTING_TEMPERATURE = 400.toKelvinFromCelsius()
val DEFAULT_CONDUCTIVITY = 0.05


val MIN_EMISSION_TEMP = 480.toKelvinFromCelsius()
//val MAX_EMISSION_TEMP = 1400.toKelvinFromCelsius()
val MAX_EMISSION_TEMP = 800.toKelvinFromCelsius()

val SNOW_CORRECTION_TEMP = 0.25f //Value to account for the fact that water freezes in some above-freezing biomes if it is snowy

val QUARTZ_MELTING_POINT = 1750.toKelvinFromCelsius()
val GLASS_MAKING_TEMP = 450.toKelvinFromCelsius()
val FURNACE_BRICK_TEMP = 550.toKelvinFromCelsius()

val COKE_REACTION_TEMP = 450.toKelvinFromCelsius()
val CARBON_SUBLIMATION_POINT = 4000.toKelvinFromCelsius()

val LIMESTONE_HEAT_CAPACITY = 2.3
val LIMESTONE_MELTING_POINT = 1400.toKelvinFromCelsius()

val WATER_MELTING_POINT = 0.toKelvinFromCelsius()
val WATER_BOILING_POINT = 100.toKelvinFromCelsius()
val WATER_HEAT_CAPACITY = 4.18
val WATER_HEAT_OF_FUSION = 334

val COPPER_MELTING_POINT = 1085.toKelvinFromCelsius()
val COPPER_HEAT_CAPACITY = 3.45
val IRON_MELTING_POINT = 1538.toKelvinFromCelsius()
val IRON_HEAT_CAPACITY = 3.5

val KILN_DAMAGE_TEMP = 125.toKelvinFromCelsius()
val KILN_FIRE_TEMP = 250.toKelvinFromCelsius()

val ENERGY_TO_HEAT = 2f

fun Number.toKelvinFromCelsius(): Double = this.toDouble() + 273.15

fun Number.toKelvinFromMinecraftUnits(): Double = toCelsiusFromMinecraftUnits().toKelvinFromCelsius()

fun Number.toCelsiusFromMinecraftUnits(): Double {
    val x = toDouble()
    if (x < 2.0) {
        return -x * x * 6 + x * 30
    } else {
        return -x * x * 6 + x * 30 + 10 * (x - 2.0) * (x - 2.0)
    }
}

fun Number.toFarenheitFromMinecraftUnits(): Double = this.toKelvinFromMinecraftUnits().toFahrenheit()

fun Number.toKelvinFromFahrenheit(): Double = this.toCelsiusFromFahrenheit().toKelvinFromCelsius()

fun Number.toCelsiusFromFahrenheit(): Double = (this.toDouble() - 32) * 5 / 9

fun Number.toCelsius(): Double = this.toDouble() - 273.15

fun Number.toFahrenheit(): Double = this.toCelsius() * 9 / 5 + 32

fun guessAmbientTemp(worldIn: World, pos: BlockPos, range: Int = 10): Double {
    var sum = 0.0
    var count = 0
    for (i in -range..range) {
        if (i == 0) {
            sum += testBiomeHeat(worldIn, pos)
            count++
        } else {
            sum += testBiomeHeat(worldIn, pos.offset(EnumFacing.NORTH, i))
            sum += testBiomeHeat(worldIn, pos.offset(EnumFacing.EAST, i))
            count += 2
        }
    }
    var water = 1
    for (side in EnumFacing.values()) {
        if (worldIn.getBlockState(pos.offset(side)).block == Blocks.WATER) {
            water++
        }
    }
    return ((sum / count).toCelsius() / water).toKelvinFromCelsius()
}

fun testBiomeHeat(worldIn: World, pos: BlockPos): Double {
    val biome = worldIn.getBiome(pos)
    val correction = if (worldIn.getBiome(pos).isSnowyBiome) SNOW_CORRECTION_TEMP else 0.0f
    return (biome.getFloatTemperature(pos) - correction).toKelvinFromMinecraftUnits()
}

private fun lookupTemp(stack: ItemStack): Double = Config.fuelTemps.map[stack.item] ?: Config.defaultMaxTemp

class FuelCache : CacheNode<ItemStack, Double>(ItemStack(Blocks.AIR), ::lookupTemp, ItemStack::isItemEqual)