package com.cout970.magneticraft.gui.client.components.bars

import com.cout970.magneticraft.util.clamp

open class CallbackBarProvider(val callback: () -> Double, val max: () -> Double,
                               val min: () -> Double) : IBarProvider {

    override fun getLevel(): Float = clamp(
            (callback.invoke() - min.invoke()) / ensureNonZero(max.invoke() - min.invoke()), 1.0,
            0.0).toFloat()

    private fun ensureNonZero(x: Double): Double = if (x == 0.0) 1.0 else x
}

class StaticBarProvider(val minVal: Double, val maxVal: Double, callback: () -> Double)
    : CallbackBarProvider(callback = callback, max = { maxVal }, min = { minVal })