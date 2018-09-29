package com.cout970.magneticraft.features.fluid_machines

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.Sprite
import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.vector.lowercaseName
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.systems.tilemodules.ModulePipe
import com.cout970.magneticraft.systems.tilerenderers.*
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
object TileRendererSmallTank : BaseTileRenderer<TileSmallTank>() {

    override fun init() {
        createModel(Blocks.smallTank,
            ModelSelector("shell", FilterNot(FilterString("base")))
        )
        createModelWithoutTexture(Blocks.smallTank,
            ModelSelector("base", FilterString("base"))
        )
    }

    override fun render(te: TileSmallTank) {

        if (MinecraftForgeClient.getRenderPass() == 0) {
            val loc = if (te.toggleExportModule.enable) "out" else "in"
            bindTexture(resource("textures/blocks/fluid_machines/small_tank_$loc.png"))
            renderModel("base")

            renderModel("shell")
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

@RegisterRenderer(TileIronPipe::class)
object TileRendererIronPipe : BaseTileRenderer<TileIronPipe>() {

    override fun init() {
        val parts = listOf(
            "center", "up", "down", "north", "south", "east", "west", "wt", "wb", "et", "eb", "nt",
            "nb", "st", "sb", "nw", "sw", "ne", "se", "cdown", "cup", "cnorth", "csouth", "cwest", "ceast"
        )
        createModel(Blocks.ironPipe, parts.map { ModelSelector(it, FilterString(it)) })
    }

    override fun render(te: TileIronPipe) {

        if (Debug.DEBUG) {
            Utilities.renderFloatingLabel("${te.tank.fluidAmount}", vec3Of(0, 1, 0))
            Utilities.setColor((te.pipeModule.pipeNetwork?.hashCode() ?: 0) or 0xFF000000.toInt())
        }

        val pipeMap = enumValues<EnumFacing>().map { te.pipeModule.getConnectionType(it, true) }

        renderModel("center")
        if (Debug.DEBUG) GL11.glColor4f(1f, 1f, 1f, 1f)

        enumValues<EnumFacing>().forEach { facing ->

            when (pipeMap[facing.ordinal]) {
                ModulePipe.ConnectionType.PIPE -> renderModel(facing.lowercaseName)
                ModulePipe.ConnectionType.NONE -> Unit
                ModulePipe.ConnectionType.TANK -> {
                    renderModel(facing.lowercaseName)

                    val state = te.pipeModule.connectionStates[facing.ordinal]
                    val color = when (state) {
                        ModulePipe.ConnectionState.DISABLE -> return@forEach
                        ModulePipe.ConnectionState.PASSIVE -> -1
                        ModulePipe.ConnectionState.ACTIVE -> Utilities.colorFromRGB(1f, 0.6f, 0.0f)
                    }
                    Utilities.setColor(color)
                    renderModel("c" + facing.lowercaseName)
                    Utilities.setColor(-1)
                }
            }
        }

        val count = pipeMap.count { it != ModulePipe.ConnectionType.NONE }

        if (count == 2) {
            val none = ModulePipe.ConnectionType.NONE

            if (pipeMap[0] != none && pipeMap[1] != none) {
                renderModel("nw")
                renderModel("sw")
                renderModel("ne")
                renderModel("se")
                return

            } else if (pipeMap[2] != none && pipeMap[3] != none) {
                renderModel("wt")
                renderModel("wb")
                renderModel("et")
                renderModel("eb")
                return

            } else if (pipeMap[4] != none && pipeMap[5] != none) {
                renderModel("nt")
                renderModel("nb")
                renderModel("st")
                renderModel("sb")
                return
            }
        }
        renderModel("wt")
        renderModel("wb")
        renderModel("et")
        renderModel("eb")

        renderModel("nt")
        renderModel("nb")
        renderModel("st")
        renderModel("sb")

        renderModel("nw")
        renderModel("sw")
        renderModel("ne")
        renderModel("se")
    }
}