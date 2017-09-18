package com.cout970.magneticraft.integration.crafttweaker

/**
 * Created by cout970 on 2017/09/18.
 */
object CraftTweakerPlugin {

    private val delayedActions = mutableListOf<() -> Unit>()

    fun runActions() {
        delayedActions.forEach { it.invoke() }
    }

    fun delayExecution(action: () -> Unit) {
        delayedActions += action
    }
}