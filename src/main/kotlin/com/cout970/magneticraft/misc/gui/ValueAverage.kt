package com.cout970.magneticraft.misc.gui

import com.cout970.magneticraft.misc.network.FloatSyncVariable

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

    operator fun plusAssign(value: Double) {
        accumulated += value.toFloat()
    }

    operator fun plusAssign(value: Float) {
        accumulated += value
    }

    operator fun plusAssign(value: Int) {
        accumulated += value.toFloat()
    }

    operator fun minusAssign(value: Number) {
        accumulated -= value.toFloat()
    }

    fun toSyncVariable(id: Int) = FloatSyncVariable(id, getter = { average }, setter = { storage = it })
}