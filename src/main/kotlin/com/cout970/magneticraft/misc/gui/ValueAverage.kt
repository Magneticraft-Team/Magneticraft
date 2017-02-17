package com.cout970.magneticraft.misc.gui

/**
 * Created by cout970 on 10/07/2016.
 */
class ValueAverage(val maxCounter: Int = 20) {

    private var accumulated: Float = 0f
    private var counter: Int = 0
    var storage: Float = 0f
    var average: Float = 0f
        private set

    fun tick() {
        counter++
        if (counter >= maxCounter) {
            average = accumulated / counter
            accumulated = 0f
            counter = 0
        }
    }

    fun add(value: Number) {
        accumulated += value.toFloat()
    }

    operator fun plusAssign(value: Number) {
        accumulated += value.toFloat()
    }

    operator fun minusAssign(value: Number) {
        accumulated -= value.toFloat()
    }
}