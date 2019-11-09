@file:Suppress("unused")

package com.cout970.magneticraft.misc

import com.cout970.magneticraft.IBlockState
import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.misc.vector.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.resources.IResource
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent

/**
 * Created by cout970 on 08/07/2016.
 */

fun resource(path: String) = ResourceLocation(MOD_ID, path)

fun guiTexture(path: String) = ResourceLocation(MOD_ID, "textures/gui/$path.png")

fun IBlockState.prettyFormat(): ITextComponent = stack().displayName

fun String.i18n(vararg args: Any): ITextComponent = TranslationTextComponent(this, *args)

val String.box: Vec2d
    get() = Minecraft.getInstance().fontRenderer.run {
        Vec2d(getStringWidth(this@box), FONT_HEIGHT)
    }

fun ResourceLocation.toModel(str: String) = ModelResourceLocation(this, str)

fun ModelResourceLocation.toResourceLocation() = ResourceLocation(namespace, path)

fun ResourceLocation.toResource(): IResource {
    val resourceManager = Minecraft.getInstance().resourceManager
    return resourceManager.getResource(this)
}

fun ResourceLocation.addPrefix(str: String): ResourceLocation = ResourceLocation(namespace, str + path)
fun ResourceLocation.addPostfix(str: String): ResourceLocation = ResourceLocation(namespace, path + str)