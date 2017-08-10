package com.cout970.magneticraft.item

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.item.core.IItemMaker
import com.cout970.magneticraft.item.core.ItemBase
import com.cout970.magneticraft.item.core.ItemBuilder
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.world.isClient
import net.minecraft.item.Item
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult

/**
 * Created by cout970 on 2017/07/22.
 */
object CraftingItems : IItemMaker {

    lateinit var guideBook: ItemBase private set
    lateinit var crafting: ItemBase private set

    val meta = mapOf(
            "sulfur" to 0,
            "alternator" to 1,
            "motor" to 2,
            "coil" to 3,
            "magnet" to 4,
            "mesh" to 5,
            "string_fabric" to 6
    )

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
        }

        crafting = builder.withName("crafting").copy {
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
}