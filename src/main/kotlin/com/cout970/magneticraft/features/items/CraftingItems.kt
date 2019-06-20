package com.cout970.magneticraft.features.items

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.items.IItemMaker
import com.cout970.magneticraft.systems.items.ItemBase
import com.cout970.magneticraft.systems.items.ItemBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult

/**
 * Created by cout970 on 2017/07/22.
 */
object CraftingItems : IItemMaker {

    lateinit var guideBook: ItemBase private set
    lateinit var crafting: ItemBase private set

    enum class Type(val meta: Int) {
        SULFUR(0),
        ALTERNATOR(1),
        MOTOR(2),
        COIL(3),
        MAGNET(4),
        MESH(5),
        STRING_FABRIC(6);

        fun stack(amount: Int = 1): ItemStack = ItemStack(crafting, amount, meta)
    }

    val meta = Type.values().map { it.name.toLowerCase() to it.meta }.toMap()

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
        }

        crafting = builder.withName("crafting").copy {
            constructor = { CraftingItem() }
            variants = meta.map { it.value to it.key }.toMap()
        }.build()

        guideBook = builder.withName("guide_book").copy {
            onItemRightClick = {
                if (it.playerIn.isSneaking || it.worldIn.isClient) {
                    it.default
                } else {
                    val pos = it.playerIn.position
                    it.playerIn.openGui(Magneticraft, -2, it.worldIn, pos.x, pos.y, pos.z)
                    ActionResult(EnumActionResult.SUCCESS, it.playerIn.getHeldItem(it.handIn))
                }
            }
        }.build()

        return listOf(crafting, guideBook)
    }

    class CraftingItem : ItemBase() {

        override fun getItemBurnTime(itemStack: ItemStack): Int {
            if (itemStack.metadata == Type.SULFUR.meta) return 800
            return -1
        }
    }
}