package com.cout970.magneticraft.systems.manual

import com.cout970.magneticraft.EntityPlayer
import com.cout970.magneticraft.systems.gui.ContainerBase
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/08/02.
 */

class ContainerGuideBook(
    player: EntityPlayer,
    world: World,
    pos: BlockPos,
    id: Int
) : ContainerBase(player, world, pos, id)


// TODO
//class GuiGuideBook(cont: ContainerBase) : GuiBase(cont), IHasContainer<ContainerBase> {
//
//    override fun initComponents() {
//        container
//        xSize = (280 * CompBookRenderer.scale).toInt()
//        ySize = (186 * CompBookRenderer.scale).toInt()
//        components.add(CompBookRenderer())
//    }
//}