package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.block.Machines
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileConveyorBelt
import com.cout970.magneticraft.tilerenderer.core.*
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemSkull
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/06/16.
 */
@RegisterRenderer(TileConveyorBelt::class)
object TileRendererConveyorBelt : TileRenderer<TileConveyorBelt>() {

    val texture = resource("textures/blocks/machines/conveyor_belt.png")

    var axisBars: ModelCache? = null
    var backLegs: ModelCache? = null
    var frontLegs: ModelCache? = null
    var lateralLeft: ModelCache? = null
    var lateralRight: ModelCache? = null
    var panelLeft: ModelCache? = null
    var panelRight: ModelCache? = null
    var rollers: List<Pair<IVector3, ModelCache?>> = emptyList()


    override fun renderTileEntityAt(te: TileConveyorBelt, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {

        // error loading the model
        axisBars ?: return

        stackMatrix {
            translate(x, y, z)
            Utilities.rotateFromCenter(te.facing)
            renderStaticParts(te, partialTicks)
            renderDynamicParts(te, partialTicks)
            translate(0f, 12.5 * PIXEL, 0f)
            //debug hitboxes
//            renderHitboxes(te)
        }

//        if (te.pos.xi == -261 && te.pos.zi == 322) {
//        renderBitmap(te, x, y, z)
//        }
    }

    fun renderHitboxes(te: TileConveyorBelt) {
        te.conveyorModule.boxes.forEach {
            Utilities.renderBox(it.getHitBox())
        }
    }


    // debug bitmaps
    fun renderBitmap(te: TileConveyorBelt, x: Double, y: Double, z: Double) {
        stackMatrix {
            translate(x, y + 1f, z)
            Utilities.rotateFromCenter(te.facing)

            Utilities.renderBox(vec3Of(1, 0, 0) * PIXEL toAABBWith vec3Of(2, 1, 1) * PIXEL,
                    vec3Of(0, 1, 0))
            Utilities.renderBox(vec3Of(0, 0, 1) * PIXEL toAABBWith vec3Of(1, 1, 2) * PIXEL,
                    vec3Of(0, 0, 1))

            val bitmap2 = te.conveyorModule.generateGlobalBitMap()

            for (i in 0 until 16) {
                for (j in 0 until 16) {
                    if (!bitmap2[i, j]) {
                        Utilities.renderBox(
                                vec3Of(i, 0, j) * PIXEL toAABBWith vec3Of(i + 1, 0, j + 1) * PIXEL,
                                vec3Of(1, 1, 1))
                    } else {
                        Utilities.renderBox(
                                vec3Of(i, 0, j) * PIXEL toAABBWith vec3Of(i + 1, 1, j + 1) * PIXEL,
                                vec3Of(0, 0, 1))
                    }
                }
            }
        }
    }

    fun renderDynamicParts(te: TileConveyorBelt, partialTicks: Float) {

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        te.conveyorModule.boxes.forEach { box ->
            stackMatrix {
                val pos = box.getPos(partialTicks)
                translate(pos.xd, 13.5 * PIXEL, pos.zd)
                renderItem(box.item, te.facing)
            }
        }
    }

    fun renderItem(stack: ItemStack, facing: EnumFacing) {
        if (!Minecraft.getMinecraft().renderItem.shouldRenderItemIn3D(stack) || stack.item is ItemSkull) {
            translate(0.0, -0.9 * PIXEL, 0.0)
            rotate(90f, 1f, 0f, 0f)
            rotate(-facing.horizontalAngle, 0f, 0f, 1f)
            translate(0.0, -1 * PIXEL, 0.0)
            val s = 0.5
            GlStateManager.scale(s, s, s)
        } else {
            rotate(-facing.horizontalAngle, 0f, 1f, 0f)
            translate(0.0, -1.9 * PIXEL, 0.0)
            val s = 0.9
            GlStateManager.scale(s, s, s)
        }

        Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.GROUND)
    }

    fun renderStaticParts(te: TileConveyorBelt, partialTicks: Float) {

        bindTexture(texture)
        axisBars?.render()

        val backTile = te.world.getTile<TileConveyorBelt>(te.pos.add(te.facing.opposite.directionVec))
        if (backTile?.facing == te.facing) {
            stackMatrix {
                translate(0f, 0f, PIXEL)
                backLegs?.render()
            }
        } else {

            backLegs?.render()
        }

        val frontTile = te.world.getTile<TileConveyorBelt>(te.pos.add(te.facing.directionVec))
        if (frontTile?.facing != te.facing) {
            frontLegs?.render()
        }

        val leftTile = te.world.getTile<TileConveyorBelt>(te.pos.add(te.facing.rotateYCCW().directionVec))
        val useLeft = leftTile?.facing == te.facing.rotateY()
        if (useLeft) {
            lateralLeft?.render()
        } else {
            panelLeft?.render()
        }

        val rightTile = te.world.getTile<TileConveyorBelt>(te.pos.add(te.facing.rotateY().directionVec))
        val useRight = rightTile?.facing == te.facing.rotateYCCW()
        if (useRight) {
            lateralRight?.render()
        } else {
            panelRight?.render()
        }

        rollers.forEach {
            if (it.second != null) {
                val angle = te.rotation
                val delta = System.currentTimeMillis() - te.deltaTimer
                te.deltaTimer = System.currentTimeMillis()
                te.rotation = (te.rotation + delta * 0.1f) % 360
                val trans = it.first * PIXEL

                pushMatrix()
                translate(trans.xd, trans.yd, trans.zd)
                rotate(-angle, 1f, 0f, 0f)
                translate(-trans.xd, -trans.yd, -trans.zd)
                it.second?.render()
                popMatrix()
            }
        }
    }

    override fun onModelRegistryReload() {
        val loc = ModelResourceLocation(Machines.conveyorBelt.registryName, "model")
        //cleaning
        axisBars?.clear()
        backLegs?.clear()
        frontLegs?.clear()
        rollers.forEach { it.second?.clear() }
        lateralLeft?.clear()
        lateralRight?.clear()

        axisBars = ModelCacheFactory.createCache(loc) { it.startsWith("axis") }
        rollers = listOf(
                vec3Of(0.0, 11, 1.25) to ModelCacheFactory.createCache(loc) { it == "roller5" }, // 1
                vec3Of(0.0, 11, 3.85) to ModelCacheFactory.createCache(loc) { it == "roller2" }, // 2
                vec3Of(0.0, 11, 6.6) to ModelCacheFactory.createCache(loc) { it == "roller3" }, // 3
                vec3Of(0.0, 11, 9.2) to ModelCacheFactory.createCache(loc) { it == "roller6" }, // 4
                vec3Of(0.0, 11, 11.9) to ModelCacheFactory.createCache(loc) { it == "roller4" }, // 5
                vec3Of(0.0, 11, 14.65) to ModelCacheFactory.createCache(loc) { it == "roller1" } // 6
        )
        lateralLeft = ModelCacheFactory.createCache(loc) { it.startsWith("lateral_left") }
        lateralRight = ModelCacheFactory.createCache(loc) { it.startsWith("lateral_right") }
        panelLeft = ModelCacheFactory.createCache(loc) { it.startsWith("panel_left") }
        panelRight = ModelCacheFactory.createCache(loc) { it.startsWith("panel_right") }
        backLegs = ModelCacheFactory.createCache(loc) { it.startsWith("back_leg") }
        frontLegs = ModelCacheFactory.createCache(loc) { it.startsWith("front_leg") }
    }
}