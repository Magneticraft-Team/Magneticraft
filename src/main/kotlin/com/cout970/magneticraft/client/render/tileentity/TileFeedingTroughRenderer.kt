package com.cout970.magneticraft.client.render.tileentity

import com.cout970.magneticraft.block.FEEDING_TROUGH_SIDE_POSITION
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.entity.RenderEntityItem
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.item.EntityItem
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 24/06/2016.
 */
object TileFeedingTroughRenderer : TileEntitySpecialRenderer<TileFeedingTrough>() {

    lateinit var entityItem: EntityItem
    lateinit var entityRenderer: RenderEntityItem
    private var initialized = false

    override fun renderTileEntityAt(te: TileFeedingTrough?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        if (te == null) return
        if (!initialized) init()
        val item = te.inventory.getStackInSlot(0) ?: return
        val itemToRender = item.copy().apply { stackSize = 1 }
        pushMatrix()
        translate(x, y, z)
        val i = 0.5f
        translate(i, 0f, i)
        val dir = te.world.getBlockState(te.pos).getValue(FEEDING_TROUGH_SIDE_POSITION)
        rotate(if (dir.axis == EnumFacing.Axis.Z) dir.horizontalAngle else dir.opposite.horizontalAngle, 0f, 1f, 0f)
        translate(-i, 0f, -i)
        entityItem.setEntityItemStack(itemToRender)
        //fix rotation
        entityItem.hoverStart = 0f
        val pixel = 0.0625
        //right
        pushMatrix()
        translate(pixel * 5, -pixel * 2, pixel * 11)
        rotate(10f, 0f, 0f, 1f)
        rotate(-10f, 1f, 0f, 0f)
        rotate(90f, 0f, 1f, 0f)
        scale(1.5, 1.5, 1.5)
        entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
        popMatrix()
        pushMatrix()
        translate(pixel * 5, -pixel * 2, pixel * 25)
        rotate(10f, 0f, 0f, 1f)
        rotate(-10f, 1f, 0f, 0f)
        rotate(90f, 0f, 1f, 0f)
        scale(1.5, 1.5, 1.5)
        entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
        popMatrix()
        //TODO
//        if (item.stackSize < 8) {
//            popMatrix()
//            return
//        }
        //left
        pushMatrix()
        translate(pixel * 11, -pixel * 2, pixel * 8)
        rotate(-10f, 0f, 0f, 1f)
        rotate(10f, 1f, 0f, 0f)
        rotate(-90f, 0f, 1f, 0f)
        scale(1.5, 1.5, 1.5)
        entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
        popMatrix()
        pushMatrix()
        translate(pixel * 11, -pixel * 2, pixel * 21)
        rotate(-10f, 0f, 0f, 1f)
        rotate(10f, 1f, 0f, 0f)
        rotate(-90f, 0f, 1f, 0f)
        scale(1.5, 1.5, 1.5)
        entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
        popMatrix()
        //bottom
        pushMatrix()
        translate(pixel * 3, pixel * 2, pixel * 14)
        rotate(90f, 0f, 0f, 1f)
        rotate(-90f - 45f, 1f, 0f, 0f)
        rotate(-90f, 0f, 1f, 0f)
        scale(1.25, 1.25, 1.25)
        entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
        popMatrix()
        pushMatrix()
        translate(pixel * 13, pixel * 2, pixel * 18)
        rotate(90f, 0f, 0f, 1f)
        rotate(50f, 1f, 0f, 0f)
        rotate(-90f, 0f, 1f, 0f)
        scale(1.25, 1.25, 1.25)
        entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
        popMatrix()
        pushMatrix()
        translate(pixel * 15, pixel * 3, pixel * 17)
        rotate(90f, 0f, 0f, 1f)
        rotate(-90f, 0f, 1f, 0f)
        scale(1.25, 1.25, 1.25)
        entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
        popMatrix()
        //front
        pushMatrix()
        translate(pixel * 8, pixel * 0, pixel * 2.5)
        scale(1.25, 1.25, 1.25)
        entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
        popMatrix()
        //back
        pushMatrix()
        translate(pixel * 8, pixel * 0, 2 - pixel * 2.5)
        scale(1.25, 1.25, 1.25)
        entityRenderer.doRender(entityItem, 0.0, 0.0, 0.0, 0f, 0f)
        popMatrix()
        popMatrix()
    }

//    private fun transform(pos: Vec3d, rot: Vec3d, rotPoint: Vec3d, scale: Vec3d) {
//        val pixel = 0.0625
//        translate(pos.xCoord * pixel, pos.yCoord * pixel, pos.zCoord * pixel)
//        translate(rotPoint.xCoord * pixel, rotPoint.yCoord * pixel, rotPoint.zCoord * pixel)
//        rotate(rot.zCoord.toFloat(), 0f, 0f, 1f)
//        rotate(rot.yCoord.toFloat(), 0f, 1f, 0f)
//        rotate(rot.xCoord.toFloat(), 1f, 0f, 0f)
//        translate(-rotPoint.xCoord * pixel, -rotPoint.yCoord * pixel, -rotPoint.zCoord * pixel)
//        scale(scale.xCoord, scale.yCoord, scale.zCoord)
//    }

    private fun init() {
        initialized = true
        entityItem = EntityItem(Minecraft.getMinecraft().theWorld)
        entityRenderer = object : RenderEntityItem(Minecraft.getMinecraft().renderManager, Minecraft.getMinecraft().renderItem) {
            override fun shouldBob(): Boolean = false
            override fun shouldSpreadItems(): Boolean = false
        }
    }
}