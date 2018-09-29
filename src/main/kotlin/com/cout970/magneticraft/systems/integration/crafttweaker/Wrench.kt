package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.internal.registries.tool.wrench.WrenchRegistry
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.magneticraft.Wrench")
@ZenRegister
object Wrench {

    @ZenMethod
    @JvmStatic
    fun addWrench(item: IItemStack) {
        CraftTweakerPlugin.delayExecution {
            val inStack = item.toStack()

            inStack.ifEmpty {
                ctLogError("[Wrench] Invalid input stack: EMPTY")
                return@delayExecution
            }

            applyAction("Adding $item as Wrench") {
                WrenchRegistry.registerWrench(inStack)
            }
        }
    }

    @ZenMethod
    @JvmStatic
    fun removeWrench(item: IItemStack) {
        CraftTweakerPlugin.delayExecution {
            val inStack = item.toStack()

            inStack.ifEmpty {
                ctLogError("[Wrench] Cannot remove wrench: Invalid ItemStack: EMPTY")
                return@delayExecution
            }

            applyAction("Removing Wrench $item") {
                WrenchRegistry.removeWrench(inStack)
            }
        }
    }
}