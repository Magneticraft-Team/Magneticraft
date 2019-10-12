package com.cout970.magneticraft.features.items

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IManualConnectionHandler.Result.*
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.misc.gui.formatHeat
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.player.sendMessage
import com.cout970.magneticraft.misc.player.sendUnlocalizedMessage
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.*
import com.cout970.magneticraft.systems.blocks.IRotable
import com.cout970.magneticraft.systems.items.*
import net.minecraft.block.properties.IProperty
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World


/**
 * Created by cout970 on 2017/06/12.
 */
object ToolItems : IItemMaker {

    lateinit var stoneHammer: ItemBase private set
    lateinit var ironHammer: ItemBase private set
    lateinit var steelHammer: ItemBase private set
    lateinit var copperCoil: ItemBase private set
    lateinit var voltmeter: ItemBase private set
    lateinit var thermometer: ItemBase private set
    lateinit var wrench: ItemBase private set

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
            isFull3d = true
            maxStackSize = 1
        }

        stoneHammer = builder.withName("stone_hammer").copy {
            onHitEntity = createHitEntity(2.0f)
            maxDamage = 130
        }.build()

        ironHammer = builder.withName("iron_hammer").copy {
            onHitEntity = createHitEntity(3.5f)
            maxDamage = 250
        }.build()

        steelHammer = builder.withName("steel_hammer").copy {
            onHitEntity = createHitEntity(5.0f)
            maxDamage = 750
        }.build()

        copperCoil = builder.withName("copper_coil").copy {
            constructor = ToolItems::CopperCoil
            isFull3d = false
            maxStackSize = 16
        }.build()

        voltmeter = builder.withName("voltmeter").copy {
            onItemUse = ToolItems::onUseVoltmeter
        }.build()

        thermometer = builder.withName("thermometer").copy {
            onItemUse = ToolItems::onUseThermometer
        }.build()

        wrench = builder.withName("wrench").copy {
            onItemUse = ToolItems::onUseWrench
        }.build()

        return listOf(stoneHammer, ironHammer, steelHammer, copperCoil, voltmeter, thermometer, wrench)
    }

    fun onUseVoltmeter(args: OnItemUseArgs): EnumActionResult {

        if (args.worldIn.isServer) {
            val tile = args.worldIn.getTileEntity(args.pos) ?: return EnumActionResult.PASS
            val handler = tile.getOrNull(ELECTRIC_NODE_HANDLER, args.facing) ?: return EnumActionResult.PASS

            val msg = handler.nodes
                    .filterIsInstance<IElectricNode>()
                    .joinToString("\n") {
                        "%.2fV %.2fA %.2fW".format(it.voltage, it.amperage, it.voltage * it.amperage)
                    }

            args.player.sendUnlocalizedMessage(msg)
        }
        return EnumActionResult.PASS
    }

    fun onUseThermometer(args: OnItemUseArgs): EnumActionResult {
        if (args.worldIn.isServer) {
            val tile = args.worldIn.getTileEntity(args.pos) ?: return EnumActionResult.PASS
            val handler = tile.getOrNull(HEAT_NODE_HANDLER, args.facing) ?: return EnumActionResult.PASS

            val msg = handler.nodes
                    .filterIsInstance<IHeatNode>()
                    .joinToString("\n") {
                        formatHeat(it.temperature)
                    }

            args.player.sendUnlocalizedMessage(msg)
        }
        return EnumActionResult.PASS
    }

    fun onUseWrench(args: OnItemUseArgs): EnumActionResult {
        if (args.worldIn.isClient) return EnumActionResult.PASS

        val state = args.worldIn.getBlockState(args.pos)
        val entry = state.properties.entries.find { it.value is IRotable } ?: return EnumActionResult.PASS

        @Suppress("UNCHECKED_CAST") val prop = entry.key as IProperty<IRotable<Any>>
        val facing = state.getValue(prop)
        val newState = state.withProperty(prop, facing.next())

        if (state != newState) {
            args.worldIn.setBlockState(args.pos, newState)
        }

        return EnumActionResult.PASS
    }

    private fun createHitEntity(damage: Float): (HitEntityArgs) -> Boolean {
        return {
            it.stack.damageItem(2, it.attacker)
            it.target.attackEntityFrom(DamageSource.GENERIC, damage)
        }
    }

    class CopperCoil : ItemBase() {

        companion object {
            const val POSITION_KEY = "Position"
        }

        override fun getItemStackDisplayName(stack: ItemStack): String {
            val name = super.getItemStackDisplayName(stack)
            if (stack.hasKey(POSITION_KEY)) {
                val basePos = stack.getBlockPos(POSITION_KEY)
                return name + " [${TextFormatting.AQUA}${basePos.x}, ${basePos.y}, ${basePos.z}${TextFormatting.WHITE}]"
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
                               facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
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
                                "[${TextFormatting.AQUA}" +
                                        "${basePos.x}, ${basePos.y}, ${basePos.z}${TextFormatting.WHITE}]")
                        return EnumActionResult.SUCCESS
                    }
                } else {
                    if (stack.hasKey(POSITION_KEY)) {
                        val basePos = stack.getBlockPos(POSITION_KEY)
                        val result = handler.connectWire(basePos, pos, worldIn, player, facing, stack)

                        if (worldIn.isServer) {
                            when (result) {
                                SUCCESS -> player.sendMessage("text.magneticraft.wire_connect.success")
                                TOO_FAR -> player.sendMessage("text.magneticraft.wire_connect.too_far")
                                NOT_A_CONNECTOR -> player.sendMessage("text.magneticraft.wire_connect.not_a_connector")
                                INVALID_CONNECTOR -> player.sendMessage("text.magneticraft.wire_connect.invalid_connector")
                                SAME_CONNECTOR -> player.sendMessage("text.magneticraft.wire_connect.same_connector")
                                ALREADY_CONNECTED -> player.sendMessage("text.magneticraft.wire_connect.already_connected")
                                ERROR, null -> player.sendMessage("text.magneticraft.wire_connect.fail")
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