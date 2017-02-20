package com.cout970.magneticraft.misc.render

/**
 * Created by cout970 on 16/07/2016.
 */
class AnimationTimer {

    var active = true
    var lastTime = System.currentTimeMillis()
    var animationStage = 0f

    fun updateAnimation() {
        animationStage += getDelta().toFloat()
        resetDelta()
    }

    fun resetDelta() {
        lastTime = System.currentTimeMillis()
    }

    fun getDelta() = (System.currentTimeMillis() - lastTime).toDouble() / 1000

    fun getRotationState(speed: Float): Float {
        return (animationStage * speed) % 360
    }

    fun getMotionState(speed: Float): Float {
        return Math.sin(Math.toRadians(getRotationState(speed).toDouble())).toFloat()
    }
}