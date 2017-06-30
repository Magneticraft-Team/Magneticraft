package com.cout970.magneticraft.item

import com.cout970.magneticraft.api.tool.IHammer
import com.cout970.magneticraft.item.core.HitEntityArgs
import com.cout970.magneticraft.item.core.IItemMaker
import com.cout970.magneticraft.item.core.ItemBase
import com.cout970.magneticraft.item.core.ItemBuilder
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.player.sendMessage
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_HAMMER
import com.cout970.magneticraft.registry.MANUAL_CONNECTION_HANDLER
import com.cout970.magneticraft.registry.fromBlock
import com.cout970.magneticraft.util.checkNBT
import com.cout970.magneticraft.util.getBlockPos
import com.cout970.magneticraft.util.hasKey
import com.cout970.magneticraft.util.setBlockPos
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 2017/06/12.
 */
object Tools : IItemMaker {

    lateinit var stoneHammer: ItemBase private set
    lateinit var ironHammer: ItemBase private set
    lateinit var steelHammer: ItemBase private set
    lateinit var copperCoil: ItemBase private set

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
            isFull3d = true
            maxStackSize = 1
        }

        stoneHammer = builder.withName("stone_hammer").copy {
            onHitEntity = createHitEntity(2.0f)
            capabilityProvider = { Hammer(1, 8) }
            maxDamage = 130
        }.build()

        ironHammer = builder.withName("iron_hammer").copy {
            onHitEntity = createHitEntity(3.5f)
            capabilityProvider = { Hammer(2, 10) }
            maxDamage = 250
        }.build()

        steelHammer = builder.withName("steel_hammer").copy {
            onHitEntity = createHitEntity(5.0f)
            capabilityProvider = { Hammer(3, 15) }
            maxDamage = 750
        }.build()

        copperCoil = builder.withName("copper_coil").copy {
            constructor = ::CopperCoil
            isFull3d = false
        }.build()

        return listOf(stoneHammer, ironHammer, steelHammer, copperCoil)
    }

    private fun createHitEntity(damage: Float): (HitEntityArgs) -> Boolean {
        return {
            it.stack.damageItem(2, it.attacker)
            it.target.attackEntityFrom(DamageSource.GENERIC, damage)
        }
    }

    class Hammer(val level: Int, val damage: Int) : IHammer, ICapabilityProvider {

        //(3 = DIAMOND, 2 = IRON, 1 = STONE, 0 = WOOD/GOLD)
        override fun getMiningLevel(): Int = level

        override fun getBreakingSpeed(): Int = damage

        override fun applyDamage(item: ItemStack, player: EntityPlayer): ItemStack {
            item.damageItem(1, player)
            return item
        }

        override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
            @Suppress("UNCHECKED_CAST")
            return this as T
        }

        override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean = capability == ITEM_HAMMER
    }

    class CopperCoil : ItemBase() {

        val POSITION_KEY = "Position"

        override fun getItemStackDisplayName(stack: ItemStack?): String {
            val name = super.getItemStackDisplayName(stack)
            if (stack!!.hasKey(POSITION_KEY)) {
                val basePos = stack.getBlockPos(POSITION_KEY)
                return name + " [${TextFormatting.AQUA}Position: ${basePos.x}, ${basePos.y}, ${basePos.z}${TextFormatting.WHITE}]"
            }
            return name
        }

        override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer,
                                      handIn: EnumHand): ActionResult<ItemStack> {
            if (playerIn.isSneaking && playerIn.getHeldItem(handIn).isNotEmpty) {
                val item = playerIn.getHeldItem(handIn)
                item.checkNBT()
                item.tagCompound?.removeTag(POSITION_KEY)
                return ActionResult(EnumActionResult.SUCCESS, item)
            }
            return super.onItemRightClick(worldIn, playerIn, handIn)
        }

        override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand,
                               facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
            val stack = player.getHeldItem(hand)
            if (stack.isEmpty) return EnumActionResult.PASS

            val block = worldIn.getBlockState(pos)
            val handler = MANUAL_CONNECTION_HANDLER!!.fromBlock(block.block)
            if (handler != null) {
                if (player.isSneaking) {
                    val basePos = handler.getBasePos(pos, worldIn, player, facing, stack)
                    if (basePos != null) {
                        stack.setBlockPos(POSITION_KEY, basePos)
                        player.sendMessage("text.magneticraft.wire_connect.updated_position",
                                "[${TextFormatting.AQUA}Position: ${basePos.x}, ${basePos.y}, ${basePos.z}${TextFormatting.WHITE}]")
                        return EnumActionResult.SUCCESS
                    }
                } else {
                    if (stack.hasKey(POSITION_KEY)) {
                        val basePos = stack.getBlockPos(POSITION_KEY)
                        if (handler.connectWire(basePos, pos, worldIn, player, facing, stack)) {
                            if (worldIn.isServer) {
                                player.sendMessage("text.magneticraft.wire_connect.success")
                            }
                        } else {
                            if (worldIn.isServer) {
                                player.sendMessage("text.magneticraft.wire_connect.fail")
                            }
                        }
                        return EnumActionResult.SUCCESS
                    } else {
                        if (worldIn.isServer) {
                            player.sendMessage("text.magneticraft.wire_connect.no_other_connector")
                        }
                    }
                }
            }
            return EnumActionResult.PASS
        }
    }
}