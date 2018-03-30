package com.cout970.magneticraft.gui.client.components.bars

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
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

            GlStateManager.enableBlend()
            val height = level / 16
            for (h in 0..height) {
                val heightLevel = Math.min(level - h * 16, 16)

                gui.drawSprite(gui.pos + vec2Of(pos.x, pos.y + 48 - heightLevel - h * 16), vec2Of(size.x, heightLevel),
                        texture)
            }
            GlStateManager.disableBlend()
        }

        gui.bindTexture(overlayTexture)

        gui.drawTexture(DrawableBox(gui.pos + pos, size, overlayPos))
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            val list = when (tank.clientFluidAmount != 0 && tank.clientFluidName.isEmpty()) {
                true -> listOf("Fluid: Empty")
                else -> listOf("Fluid: ${tank.clientFluidName}", "Amount: ${tank.clientFluidAmount}")
            }

            gui.drawHoveringText(list, mouse)
        }
    }
}