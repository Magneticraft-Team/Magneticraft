package com.cout970.magneticraft.features.items

import com.cout970.magneticraft.EntityPlayer
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
import com.cout970.magneticraft.systems.items.*
import com.cout970.magneticraft.tagCompound
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType
import net.minecraft.util.DamageSource
import net.minecraft.util.Hand
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World


/**
 * Created by cout970 on 2017/06/12.
 */
@RegisterItems
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

    fun onUseVoltmeter(args: OnItemUseArgs): ActionResultType {
        if (args.worldIn.isServer && args.player != null) {
            val tile = args.worldIn.getTileEntity(args.pos) ?: return ActionResultType.PASS
            val handler = tile.getOrNull(ELECTRIC_NODE_HANDLER, args.facing) ?: return ActionResultType.PASS

            val msg = handler.nodes
                .filterIsInstance<IElectricNode>()
                .joinToString("\n") {
                    "%.2fV %.2fA %.2fW".format(it.voltage, it.amperage, it.voltage * it.amperage)
                }

            args.player.sendUnlocalizedMessage(msg)
        }
        return ActionResultType.PASS
    }

    fun onUseThermometer(args: OnItemUseArgs): ActionResultType {
        if (args.worldIn.isServer && args.player != null) {
            val tile = args.worldIn.getTileEntity(args.pos) ?: return ActionResultType.PASS
            val handler = tile.getOrNull(HEAT_NODE_HANDLER, args.facing) ?: return ActionResultType.PASS

            val msg = handler.nodes
                .filterIsInstance<IHeatNode>()
                .joinToString("\n") {
                    formatHeat(it.temperature)
                }

            args.player.sendUnlocalizedMessage(msg)
        }
        return ActionResultType.PASS
    }

    fun onUseWrench(args: OnItemUseArgs): ActionResultType {
        if (args.worldIn.isClient) return ActionResultType.PASS

        val state = args.worldIn.getBlockState(args.pos)
        // TODO
//        val entry = state.properties.entries.find { it.value is IRotable } ?: return EnumActionResult.PASS
//
//        @Suppress("UNCHECKED_CAST") val prop = entry.key as IProperty<IRotable<Any>>
//        val facing = state.getValue(prop)
//        val newState = state.withProperty(prop, facing.next())

        return ActionResultType.PASS
    }

    private fun createHitEntity(damage: Float): (HitEntityArgs) -> Boolean {
        return {
            it.stack.damageItem(2, it.attacker) {}
            it.target.attackEntityFrom(DamageSource.GENERIC, damage)
        }
    }

    class CopperCoil(properties: Properties) : ItemBase(properties) {

        companion object {
            const val POSITION_KEY = "Position"
        }

        override fun getDisplayName(stack: ItemStack): ITextComponent {
            val name = super.getDisplayName(stack)
            if (stack.hasKey(POSITION_KEY)) {
                val basePos = stack.getBlockPos(POSITION_KEY)
                return name.appendText(" [${TextFormatting.AQUA}${basePos.x}, ${basePos.y}, ${basePos.z}${TextFormatting.WHITE}]")
            }
            return name
        }

        override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer,
                                      handIn: Hand): ActionResult<ItemStack> {
            if (playerIn.isSneaking && playerIn.getHeldItem(handIn).isNotEmpty) {
                val item = playerIn.getHeldItem(handIn)
                item.checkNBT()
                item.tagCompound?.remove(POSITION_KEY)
                return ActionResult(ActionResultType.SUCCESS, item)
            }
            return super.onItemRightClick(worldIn, playerIn, handIn)
        }

        override fun onItemUse(context: ItemUseContext): ActionResultType = context.run {
            val player = player ?: return ActionResultType.PASS
            val stack = player.getHeldItem(hand)
            if (stack.isEmpty) return ActionResultType.PASS

            val block = world.getBlockState(pos)
            val handler = MANUAL_CONNECTION_HANDLER!!.fromBlock(block.block)
            if (handler != null) {
                if (player.isSneaking) {
                    val basePos = handler.getBasePos(pos, world, player, face, stack)
                    if (basePos != null) {
                        stack.setBlockPos(POSITION_KEY, basePos)
                        player.sendMessage("text.magneticraft.wire_connect.updated_position",
                            "[${TextFormatting.AQUA}" +
                                "${basePos.x}, ${basePos.y}, ${basePos.z}${TextFormatting.WHITE}]")
                        return ActionResultType.SUCCESS
                    }
                } else {
                    if (stack.hasKey(POSITION_KEY)) {
                        val basePos = stack.getBlockPos(POSITION_KEY)
                        val result = handler.connectWire(basePos, pos, world, player, face, stack)

                        if (world.isServer) {
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

                        return ActionResultType.SUCCESS
                    } else {
                        if (world.isServer) {
                            player.sendMessage("text.magneticraft.wire_connect.no_other_connector")
                        }
                    }
                }
            }
            ActionResultType.PASS
        }
    }
}