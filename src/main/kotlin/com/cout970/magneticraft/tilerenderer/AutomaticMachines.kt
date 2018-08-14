package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.AutomaticMachines
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import com.cout970.magneticraft.tilerenderer.core.BaseTileRenderer
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.vector.xd
import com.cout970.magneticraft.util.vector.yd
import com.cout970.magneticraft.util.vector.zd
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileFeedingTrough::class)
object TileRendererFeedingTrough : BaseTileRenderer<TileFeedingTrough>() {

    override fun init() {
        createModel(AutomaticMachines.feedingTrough)
    }

    override fun render(te: TileFeedingTrough) {

        val item = te.invModule.inventory[0]
        val level = when {
            item.count > 32 -> 4
            item.count > 16 -> 3
            item.count > 8 -> 2
            item.count > 0 -> 1
            else -> 0
        }

        Utilities.rotateFromCenter(te.facing, 90f)
        renderModel("default")
        if (level > 0) {
            Utilities.rotateFromCenter(EnumFacing.NORTH, 90f)
            stackMatrix {
                renderSide(level, item)
                rotate(180f, 0.0f, 1.0f, 0.0f)
                translate(-1.0, 0.0, -2.0)
                renderSide(level, item)
            }
        }
    }

    private fun renderSide(level: Int, item: ItemStack) {

        if (level >= 1) {
            pushMatrix()
            transform(Vec3d(2.0, 1.1, 6.0),
                    Vec3d(90.0, 0.0, 0.0),
                    Vec3d(0.0, 0.0, 0.0),
                    Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()


            pushMatrix()
            transform(Vec3d(7.5, 1.5, 7.0),
                    Vec3d(90.0, 30.0, 0.0),
                    Vec3d(0.0, 0.0, 0.0),
                    Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()
        }

        if (level >= 2) {
            pushMatrix()
            transform(Vec3d(0.0, 2.0, 12.0),
                    Vec3d(90.0, -30.0, 0.0),
                    Vec3d(16.0, 0.0, 0.0),
                    Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()

            pushMatrix()
            transform(Vec3d(16.5, 2.5, 12.5),
                    Vec3d(90.0, -30.0, 0.0),
                    Vec3d(16.0, 0.0, 0.0),
                    Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()
        }

        if (level >= 3) {
            pushMatrix()
            transform(Vec3d(1.0, 1.0, 7.0),
                    Vec3d(-10.0, 22.0, 4.0),
                    Vec3d(0.0, 0.0, 0.0),
                    Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()

            pushMatrix()
            transform(Vec3d(0.0, 1.0, 15.0),
                    Vec3d(10.0, -22.0, -4.0),
                    Vec3d(16.0, 0.0, 0.0),
                    Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()

            pushMatrix()
            transform(Vec3d(-2.5, -2.0, -4.0),
                    Vec3d(20.0, -90.0, 5.0),
                    Vec3d(0.0, 0.0, 8.0),
                    Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()
        }

        if (level >= 4) {
            pushMatrix()
            transform(Vec3d(5.0, 0.0, 4.5),
                    Vec3d(-10.0, 0.0, 0.0),
                    Vec3d(0.0, 0.0, 0.0),
                    Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()

            pushMatrix()
            transform(Vec3d(7.0, 0.0, 11.0),
                    Vec3d(14.0, 0.0, 0.0),
                    Vec3d(0.0, 0.0, 0.0),
                    Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()

            pushMatrix()
            transform(Vec3d(12.0, 0.0, 10.0),
                    Vec3d(14.0, 0.0, 0.0),
                    Vec3d(0.0, 0.0, 0.0),
                    Vec3d(1.0, 1.0, 1.0))
            renderItem(item)
            popMatrix()
        }
    }

    private fun transform(pos: Vec3d, rot: Vec3d, rotPos: Vec3d, scale: Vec3d) {
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

    private fun renderItem(stack: ItemStack) {
        Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.GROUND)
    }
}
