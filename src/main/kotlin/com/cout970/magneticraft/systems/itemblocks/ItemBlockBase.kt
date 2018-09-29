package com.cout970.magneticraft.systems.itemblocks

import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.systems.blocks.BlockBase
import com.cout970.magneticraft.systems.blocks.OnBlockPostPlacedArgs
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/07/03.
 */
open class ItemBlockBase(val blockBase: BlockBase) : ItemBlock(blockBase) {

    init {
        registryName = blockBase.registryName
        hasSubtypes = true
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        return blockBase.getItemName(stack)
    }

    override fun getSubItems(itemIn: CreativeTabs, tab: NonNullList<ItemStack>) {
        if (isInCreativeTab(itemIn)) {
            blockBase.inventoryVariants.forEach { meta, _ -> tab.add(ItemStack(this, 1, meta)) }
        }
    }

    override fun getMetadata(damage: Int): Int = damage

    /**
     * Called when a Block is right-clicked with this Item
     */
    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing,
                           hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val itemstack = player.getHeldItem(hand)
        if (itemstack.isEmpty) return EnumActionResult.FAIL

        val otherBlock = worldIn.getBlockState(pos).block
        val basePos = if (!otherBlock.isReplaceable(worldIn, pos)) pos.offset(facing) else pos
        val meta = getMetadata(itemstack.metadata)

        val posStatePair = blockBase.getBlockStatesToPlace(worldIn, basePos, facing,
            hitX, hitY, hitZ, meta, player, hand)
        val posList = posStatePair.map { it.first.add(basePos) }

        val canEdit = posList.all { player.canPlayerEdit(it, facing, itemstack) }
        val mayPlace = posList.all { worldIn.mayPlace(block, it, false, facing, null) }

        if (canEdit && mayPlace) {

            var success = false
            posStatePair.forEach { (pos0, blockstate) ->
                val toPlace = basePos.add(pos0)
                if (placeBlockAt(itemstack, player, worldIn, toPlace, facing, hitX, hitY, hitZ, blockstate)) {
                    val statePlaced = worldIn.getBlockState(toPlace)

                    val soundType = statePlaced.block.getSoundType(statePlaced, worldIn, toPlace, player)
                    worldIn.playSound(player, toPlace, soundType.placeSound,
                        SoundCategory.BLOCKS,
                        (soundType.getVolume() + 1.0f) / 2.0f,
                        soundType.getPitch() * 0.8f
                    )
                    blockBase.onBlockPostPlaced?.invoke(OnBlockPostPlacedArgs(
                        worldIn, pos, facing, vec3Of(hitX, hitY, hitZ), player, hand, pos0
                    ))
                    success = true
                }
            }
            if (success) {
                itemstack.shrink(1)
            }
            return EnumActionResult.SUCCESS
        } else {
            return EnumActionResult.FAIL
        }
    }
}

fun itemBlockListOf(vararg blocks: BlockBase): List<Pair<BlockBase, ItemBlockBase>> {
    return blocks.map { it to ItemBlockBase(it) }
}

fun blockListOf(vararg blocks: BlockBase): List<Pair<BlockBase, ItemBlockBase?>> {
    return blocks.map { it to null }
}