package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.gui.client.components.CompBookRenderer
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.core.ContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/08/02.
 */

class ContainerGuideBook(player: EntityPlayer, world: World, pos: BlockPos) : ContainerBase(player, world, pos)

class GuiGuideBook(container: ContainerBase) : GuiBase(container) {

    override fun initComponents() {
        xSize = 280
        ySize = 186
        components.add(CompBookRenderer())
    }
}