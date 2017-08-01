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
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraftforge.fluids.FluidRegistry

/**
 * Created by cout970 on 11/07/2016.
 */

class CompFluidBar(
        bottomPos: Vec2d,
        val tank: Tank
) : IComponent {

    override val pos: IVector2 = bottomPos.copy(y = bottomPos.y - 48)
    override val size: IVector2 = Vec2d(5, 48)
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        if (tank.clientFluidAmount > 0) {
            val fluid = FluidRegistry.getFluid(tank.clientFluidName)
            if (fluid != null) {
                val level = (tank.clientFluidAmount * 48 / tank.capacity)
                val texture = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(fluid.still.toString())
                if (texture != null) {
                    gui.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

                    GlStateManager.enableBlend()
                    gui.drawSprite(Vec2d(pos.x, pos.y + 48 - level), Vec2d(size.x, level), texture)
                    GlStateManager.disableBlend()

                    gui.bindTexture(BAR_TEXTURES)
                    gui.drawTexture(DrawableBox(
                            screen = pos to size,
                            texture = vec2Of(59, 0) to size,
                            textureSize = Vec2d(64, 64)
                    ))
                }
            }
        }
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (pos to size)) {
            val list = if (tank.clientFluidName.isNullOrEmpty())
                listOf("Fluid: Empty")
            else
                listOf("Fluid: ${tank.clientFluidName}", "Amount: ${tank.clientFluidAmount}")
            gui.drawHoveringText(list, mouse)
        }
    }
}