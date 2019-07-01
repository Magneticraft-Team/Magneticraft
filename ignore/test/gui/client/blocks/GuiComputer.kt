package gui.client.blocks

import com.cout970.magneticraft.gui.client.GuiBase
import com.cout970.magneticraft.gui.client.components.*
import com.cout970.magneticraft.gui.common.blocks.ContainerMonitor
import com.cout970.magneticraft.tileentity.computer.TileComputer
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 2016/09/30.
 */
class GuiComputer(val comp: TileComputer, val contMonitor: ContainerMonitor) : GuiBase(contMonitor) {

    init {
        xSize = 350
        ySize = 255
    }

    override fun initComponents() {
        val listener = object : IButtonListener {
            override fun onPress(button: AbstractButton, mouse: Vec2d, mouseButton: Int): Boolean {
                contMonitor.onPress(button.ID)
                return true
            }
        }
        components.add(CompBackground("old_monitor", textureSize = Vec2d(512, 512), size = Vec2d(350, 255)))
        components.add(MonitorComponent(comp.monitor))
        components.add(SimpleButton(0, Vec2d(23, 220), listener, Vec2d(0, 49)))
        components.add(SimpleButton(1, Vec2d(33, 220), listener, Vec2d(0, 49)))
        components.add(SimpleButton(2, Vec2d(43, 220), listener, Vec2d(0, 49)))
        components.add(CompGreenLight(box.start + Vec2d(14, 221), { comp.motherboard.isOnline() }))
    }
}