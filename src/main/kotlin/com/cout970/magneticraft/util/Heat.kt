package com.cout970.magneticraft.util

/**
 * Created by cout970 on 07/07/2016.
 */

//values stored in kelvin
val STANDARD_TEMPERATURE = 0.toKelvinFromCelsius()
val STANDARD_AMBIENT_TEMPERATURE = 25.toKelvinFromCelsius()
val DEFAULT_COOKING_TEMPERATURE = 180.toKelvinFromCelsius()
val DEFAULT_SMELTING_TEMPERATURE = 400.toKelvinFromCelsius()

val COPPER_MELTING_POINT = 1085.toKelvinFromCelsius()
val COPPER_HEAT_CAPACITY = 3.45

fun Number.toKelvinFromCelsius(): Double = this.toDouble() + 273.15

fun Number.toKelvinFromMinecraftUnits(): Double = this.toDouble() / 4

fun Number.toKelvinFromFahrenheit(): Double = this.toCelsiusFromFahrenheit().toKelvinFromCelsius()

fun Number.toCelsiusFromFahrenheit(): Double = (this.toDouble() - 32) * 5 / 9

fun Number.toCelsius(): Double = this.toDouble() - 273.15

fun Number.toFahrenheit(): Double = this.toCelsius() * 9 / 5 + 32

