package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.Sprite
import com.cout970.magneticraft.block.FluidMachines
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileCopperTank
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.TileRendererSimple
import com.cout970.magneticraft.tilerenderer.core.px
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 2017/08/28.
 */

@RegisterRenderer(TileCopperTank::class)
object TileRendererCopperTank : TileRendererSimple<TileCopperTank>(
        modelLocation = { ModelResourceLocation(FluidMachines.copperTank.registryName, "model") }
) {


    override fun renderModels(models: List<ModelCache>, te: TileCopperTank) {

        if (MinecraftForgeClient.getRenderPass() == 0) {
            models.last().renderTextured()
        } else {
            if (te.tank.isNonEmpty()) {
                val fluidStack = te.tank.fluid ?: return
                val fillPercent = fluidStack.amount / te.tank.capacity.toDouble()

                bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                te.fluidRenderer.sprites = listOf(getFluidSprite(fluidStack))
                te.fluidRenderer.size = vec3Of(12.px - 0.002, 15.px * fillPercent - 0.002, 12.px - 0.002)
                te.fluidRenderer.pos = vec3Of(2.px + 0.001, 1.px + 0.001, 2.px + 0.001)
                te.fluidRenderer.render()
            }
        }
    }

    fun getFluidSprite(fluidStack: FluidStack): Sprite {
        val loc = fluidStack.fluid.getStill(fluidStack)
        return Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(loc.toString())
    }
}
