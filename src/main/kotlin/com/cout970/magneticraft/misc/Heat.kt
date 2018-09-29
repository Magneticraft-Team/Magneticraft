@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.cout970.magneticraft.misc

import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 07/07/2016.
 */

//values stored in kelvin
val STANDARD_TEMPERATURE = 0.fromCelsiusToKelvin()
val STANDARD_AMBIENT_TEMPERATURE = 25.fromCelsiusToKelvin()
val DEFAULT_COOKING_TEMPERATURE = 180.fromCelsiusToKelvin()
val DEFAULT_SMELTING_TEMPERATURE = 400.fromCelsiusToKelvin()

val MIN_EMISSION_TEMP = 480.fromCelsiusToKelvin()
//val MAX_EMISSION_TEMP = 1400.fromCelsiusToKelvin()
val MAX_EMISSION_TEMP = 800.fromCelsiusToKelvin()

val SNOW_CORRECTION_TEMP = 0.25f //Value to account for the fact that water freezes in some above-freezing biomes if it is snowy

val QUARTZ_MELTING_POINT = 1750.fromCelsiusToKelvin()
val GLASS_MAKING_TEMP = 450.fromCelsiusToKelvin()
val FURNACE_BRICK_TEMP = 550.fromCelsiusToKelvin()

val COKE_REACTION_TEMP = 450.fromCelsiusToKelvin()
val CARBON_SUBLIMATION_POINT = 4000.fromCelsiusToKelvin()
val LIMESTONE_MELTING_POINT = 1400.fromCelsiusToKelvin()

val WATER_FREEZING_POINT = 0.fromCelsiusToKelvin()
val WATER_MELTING_POINT = 0.fromCelsiusToKelvin()
val WATER_BOILING_POINT = 100.fromCelsiusToKelvin()

val COPPER_MELTING_POINT = 1085.fromCelsiusToKelvin()
val IRON_MELTING_POINT = 1538.fromCelsiusToKelvin()

val KILN_DAMAGE_TEMP = 125.fromCelsiusToKelvin()
val KILN_FIRE_TEMP = 250.fromCelsiusToKelvin()

val FIRE_TEMP = 250.fromCelsiusToKelvin()
val MAGMA_TEMP = 800.fromCelsiusToKelvin()


inline fun Number.fromCelsiusToKelvin(): Double = this.toDouble() + 273.15

inline fun Number.fromMinecraftToKelvin(): Double = fromMinecraftToCelsius().fromCelsiusToKelvin()

inline fun Number.fromMinecraftToCelsius(): Double {
    val x = toDouble()

    return if (x < 2.0) {
        -x * x * 6 + x * 30
    } else {
        -x * x * 6 + x * 30 + 10 * (x - 2.0) * (x - 2.0)
    }
}

inline fun Number.fromMinecraftToFarenheit(): Double = this.fromMinecraftToKelvin().toFahrenheit()

inline fun Number.fromFahrenheitToKelvin(): Double = this.fromFahrenheitToCelsius().fromCelsiusToKelvin()

inline fun Number.fromFahrenheitToCelsius(): Double = (this.toDouble() - 32) * 5 / 9

inline fun Number.toCelsius(): Double = this.toDouble() - 273.15

inline fun Number.toFahrenheit(): Double = this.toCelsius() * 9 / 5 + 32

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
    return ((sum / count).toCelsius() / water).fromCelsiusToKelvin()
}

fun testBiomeHeat(worldIn: World, pos: BlockPos): Double {
    val biome = worldIn.getBiome(pos)
    val correction = if (worldIn.getBiome(pos).isSnowyBiome) SNOW_CORRECTION_TEMP else 0.0f
    return (biome.getTemperature(pos) - correction).fromMinecraftToKelvin()
}