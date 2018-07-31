package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.Sprite
import com.cout970.magneticraft.block.FluidMachines
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileIronPipe
import com.cout970.magneticraft.tileentity.TileSmallTank
import com.cout970.magneticraft.tileentity.modules.ModulePipe
import com.cout970.magneticraft.tilerenderer.core.*
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.fluids.FluidStack
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/08/28.
 */

@RegisterRenderer(TileSmallTank::class)
object TileRendererSmallTank : TileRendererSimple<TileSmallTank>(
        modelLocation = modelOf(FluidMachines.smallTank),
        filters = listOf<(String) -> Boolean>({ it != "base" }, { it == "base" })
) {

    override fun renderModels(models: List<ModelCache>, te: TileSmallTank) {

        if (MinecraftForgeClient.getRenderPass() == 0) {
            val loc = if (te.toggleExportModule.enable) "out" else "in"
            bindTexture(resource("textures/blocks/fluid_machines/small_tank_$loc.png"))
            models.last().render()

            models.first().renderTextured()
        } else {
            if (te.tank.isNonEmpty()) {
                val fluidStack = te.tank.fluid ?: return
                val fillPercent = fluidStack.amount / te.tank.capacity.toDouble()
                val color = fluidStack.fluid.getColor(fluidStack)

                bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

                te.fluidRenderer.sprites = listOf(getFluidSprite(fluidStack))
                te.fluidRenderer.size = vec3Of(16.px - 0.002, 15.px * fillPercent - 0.002, 16.px - 0.002)
                te.fluidRenderer.pos = vec3Of(0.px + 0.001, 1.px + 0.001, 0.px + 0.001)
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

private val map = mapOf(
        "center" to 0,
        "up" to 1,
        "down" to 2,
        "north" to 3,
        "south" to 4,
        "east" to 5,
        "west" to 6,
        "wt" to 7,
        "wb" to 8,
        "et" to 9,
        "eb" to 10,
        "nt" to 11,
        "nb" to 12,
        "st" to 13,
        "sb" to 14,
        "nw" to 15,
        "sw" to 16,
        "ne" to 17,
        "se" to 18,
        "cdown" to 19,
        "cup" to 20,
        "cnorth" to 21,
        "csouth" to 22,
        "cwest" to 23,
        "ceast" to 24
)

@RegisterRenderer(TileIronPipe::class)
object TileRendererIronPipe : TileRendererSimple<TileIronPipe>(
        modelLocation = modelOf(FluidMachines.ironPipe),
        filters = map.keys.sortedBy { map[it] }.map { name -> { it: String -> it == name } }
) {

    override fun renderModels(models: List<ModelCache>, te: TileIronPipe) {

        if (Debug.DEBUG) {
            Utilities.renderFloatingLabel("${te.tank.fluidAmount}", vec3Of(0, 1, 0))
            Utilities.setColor((te.pipeModule.pipeNetwork?.hashCode() ?: 0) or 0xFF000000.toInt())
        }

        val pipeMap = enumValues<EnumFacing>().map { te.pipeModule.getConnectionType(it, true) }

        models[map["center"]!!].renderTextured()
        if (Debug.DEBUG) GL11.glColor4f(1f, 1f, 1f, 1f)

        enumValues<EnumFacing>().forEach {

            when (pipeMap[it.ordinal]) {
                ModulePipe.ConnectionType.PIPE -> models[map[it.name.toLowerCase()]!!].render()
                ModulePipe.ConnectionType.TANK -> {
                    models[map[it.name.toLowerCase()]!!].render()

                    val state = te.pipeModule.connectionStates[it.ordinal]
                    val color = when (state) {
                        ModulePipe.ConnectionState.DISABLE -> return@forEach
                        ModulePipe.ConnectionState.PASSIVE -> -1
                        ModulePipe.ConnectionState.ACTIVE -> Utilities.colorFromRGB(1f, 0.6f, 0.0f)
                    }
                    Utilities.setColor(color)
                    models[map["c" + it.name.toLowerCase()]!!].render()
                    Utilities.setColor(-1)
                }
                ModulePipe.ConnectionType.NONE -> {
                    // Ingored
                }
            }
        }

        val count = pipeMap.count { it != ModulePipe.ConnectionType.NONE }

        if (count == 2) {
            val none = ModulePipe.ConnectionType.NONE

            if (pipeMap[0] != none && pipeMap[1] != none) {
                models[map["nw"]!!].render()
                models[map["sw"]!!].render()
                models[map["ne"]!!].render()
                models[map["se"]!!].render()
                return

            } else if (pipeMap[2] != none && pipeMap[3] != none) {
                models[map["wt"]!!].render()
                models[map["wb"]!!].render()
                models[map["et"]!!].render()
                models[map["eb"]!!].render()
                return

            } else if (pipeMap[4] != none && pipeMap[5] != none) {
                models[map["nt"]!!].render()
                models[map["nb"]!!].render()
                models[map["st"]!!].render()
                models[map["sb"]!!].render()
                return
            }
        }
        models[map["wt"]!!].render()
        models[map["wb"]!!].render()
        models[map["et"]!!].render()
        models[map["eb"]!!].render()

        models[map["nt"]!!].render()
        models[map["nb"]!!].render()
        models[map["st"]!!].render()
        models[map["sb"]!!].render()

        models[map["nw"]!!].render()
        models[map["sw"]!!].render()
        models[map["ne"]!!].render()
        models[map["se"]!!].render()
    }
}