package com.cout970.magneticraft.features.items

import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.RegisterItems
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.items.IItemMaker
import com.cout970.magneticraft.systems.items.ItemBase
import com.cout970.magneticraft.systems.items.ItemBuilder
import net.minecraft.item.Item
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType

/**
 * Created by cout970 on 2017/07/22.
 */
@RegisterItems
object CraftingItems : IItemMaker {

    lateinit var guideBook: ItemBase private set
    lateinit var sulfur: ItemBase private set
    lateinit var alternator: ItemBase private set
    lateinit var motor: ItemBase private set
    lateinit var coil: ItemBase private set
    lateinit var magnet: ItemBase private set
    lateinit var mesh: ItemBase private set
    lateinit var string_fabric: ItemBase private set


    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
        }

        sulfur = builder.withName("sulfur").copy { burnTime = 800 }.build()
        alternator = builder.withName("alternator").build()
        motor = builder.withName("motor").build()
        coil = builder.withName("coil").build()
        magnet = builder.withName("magnet").build()
        mesh = builder.withName("mesh").build()
        string_fabric = builder.withName("string_fabric").build()

        guideBook = builder.withName("guide_book").copy {
            onItemRightClick = {
                if (it.playerIn.isSneaking || it.worldIn.isClient) {
                    it.default
                } else {
                    val pos = it.playerIn.position
                    // TODO
//                    it.playerIn.openGui(Magneticraft, -2, it.worldIn, pos.x, pos.y, pos.z)
                    ActionResult(ActionResultType.SUCCESS, it.playerIn.getHeldItem(it.handIn))
                }
            }
        }.build()

        return listOf(magnet, alternator, motor, coil, magnet, mesh, string_fabric, guideBook)
    }

}