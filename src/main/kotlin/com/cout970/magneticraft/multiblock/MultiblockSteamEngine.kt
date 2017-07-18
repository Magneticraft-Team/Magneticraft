package com.cout970.magneticraft.multiblock

import com.cout970.magneticraft.block.MultiblockParts
import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.multiblock.components.IgnoreBlockComponent
import com.cout970.magneticraft.multiblock.components.MainBlockComponent
import com.cout970.magneticraft.multiblock.components.SingleBlockComponent
import com.cout970.magneticraft.multiblock.core.BlockData
import com.cout970.magneticraft.multiblock.core.IMultiblockComponent
import com.cout970.magneticraft.multiblock.core.Multiblock
import com.cout970.magneticraft.multiblock.core.MultiblockContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent

/**
 * Created by cout970 on 2017/07/17.
 */
object MultiblockSteamEngine : Multiblock() {

    override val name: String = "steam_engine"
    override val size: BlockPos = BlockPos(3, 4, 4)
    override val scheme: List<MultiblockLayer>
    override val center: BlockPos = BlockPos(0, 1, 0)

    init {
        val replacement = Multiblocks.gap.defaultState

        val I = IgnoreBlockComponent

        val pBlock = MultiblockParts.PartType.ELECTRIC.getBlockState(MultiblockParts.parts)
        val P: IMultiblockComponent = SingleBlockComponent(pBlock, replacement)

        val M: IMultiblockComponent = MainBlockComponent(Multiblocks.steamEngine) { context, activate ->
            Multiblocks.MultiblockOrientation.of(context.facing, activate).getBlockState(Multiblocks.steamEngine)
        }

        scheme = yLayers(
                zLayers(listOf(P, I, I), // y = 0
                        listOf(P, I, I),
                        listOf(P, I, I),
                        listOf(I, I, I)),

                zLayers(listOf(P, I, I), // y = 0
                        listOf(P, I, I),
                        listOf(P, P, I),
                        listOf(I, P, I)),

                zLayers(listOf(M, I, I), // y = 0
                        listOf(P, I, I),
                        listOf(P, P, P),
                        listOf(P, P, P)),

                zLayers(listOf(I, I, I), // y = -1
                        listOf(I, I, I),
                        listOf(I, P, I),
                        listOf(I, P, I))
        )
    }

    override fun checkExtraRequirements(data: MutableList<BlockData>,
                                        context: MultiblockContext): List<ITextComponent> = listOf()
}