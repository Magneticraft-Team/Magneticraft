package com.cout970.magneticraft.systems.integration.crafttweaker

import com.cout970.magneticraft.api.internal.registries.tool.wrench.WrenchRegistry
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@Suppress("UNUSED")
@ZenClass("mods.magneticraft.Wrench")
@ZenRegister
object Wrench {

    @ZenMethod
    @JvmStatic
    fun addWrench(item: IIngredient) {
        CraftTweakerPlugin.delayExecution {
            val inStack = item.items

            inStack.ifEmpty {
                ctLogError("[Wrench] Invalid input stack: EMPTY")
                return@delayExecution
            }

            for (inputItem in inStack) {
                applyAction("Adding $item as Wrench") {
                    WrenchRegistry.registerWrench(inputItem.toStack())
                }
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