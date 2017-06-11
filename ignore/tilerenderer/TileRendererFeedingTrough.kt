package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.BlockFeedingTrough
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import com.cout970.magneticraft.util.vector.xd
import com.cout970.magneticraft.util.vector.yd
import com.cout970.magneticraft.util.vector.zd
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderEntityItem
import net.minecraft.entity.item.EntityItem
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 24/06/2016.
 */
object TileRendererFeedingTrough : TileEntityRenderer<TileFeedingTrough>() {

    lateinit var entityItem: EntityItem
    lateinit var entityRenderer: RenderEntityItem
    private var initialized = false

    override fun renderTileEntityAt(te: TileFeedingTrough, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        if (!initialized) init()
        val item = te.inventory.getStackInSlot(0) ?: return
        val itemToRender = item.copy().apply { stackSize = 1 }
        pushMatrix()
        translate(x, y, z)
        val i = 0.5f
        translate(i, 0f, i)
        val dir = te.world.getBlockState(te.pos).getValue(BlockFeedingTrough.FEEDING_TROUGH_SIDE_POSITION)
        rotate(if (dir.axis == EnumFacing.Axis.Z) dir.horizontalAngle else dir.opposite.horizontalAngle, 0f, 1f, 0f)
        translate(-i, 0f, -i)
        entityItem.setEntityItemStack(itemToRender)
        //fix rotation
        entityItem.hoverStart = 0f
        var level = 0
        if (item.stackSize > 32) {
            level = 4
        } else if (item.stackSize > 16) {
            level = 3
        } else if (item.stackSize > 8) {
            level = 2
        } else if (item.stackSize > 0) {
            level = 1
        }
        side(level)
        rotate(180f, 0.0f, 1.0f, 0.0f)
        translate(-1.0, 0.0, -2.0)
        side(level)

        popMatrix()
    }

    fun side(level: Int) {

        //level 1
        if (level >= 1) {
            pushMatrix()
            tranform(Vec3d(2.0, 2.0, 3.0), Vec3d(90.0, 0.0, 0.0), Vec3d(0.0, 0.0, 0.0), Vec3d(1.0, 1.0, 1.0))
            entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
            popMatrix()


            pushMatrix()
            tranform(Vec3d(4.0, 2.0, 3.0), Vec3d(90.0, 30.0, 0.0), Vec3d(0.0, 0.0, 0.0), Vec3d(1.0, 1.0, 1.0))
            entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
            popMatrix()
        }

        //level2
        if (level >= 2) {
            pushMatrix()
            tranform(Vec3d(8.0, 3.0, 10.0), Vec3d(90.0, -30.0, 0.0), Vec3d(16.0, 0.0, 0.0), Vec3d(1.0, 1.0, 1.0))
            entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
            popMatrix()

            pushMatrix()
            tranform(Vec3d(12.0, 2.5, 10.0), Vec3d(90.0, -30.0, -5.0), Vec3d(16.0, 0.0, 0.0), Vec3d(1.0, 1.0, 1.0))
            entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
            popMatrix()
        }

        //level3
        if (level >= 3) {
            pushMatrix()
            tranform(Vec3d(2.0, 1.0, 7.0), Vec3d(-10.0, 22.0, 4.0), Vec3d(0.0, 0.0, 0.0), Vec3d(1.0, 1.0, 1.0))
            entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
            popMatrix()

            pushMatrix()
            tranform(Vec3d(1.0, 1.0, 15.0), Vec3d(10.0, -22.0, -4.0), Vec3d(16.0, 0.0, 0.0), Vec3d(1.0, 1.0, 1.0))
            entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
            popMatrix()

            pushMatrix()
            tranform(Vec3d(-1.0, -2.0, -4.0), Vec3d(20.0, -90.0, 5.0), Vec3d(0.0, 0.0, 8.0), Vec3d(1.0, 1.0, 1.0))
            entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
            popMatrix()
        }

        //level4
        if (level >= 4) {
            pushMatrix()
            tranform(Vec3d(5.0, 0.0, 4.5), Vec3d(-10.0, 0.0, 0.0), Vec3d(0.0, 0.0, 0.0), Vec3d(1.0, 1.0, 1.0))
            entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
            popMatrix()

            pushMatrix()
            tranform(Vec3d(7.0, 0.0, 11.0), Vec3d(14.0, 0.0, 0.0), Vec3d(0.0, 0.0, 0.0), Vec3d(1.0, 1.0, 1.0))
            entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
            popMatrix()

            pushMatrix()
            tranform(Vec3d(12.0, 0.0, 10.0), Vec3d(14.0, 0.0, 0.0), Vec3d(0.0, 0.0, 0.0), Vec3d(1.0, 1.0, 1.0))
            entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
            popMatrix()
        }
    }

    fun tranform(pos: Vec3d, rot: Vec3d, rotPos: Vec3d, scale: Vec3d) {
        translate(PIXEL * 16, 0.0, 0.0)
        rotate(0, -90, 0)

        translate(pos.xd * PIXEL, pos.yd * PIXEL, pos.zd * PIXEL)

        translate(rotPos.xd * PIXEL, rotPos.yd * PIXEL, rotPos.zd * PIXEL)
        rotate(rot.xd, rot.yd, rot.zd)
        translate(-rotPos.xd * PIXEL, -rotPos.yd * PIXEL, -rotPos.zd * PIXEL)

        translate(PIXEL * 4, 0.0, 0.0)
        GlStateManager.scale(scale.xd, scale.yd, scale.zd)
    }

    private fun rotate(x: Number, y: Number, z: Number) {
        rotate(z, 0f, 0f, 1f)
        rotate(y, 0f, 1f, 0f)
        rotate(x, 1f, 0f, 0f)
    }

    private fun init() {
        initialized = true
        entityItem = EntityItem(Minecraft.getMinecraft().theWorld)
        entityRenderer = object : RenderEntityItem(Minecraft.getMinecraft().renderManager, Minecraft.getMinecraft().renderItem) {
            override fun shouldBob(): Boolean = false
            override fun shouldSpreadItems(): Boolean = false
        }
    }
}