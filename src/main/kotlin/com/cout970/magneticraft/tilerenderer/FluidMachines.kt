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
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.TileRendererSimple
import com.cout970.magneticraft.tilerenderer.core.px
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.EnumFacing
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

@RegisterRenderer(TileIronPipe::class)
object TileRendererIronPipe : TileRendererSimple<TileIronPipe>(
        modelLocation = { ModelResourceLocation(FluidMachines.ironPipe.registryName, "model") },
        filters = filterOf(listOf("base", "up", "down", "north", "south", "west", "east"))
) {

    val sides = listOf(
            EnumFacing.UP to 1,
            EnumFacing.DOWN to 2,
            EnumFacing.NORTH to 3,
            EnumFacing.SOUTH to 4,
            EnumFacing.WEST to 5,
            EnumFacing.EAST to 6
    )

    override fun renderModels(models: List<ModelCache>, te: TileIronPipe) {
        models[0].renderTextured()
        sides.forEach {
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
