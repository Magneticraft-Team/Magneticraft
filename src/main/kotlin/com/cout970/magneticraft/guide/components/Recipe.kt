package com.cout970.magneticraft.guide.components

import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.Page
import com.cout970.magneticraft.guide.builders.GUIDE_FOLDER
import com.cout970.magneticraft.util.shuffled
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

const val DISPLAY_TIME = 20

val GRID_TEXTURE = ResourceLocation("$GUIDE_FOLDER/grid.png")
val GRID_SIZE = Coords(88, 56)
val STACK_OFFSET = Array(3) { row ->
    Array(3) { column ->
        Coords(2 + 18 * column, 2 + 18 * row)
    }
}
val STACK_SIZE = Coords(16, 16)
val RESULT_OFFSET = Coords(68, 20)

class Recipe(
    position: Coords,
    val recipe: Array<out Array<out List<ItemStack>>>,
    val result: ItemStack
) : PageComponent(position) {
    override val size = Coords(70, 44)


    override fun toGuiComponent(parent: Page.Gui): GuiPageComponent = Gui(parent)

    private inner class Gui(parent: Page.Gui) : PageComponent.Gui(parent) {
        val slots = recipe.map { it.map { it.shuffled() } }

        override fun draw(mouse: Coords, time: Double) {
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

        override fun postDraw(mouse: Coords, time: Double) {
            val mouseRelative = mouse - drawPos

            if (mouseRelative.inside(RESULT_OFFSET, RESULT_OFFSET + STACK_SIZE)) {
                parent.gui.renderToolTip(result, mouse)
                return
            }

            for (row in (0..2)) {
                for (column in (0..2)) {
                    val offset = STACK_OFFSET[row][column]

                    if (mouseRelative.inside(offset, offset + STACK_SIZE) && slots[row][column].isNotEmpty()) {
                        val displayIndex = ((time / DISPLAY_TIME) % slots[row][column].size).toInt()

                        parent.gui.renderToolTip(slots[row][column][displayIndex], mouse)
                    }
                }
            }
        }
    }
}