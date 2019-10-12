package com.cout970.magneticraft.systems.gui

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.misc.t
import com.cout970.magneticraft.systems.config.ConfigHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory

class ModGuiFactory : IModGuiFactory {

    override fun hasConfigGui(): Boolean = true

    override fun createConfigGui(parentScreen: GuiScreen?): GuiScreen = ModGuiConfig(parentScreen)

    override fun runtimeGuiCategories(): MutableSet<IModGuiFactory.RuntimeOptionCategoryElement> = mutableSetOf()

    override fun initialize(minecraftInstance: Minecraft?) {}
}

class ModGuiConfig(parentScreen: GuiScreen?) : net.minecraftforge.fml.client.config.GuiConfig(
        parentScreen,
        ConfigHandler.getConfigElements(),
        MOD_ID,
        false,
        false,
        t("gui.magneticraft.config_gui.title")
)