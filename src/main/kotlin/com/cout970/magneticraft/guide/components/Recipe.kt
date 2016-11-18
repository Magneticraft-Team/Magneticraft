package com.cout970.magneticraft.guide.components

import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.BookPage
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.shuffled
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.ItemStack

class Recipe(
        position: Vec2d,
        val recipe: Array<out Array<out List<ItemStack>>>,
        val result: ItemStack

) : PageComponent(position) {

    companion object {

        const val DISPLAY_TIME = 20

        val GRID_TEXTURE = resource("textures/gui/guide/grid.png")
        val GRID_SIZE = Vec2d(88, 56)
        val STACK_OFFSET = Array(3) { row ->
            Array(3) { column ->
                Vec2d(2 + 18 * column, 2 + 18 * row)
            }
        }
        val STACK_SIZE = Vec2d(16, 16)
        val RESULT_OFFSET = Vec2d(68, 20)
    }

    override val id: String = "recipe"

    override val size = Vec2d(70, 44)

    override fun toGuiComponent(parent: BookPage.Gui): GuiPageComponent = Gui(parent)

    private inner class Gui(parent: BookPage.Gui) : PageComponent.Gui(parent) {
        val slots = recipe.map { it.map { it.shuffled() } }

        override fun draw(mouse: Vec2d, time: Double) {
            parent.gui.drawTexture(drawPos, GRID_SIZE, GRID_TEXTURE)

            GlStateManager.pushMatrix()
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)

            slots.forEachIndexed { rowN, row ->
                row.forEachIndexed { columnN, stacks ->
                    if (stacks.isNotEmpty()) {
                        val displayIndex = ((time.toInt() / DISPLAY_TIME) % stacks.size)

                        parent.gui.drawStack(stacks[displayIndex], drawPos + STACK_OFFSET[rowN][columnN])
                    }
                }
            }

            parent.gui.drawStack(result, drawPos + RESULT_OFFSET)

            GlStateManager.popMatrix()
        }

        override fun postDraw(mouse: Vec2d, time: Double) {
            val mouseRelative = mouse - drawPos

            if (mouseRelative in RESULT_OFFSET to (RESULT_OFFSET + STACK_SIZE)) {
                parent.gui.renderToolTip(result, mouse)
                return
            }

            for (row in (0..2)) {
                for (column in (0..2)) {
                    val offset = STACK_OFFSET[row][column]

                    if (mouseRelative in offset to (offset + STACK_SIZE) && slots[row][column].isNotEmpty()) {
                        val displayIndex = ((time / DISPLAY_TIME) % slots[row][column].size).toInt()

                        parent.gui.renderToolTip(slots[row][column][displayIndex], mouse)
                    }
                }
            }
        }
    }
}