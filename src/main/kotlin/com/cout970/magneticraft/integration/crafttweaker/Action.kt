package com.cout970.magneticraft.integration.crafttweaker

import crafttweaker.IAction

class Action(val func: () -> Unit, val description: String) : IAction {
    override fun describe(): String = description
    override fun apply() = func()
}