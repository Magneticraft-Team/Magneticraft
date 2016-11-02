package com.cout970.magneticraft.util

/**
 * Created by cout970 on 07/07/2016.
 */

//values stored in kelvin
val STANDARD_TEMPERATURE = 0.toKelvinFromCelsius()
val STANDARD_AMBIENT_TEMPERATURE = 25.toKelvinFromCelsius()
val DEFAULT_COOKING_TEMPERATURE = 180.toKelvinFromCelsius()
val DEFAULT_SMELTING_TEMPERATURE = 400.toKelvinFromCelsius()
val MIN_EMISSION_TEMP = 480.toKelvinFromCelsius()
//val MAX_EMISSION_TEMP = 1400.toKelvinFromCelsius()
val MAX_EMISSION_TEMP = 800.toKelvinFromCelsius()

val WATER_MELTING_POINT = 0.toKelvinFromCelsius()
val WATER_BOILING_POINT = 100.toKelvinFromCelsius()
val WATER_HEAT_CAPACITY = 4.18
val WATER_HEAT_OF_FUSION = 334

val COPPER_MELTING_POINT = 1085.toKelvinFromCelsius()
val COPPER_HEAT_CAPACITY = 3.45
val IRON_MELTING_POINT = 1538.toKelvinFromCelsius()
val IRON_HEAT_CAPACITY = 3.5

val ENERGY_TO_HEAT = 2f

fun Number.toKelvinFromCelsius(): Double = this.toDouble() + 273.15

fun Number.toKelvinFromMinecraftUnits(): Double = (this.toDouble() * 25) + 273.15

fun Number.toFarenheitFromMinecraftUnits(): Double = this.toKelvinFromMinecraftUnits().toFahrenheit()

fun Number.toCelsiusFromMinecraftUnits(): Double = this.toDouble() * 25

fun Number.toKelvinFromFahrenheit(): Double = this.toCelsiusFromFahrenheit().toKelvinFromCelsius()

fun Number.toCelsiusFromFahrenheit(): Double = (this.toDouble() - 32) * 5 / 9

fun Number.toCelsius(): Double = this.toDouble() - 273.15

fun Number.toFahrenheit(): Double = this.toCelsius() * 9 / 5 + 32

