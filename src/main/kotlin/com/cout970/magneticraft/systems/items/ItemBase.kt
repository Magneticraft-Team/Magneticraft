package com.cout970.magneticraft.systems.items

import com.cout970.magneticraft.*
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import net.minecraft.block.BlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.World
import net.minecraftforge.common.ToolType
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 2017/06/11.
 */
open class ItemBase(properties: Properties) : Item(properties) {

    var onHitEntity: ((HitEntityArgs) -> Boolean)? = null
    var onItemUse: ((OnItemUseArgs) -> ActionResultType)? = null
    var onItemRightClick: ((OnItemRightClickArgs) -> ActionResult<ItemStack>)? = null
    var itemInteractionForEntity: ((ItemInteractionForEntityArgs) -> Boolean)? = null
    var capabilityProvider: ((InitCapabilitiesArgs) -> ICapabilityProvider?)? = null
    var addInformation: ((AddInformationArgs) -> Unit)? = null
    var getDestroySpeed: ((GetDestroySpeedArgs) -> Float)? = null
    var getHarvestLevel: ((GetHarvestLevelArgs) -> Int)? = null
    var getToolClasses: ((ItemStack) -> MutableSet<ToolType>)? = null
    var canHarvestBlock: ((IBlockState) -> Boolean)? = null
    var onBlockDestroyed: ((OnBlockDestroyedArgs) -> Boolean)? = null
    var postCreate: ((ItemStack) -> Unit)? = null
    var burnTime = -1

    var customModels: List<Pair<String, ResourceLocation>> = emptyList()

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        capabilityProvider?.let { return it(InitCapabilitiesArgs(stack, nbt)) }
        return super.initCapabilities(stack, nbt)
    }

    override fun hitEntity(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        onHitEntity?.let { return it(HitEntityArgs(stack, target, attacker)) }
        return super.hitEntity(stack, target, attacker)
    }

    override fun onItemUse(context: ItemUseContext): ActionResultType {
        onItemUse?.let {
            return it(OnItemUseArgs(this,
                context.player, context.world, context.pos,
                context.hand, context.face, context.hitVec))
        }
        return super.onItemUse(context)
    }

    override fun itemInteractionForEntity(stack: ItemStack, playerIn: EntityPlayer, target: LivingEntity, hand: Hand): Boolean {
        itemInteractionForEntity?.let { return it(ItemInteractionForEntityArgs(stack, playerIn, target, hand)) }
        return super.itemInteractionForEntity(stack, playerIn, target, hand)
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: Hand): ActionResult<ItemStack> {
        val default = super.onItemRightClick(worldIn, playerIn, handIn)
        onItemRightClick?.let { return it(OnItemRightClickArgs(worldIn, playerIn, handIn, default)) }
        return default
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        addInformation?.invoke(AddInformationArgs(stack, worldIn, tooltip, flagIn))
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }

    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        getDestroySpeed?.let { return it(GetDestroySpeedArgs(stack, state)) }
        return super.getDestroySpeed(stack, state)
    }

    override fun hasContainerItem(stack: ItemStack): Boolean {
        @Suppress("DEPRECATION")
        if (stack.isNotEmpty && containerItem == null && maxDamage > 0) {
            return true
        }
        return super.hasContainerItem(stack)
    }

    @Suppress("DEPRECATION")
    override fun getContainerItem(itemStack: ItemStack): ItemStack {
        var newStack = super.getContainerItem(itemStack)

        if (newStack.isEmpty && maxDamage > 0) {
            newStack = itemStack.copy()
            newStack.damage++

            if (newStack.damage > maxDamage) {
                newStack.shrink(1)
            }
        }
        return newStack
    }

    override fun getHarvestLevel(stack: ItemStack, tool: ToolType, player: PlayerEntity?, blockState: BlockState?): Int {
        getHarvestLevel?.let { return it(GetHarvestLevelArgs(stack, tool, player, blockState)) }
        return super.getHarvestLevel(stack, tool, player, blockState)
    }

    override fun getToolTypes(stack: ItemStack): MutableSet<ToolType> {
        getToolClasses?.let { return it(stack) }
        return super.getToolTypes(stack)
    }

    override fun canHarvestBlock(blockIn: IBlockState): Boolean {
        canHarvestBlock?.let { return it(blockIn) }
        return false
    }

    override fun onBlockDestroyed(stack: ItemStack, worldIn: World, state: IBlockState, pos: BlockPos, entityLiving: LivingEntity): Boolean {
        onBlockDestroyed?.let { return it(OnBlockDestroyedArgs(stack, worldIn, state, pos, entityLiving)) }
        return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving)
    }

    override fun fillItemGroup(group: ItemGroup, items: NonNullList<ItemStack>) {
        if (this.isInGroup(group)) {
            val stack = ItemStack(this)
            postCreate?.let{ it(stack) }
            items.add(stack)
        }
    }

    override fun getBurnTime(itemStack: ItemStack?): Int {
        return burnTime
    }

    override fun toString(): String = "ItemBase($registryName)"
}

data class HitEntityArgs(val stack: ItemStack, val target: LivingEntity, val attacker: LivingEntity)
data class InitCapabilitiesArgs(val stack: ItemStack, val nbt: NBTTagCompound?)
data class OnItemUseArgs(val item: ItemBase, val player: EntityPlayer?, val worldIn: World, val pos: BlockPos,
                         val hand: Hand,
                         val facing: EnumFacing, val hit: IVector3)

data class OnItemRightClickArgs(val worldIn: World, val playerIn: EntityPlayer, val handIn: Hand,
                                val default: ActionResult<ItemStack>)

data class AddInformationArgs(val stack: ItemStack, val worldIn: World?, val tooltip: MutableList<ITextComponent>,
                              val flagIn: ITooltipFlag)

data class GetDestroySpeedArgs(val stack: ItemStack, val state: IBlockState)

data class GetHarvestLevelArgs(val stack: ItemStack, val toolClass: ToolType,
                               val player: EntityPlayer?, val blockState: IBlockState?)

data class OnBlockDestroyedArgs(val stack: ItemStack, val worldIn: World, val state: IBlockState,
                                val pos: BlockPos, val entityLiving: LivingEntity)

data class ItemInteractionForEntityArgs(val stack: ItemStack, val player: EntityPlayer,
                                        val target: LivingEntity, val hand: Hand)