package com.cout970.magneticraft.tilerender

import coffee.cypher.mcextlib.extensions.inventories.get
import com.cout970.magneticraft.tileentity.TileTableSieve
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.entity.RenderEntityItem
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.item.EntityItem

/**
 * Created by cout970 on 19/06/2016.
 */
object TileRendererTableSieve : TileEntitySpecialRenderer<TileTableSieve>() {

    lateinit var entity: EntityItem
    lateinit var renderer: RenderEntityItem
    private var init = false

    override fun renderTileEntityAt(te: TileTableSieve?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        if (!init) init()
        val item = te!!.inventory[0]
        if (item != null) {
            pushMatrix()
            translate(x, y, z)
            color(1f, 1f, 1f, 1f)
            //setup entity renderer
            entity.setEntityItemStack(te.inventory[0]!!.copy().apply { stackSize = 1 })
            //check if the item is in 3D
            val model = Minecraft.getMinecraft().renderItem.getItemModelWithOverrides(item, entity.worldObj, null)

            if (model.isGui3d) {
                val size = item.stackSize
                var process = 0.0
                if (size != 0) {
                    process = (size + (1 - (te.tickCounter / te.updateTime.toDouble()))) / 64
                }

                scale(2.0, 1.0, 2.0)
                translate(0.0625 * 4, 0.438 + process * 0.25, 0.0625 * 4)
                //fix item rotation
                val rot = ((entity.age.toFloat()) / 20.0f + entity.hoverStart) * (180f / Math.PI.toFloat())
                rotate(-rot, 0.0f, 1.0f, 0.0f)
                //render item
                renderer.doRender(entity, 0.0, 0.0, 0.0, 0f, 0f)
            }


            popMatrix()
        }
    }

    private fun init() {
        init = true
        entity = EntityItem(Minecraft.getMinecraft().theWorld)
        renderer = object : RenderEntityItem(Minecraft.getMinecraft().renderManager, Minecraft.getMinecraft().renderItem) {

            override fun shouldSpreadItems(): Boolean {
                return false
            }

            override fun shouldBob(): Boolean {
                return false
            }
        }
    }
}