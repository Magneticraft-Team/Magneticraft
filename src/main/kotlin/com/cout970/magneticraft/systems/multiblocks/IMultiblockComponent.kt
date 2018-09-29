package com.cout970.magneticraft.systems.multiblocks

import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.systems.tileentities.TileBase
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 19/08/2016.
 */
interface IMultiblockComponent {

    //if the block is not correct, return a list of errors to show to the player
    fun checkBlock(relativePos: BlockPos, context: MultiblockContext): List<ITextComponent>

    //returns the current ste of the block, used for extra requirements in the multiblock
    fun getBlockData(relativePos: BlockPos, context: MultiblockContext): BlockData

    //called when the multiblock is activated
    fun activateBlock(relativePos: BlockPos, context: MultiblockContext) {
        val pos = context.center + relativePos
        val tile = context.world.getTile<TileBase>(pos) ?: return
        val module = tile.container.modules.find { it is IMultiblockModule } as? IMultiblockModule
            ?: return

        module.multiblock = context.multiblock
        module.multiblockFacing = context.facing
        module.centerPos = relativePos
        module.onActivate()
    }

    //called when the multiblock is destroyed
    fun deactivateBlock(relativePos: BlockPos, context: MultiblockContext) {
        val pos = context.center + relativePos
        val tile = context.world.getTile<TileBase>(pos)
        val module = tile?.container?.modules?.find { it is IMultiblockModule } as? IMultiblockModule
        if (module != null) {
            module.onDeactivate()
            module.multiblock = null
            module.multiblockFacing = null
            module.centerPos = null
        }
    }

    //return the items to render in the blueprint of this block
    fun getBlueprintBlocks(multiblock: Multiblock, blockPos: BlockPos): List<ItemStack>
}