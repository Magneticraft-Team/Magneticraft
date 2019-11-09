package com.cout970.magneticraft.systems.itemblocks

import com.cout970.magneticraft.systems.blocks.BlockBase
import com.cout970.magneticraft.systems.blocks.OnBlockPostPlacedArgs
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemUseContext
import net.minecraft.util.ActionResultType
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockRayTraceResult



/**
 * Created by cout970 on 2017/07/03.
 */
open class ItemBlockBase(val blockBase: BlockBase) : BlockItem(blockBase, Properties().group(blockBase.creativeTab)) {

    init {
        registryName = blockBase.registryName
    }

    /**
     * Called when a Block is right-clicked with this Item to place the block/blocks
     */
    override fun tryPlace(ctx: BlockItemUseContext): ActionResultType {
        val player = ctx.player ?: return ActionResultType.PASS
        val handStack = player.getHeldItem(ctx.hand)
        if (handStack.isEmpty) return ActionResultType.FAIL

        val basePos = ctx.pos

        val posStatePair = blockBase.getBlockStatesToPlace(
            ctx.world, ctx.pos, ctx.face,
            ctx.hitVec.x.toFloat(), ctx.hitVec.y.toFloat(), ctx.hitVec.z.toFloat(),
            player, ctx.hand
        )
        val posList = posStatePair.map { it.first.add(basePos) }

        val canEdit = posList.all { player.canPlayerEdit(it, ctx.face, handStack) }
        val mayPlace = posList.all {
            val ray = BlockRayTraceResult(ctx.hitVec, ctx.face, it, ctx.func_221533_k())
            val newCtx = BlockItemUseContext(ItemUseContext(player, ctx.hand, ray))
            newCtx.canPlace()
        }

        if (mayPlace && canEdit) {
            var success = false
            posStatePair.forEach { (pos0, toPlaceState) ->
                val toPlacePos = basePos.add(pos0)

                if (ctx.world.setBlockState(toPlacePos, toPlaceState, 11)) {
                    val statePlaced = ctx.world.getBlockState(toPlacePos)

                    if (statePlaced.block === toPlaceState.block) {
//                        statePlaced = this.func_219985_a(blockpos, world, itemstack, blockstate1)
                        onBlockPlaced(toPlacePos, ctx.world, player, handStack, statePlaced)
                        statePlaced.block.onBlockPlacedBy(ctx.world, toPlacePos, statePlaced, player, handStack)
                        if (player is ServerPlayerEntity) {
                            CriteriaTriggers.PLACED_BLOCK.trigger(player as ServerPlayerEntity, toPlacePos, handStack)
                        }
                    }

                    val soundType = statePlaced.getSoundType(ctx.world, toPlacePos, player)
                    val placeSound = getPlaceSound(statePlaced, ctx.world, toPlacePos, player)

                    ctx.world.playSound(player, toPlacePos, placeSound,
                        SoundCategory.BLOCKS,
                        (soundType.getVolume() + 1.0f) / 2.0f,
                        soundType.getPitch() * 0.8f
                    )

                    blockBase.onBlockPostPlaced?.invoke(OnBlockPostPlacedArgs(
                        ctx.world, ctx.pos, ctx.face, ctx.hitVec, player, ctx.hand, pos0
                    ))
                    success = true
                }
            }
            if (success) {
                handStack.shrink(1)
            }
            return ActionResultType.SUCCESS
        }
        return ActionResultType.FAIL
    }
}

fun itemBlockListOf(vararg blocks: BlockBase): List<Pair<BlockBase, ItemBlockBase>> {
    return blocks.map { it to ItemBlockBase(it) }
}

fun blockListOf(vararg blocks: BlockBase): List<Pair<BlockBase, ItemBlockBase?>> {
    return blocks.map { it to null }
}