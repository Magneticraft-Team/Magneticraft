@file:Suppress("unused")

package com.cout970.magneticraft.misc

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.misc.vector.Vec2d
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.client.resources.IResource
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation

/**
 * Created by cout970 on 08/07/2016.
 */

fun resource(path: String) = ResourceLocation(MOD_ID, path)

fun guiTexture(path: String) = ResourceLocation(MOD_ID, "textures/gui/$path.png")

fun IBlockState.prettyFormat(): String = stack().displayName

fun String.i18n(vararg args: Any): ITextComponent = TextComponentTranslation(this, *args)

val String.box: Vec2d
    get() = Minecraft.getMinecraft().fontRenderer.run {
        Vec2d(getStringWidth(this@box), FONT_HEIGHT)
    }

fun ResourceLocation.toModel(str: String) = ModelResourceLocation(this, str)

fun ModelResourceLocation.toResourceLocation() = ResourceLocation(resourceDomain, resourcePath)

fun ResourceLocation.toResource(): IResource {
    val resourceManager = Minecraft.getMinecraft().resourceManager
    return resourceManager.getResource(this)
}

fun ResourceLocation.addPrefix(str: String): ResourceLocation = ResourceLocation(resourceDomain, str + resourcePath)
fun ResourceLocation.addPostfix(str: String): ResourceLocation = ResourceLocation(resourceDomain, resourcePath + str)