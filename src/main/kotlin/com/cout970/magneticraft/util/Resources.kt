@file:Suppress("unused")

package com.cout970.magneticraft.util

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResource
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation

/**
 * Created by cout970 on 08/07/2016.
 */

fun resource(path: String) = ResourceLocation(MOD_ID, path)

fun String.i18n(vararg args: Any): ITextComponent = TextComponentTranslation(this, *args)

val String.box: Vec2d get() = Minecraft.getMinecraft().fontRendererObj.run {
    Vec2d(getStringWidth(this@box), FONT_HEIGHT)
}

fun ResourceLocation.toModel(str: String) = ModelResourceLocation(this, str)

fun ModelResourceLocation.toResourceLocation() = ResourceLocation(resourceDomain, resourcePath)

fun ResourceLocation.toResource(): IResource {
    val resourceManager = Minecraft.getMinecraft().resourceManager
    return resourceManager.getResource(this)
}