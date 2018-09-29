package com.cout970.magneticraft.systems.integration.crafttweaker

/**
 * Created by cout970 on 2017/09/18.
 */
object CraftTweakerPlugin {

    private val delayedActions = mutableListOf<() -> Unit>()

    fun runActions() {
        delayedActions.forEach {
            try {
                it.invoke()
            } catch (e: Exception) {
                ctLogError(e.message ?: "Unknown Exception, please read the game log")
                e.printStackTrace()
            }
        }
    }

    fun delayExecution(action: () -> Unit) {
        delayedActions += action
    }
}