@file:Suppress("LeakingThis")

package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.blocks.creativeTab
import coffee.cypher.mcextlib.extensions.items.stack
import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.MODID
import com.cout970.magneticraft.util.resource
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class BlockBase(
        material: Material,
        registryName: String,
        unlocalizedName: String = registryName
) : Block(material) {

    open val inventoryVariants = mapOf(
            0 to "inventory"
    )

    init {
        this.unlocalizedName = "$MODID.$unlocalizedName"
        this.registryName = resource(registryName)
        setHardness(1.5F)
        setResistance(10.0F)
        creativeTab = CreativeTabMg
    }

    open fun getItemName(stack: ItemStack?): String? = unlocalizedName

    override fun removedByPlayer(state: IBlockState?, world: World?, pos: BlockPos?, player: EntityPlayer?, willHarvest: Boolean): Boolean {
        if (world!!.isRemote) {
            val tile = world.getTile<TileBase>(pos!!)
            tile?.onBreak()
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile = worldIn.getTile<TileBase>(pos)
        tile?.onBreak()
        super.breakBlock(worldIn, pos, state)
    }

    override fun getSubBlocks(itemIn: Item?, tab: CreativeTabs?, list: MutableList<ItemStack>?) {
        if (list == null || itemIn == null) {
            return
        }
        list += inventoryVariants.map { itemIn.stack(meta = it.key) }
    }

    open fun getCustomStateMapper(): IStateMapper? = null
}