package com.cout970.magneticraft.util

/**
 * Created by cout970 on 07/07/2016.
 */

//values stored in kelvin
val STANDARD_TEMPERATURE = 0.toKelvinFromCelsius()
val STANDARD_AMBIENT_TEMPERATURE = 25.toKelvinFromCelsius()

fun Number.toKelvinFromCelsius(): Double = this.toDouble() + 273.15

fun Number.toKelvinFromFahrenheit(): Double = this.toCelsiusFromFahrenheit().toKelvinFromCelsius()

fun Number.toCelsiusFromFahrenheit(): Double = (this.toDouble() - 32) * 5 / 9

fun Number.toCelsius(): Double = this.toDouble() - 273.15

fun Number.toFahrenheit(): Double = this.toCelsius() * 9 / 5 + 32

