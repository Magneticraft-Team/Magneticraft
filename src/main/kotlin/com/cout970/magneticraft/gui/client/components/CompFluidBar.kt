package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.gui.client.IComponent
import com.cout970.magneticraft.gui.client.IGui
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraftforge.fluids.FluidRegistry

/**
 * Created by cout970 on 11/07/2016.
 */

class CompFluidBar(
        val pos: Vec2d,
        val tank: Tank
) : IComponent {

    override val box: Box = Box(pos.copy(y = pos.y - 48), Vec2d(5, 48))

    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        if (tank.clientFluidAmount > 0) {
            gui.run {
                val fluid = FluidRegistry.getFluid(tank.clientFluidName)
                if (fluid != null) {
                    val level = (tank.clientFluidAmount * 48 / tank.capacity).toInt()
                    val texture = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(fluid.still.toString())
                    if (texture != null) {
                        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                        GlStateManager.enableBlend()
                        drawTexture(Box(Vec2d(this@CompFluidBar.box.pos.x, this@CompFluidBar.box.pos.y + 48 - level), Vec2d(this@CompFluidBar.box.size.x, level)), texture)
                        GlStateManager.disableBlend()
                        bindTexture(BAR_TEXTURES)
                        drawScaledTexture(Box(this@CompFluidBar.box.pos, this@CompFluidBar.box.size), Vec2d(59, 0), Vec2d(64, 64))
                    }
                }
            }
        }
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in box) {
            val list = if (tank.clientFluidName.isNullOrEmpty())
                listOf("Fluid: Empty")
            else
                listOf("Fluid: ${tank.clientFluidName}", "Amount: ${tank.clientFluidAmount}")
            gui.drawHoveringText(list, mouse)
        }
    }
}