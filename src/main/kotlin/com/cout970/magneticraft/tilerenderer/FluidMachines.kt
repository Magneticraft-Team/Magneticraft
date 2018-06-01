package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.Sprite
import com.cout970.magneticraft.block.FluidMachines
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileCopperTank
import com.cout970.magneticraft.tileentity.TileIronPipe
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModulePipe
import com.cout970.magneticraft.tilerenderer.core.*
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.fluids.FluidStack
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/08/28.
 */

@RegisterRenderer(TileCopperTank::class)
object TileRendererCopperTank : TileRendererSimple<TileCopperTank>(
        modelLocation = modelOf(FluidMachines.copperTank)
) {

    override fun renderModels(models: List<ModelCache>, te: TileCopperTank) {

        if (MinecraftForgeClient.getRenderPass() == 0) {
            models.last().renderTextured()
        } else {
            if (te.tank.isNonEmpty()) {
                val fluidStack = te.tank.fluid ?: return
                val fillPercent = fluidStack.amount / te.tank.capacity.toDouble()
                val color = fluidStack.fluid.getColor(fluidStack)

                bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

                te.fluidRenderer.sprites = listOf(getFluidSprite(fluidStack))
                te.fluidRenderer.size = vec3Of(12.px - 0.002, 15.px * fillPercent - 0.002, 12.px - 0.002)
                te.fluidRenderer.pos = vec3Of(2.px + 0.001, 1.px + 0.001, 2.px + 0.001)
                Utilities.setColor(color)
                te.fluidRenderer.render()
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            }
        }
    }

    fun getFluidSprite(fluidStack: FluidStack): Sprite {
        val loc = fluidStack.fluid.getStill(fluidStack)
        return Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(loc.toString())
    }
}

@RegisterRenderer(TileIronPipe::class)
object TileRendererIronPipe : TileRendererSimple<TileIronPipe>(
        modelLocation = modelOf(FluidMachines.ironPipe),
        filters = filterOf(listOf("base", "up", "down", "north", "south", "west", "east"))
) {

    override fun renderModels(models: List<ModelCache>, te: TileIronPipe) {
        models[0].renderTextured()
        sidesToIndices.forEach {
            val tile = te.world.getTileEntity(te.pos + it.first) ?: return@forEach

            FLUID_HANDLER!!.fromTile(tile, it.first.opposite)?.let { _ ->
                models[it.second].render()
            }

            (tile as? TileBase)?.getModule<ModulePipe>()?.let { mod ->
                if (mod.type == te.pipeModule.type) {
                    models[it.second].render()
                }
            }
        }
    }
}