package com.cout970.magneticraft.systems.integration.crafttweaker

import crafttweaker.IAction

class Action(val func: () -> Unit, val description: String) : IAction {
    override fun describe(): String = description
    override fun apply() = func()
}