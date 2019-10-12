package com.cout970.magneticraft.systems.gui.components.bars

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.gui.format
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidRegistry

/**
 * Created by cout970 on 11/07/2016.
 */

class CompFluidBar(
    topPos: IVector2,
    val overlayTexture: ResourceLocation,
    val overlayPos: IVector2,
    val tank: Tank
) : IComponent {

    override val pos = topPos
    override val size = vec2Of(16, 48)
    override lateinit var gui: IGui

    fun getFluidTexture(): TextureAtlasSprite? {
        if (tank.clientFluidName.isEmpty()) return null

        val fluid = FluidRegistry.getFluid(tank.clientFluidName) ?: return null
        val textureMap = Minecraft.getMinecraft().textureMapBlocks

        return textureMap.getAtlasSprite(fluid.still.toString())
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val texture = getFluidTexture()
        if (tank.clientFluidAmount > 0 && texture != null) {
            val level = (tank.clientFluidAmount * 48 / tank.capacity)

            gui.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

            enableBlend()
            val height = level / 16
            for (h in 0..height) {
                val heightLevel = Math.min(level - h * 16, 16)

                gui.drawSprite(gui.pos + vec2Of(pos.x, pos.y + 48 - heightLevel - h * 16), vec2Of(size.x, heightLevel),
                    texture)
            }
            disableBlend()
        }

        gui.bindTexture(overlayTexture)

        gui.drawTexture(DrawableBox(gui.pos + pos, size, overlayPos))
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            val list = when (tank.clientFluidAmount == 0) {
                true -> listOf("Fluid: Empty")
                else -> listOf("Fluid: ${tank.clientFluidName}", "Amount: ${tank.clientFluidAmount}")
            }

            gui.drawHoveringText(list, mouse)
        }
    }
}

class CompFluidBar2(
    override val pos: IVector2,
    val tank: Tank
) : IComponent {

    override val size = vec2Of(18, 50)
    override lateinit var gui: IGui

    lateinit var back: DrawableBox
    lateinit var front: DrawableBox

    override fun init() {
        back = DrawableBox(
            screenPos = gui.pos + pos,
            screenSize = vec2Of(18, 50),
            texturePos = vec2Of(102, 61),
            textureSize = vec2Of(18, 50),
            textureScale = vec2Of(256)
        )
        front = DrawableBox(
            screenPos = gui.pos + pos + vec2Of(1),
            screenSize = vec2Of(16, 48),
            texturePos = vec2Of(121, 61),
            textureSize = vec2Of(16, 48),
            textureScale = vec2Of(256)
        )
    }

    fun getFluidTexture(): TextureAtlasSprite? {
        if (tank.clientFluidName.isEmpty()) return null

        val fluid = FluidRegistry.getFluid(tank.clientFluidName) ?: return null
        val textureMap = Minecraft.getMinecraft().textureMapBlocks

        return textureMap.getAtlasSprite(fluid.still.toString())
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        color(1f, 1f, 1f, 1f)
        gui.bindTexture(guiTexture("misc"))
        gui.drawTexture(back)
        enableAlpha()
        enableBlend()

        if (tank.clientFluidAmount > 0) {
            val texture = getFluidTexture()
            if (texture != null) {
                val level = tank.clientFluidAmount * 48 / tank.capacity

                gui.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)


                val height = level / 16
                for (h in 0..height) {
                    val heightLevel = Math.min(level - h * 16, 16)

                    gui.drawSprite(gui.pos + vec2Of(pos.x + 1, pos.y + 1 + 48 - heightLevel - h * 16), vec2Of(16, heightLevel),
                        texture)
                }
            }
        }

        gui.bindTexture(guiTexture("misc"))
        gui.drawTexture(front)
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            val list = when (tank.clientFluidAmount == 0) {
                true -> listOf("Fluid: Empty")
                else -> listOf("Fluid: ${tank.clientFluidName}", "Amount: ${tank.clientFluidAmount.format()} mB")
            }

            gui.drawHoveringText(list, mouse)
        }
    }
}