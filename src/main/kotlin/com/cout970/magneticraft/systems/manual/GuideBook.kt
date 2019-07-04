package com.cout970.magneticraft.systems.manual

import com.cout970.magneticraft.systems.gui.ContainerBase
import com.cout970.magneticraft.systems.gui.GuiBase
import com.cout970.magneticraft.systems.gui.components.CompBookRenderer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/08/02.
 */

class ContainerGuideBook(player: EntityPlayer, world: World, pos: BlockPos) : ContainerBase(player, world, pos)

class GuiGuideBook(cont: ContainerBase) : GuiBase(cont) {

    override fun initComponents() {
        xSize = (280 * CompBookRenderer.scale).toInt()
        ySize = (186 * CompBookRenderer.scale).toInt()
        components.add(CompBookRenderer())
    }
}