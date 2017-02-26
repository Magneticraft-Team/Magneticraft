@file:Suppress("LeakingThis")

package com.cout970.magneticraft.block


import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.TileBase
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

/**
 * This is the base class for all the blocks in the mod
 */
abstract class BlockBase(
        material: Material,
        registryName: String,
        unlocalizedName: String = registryName
) : Block(material) {

    open val inventoryVariants = mapOf(
            0 to "inventory"
    )

    init {
        this.unlocalizedName = "$MOD_ID.$unlocalizedName"
        this.registryName = resource(registryName)
        setHardness(1.5F)
        setResistance(10.0F)
        setCreativeTab(CreativeTabMg)
    }

    open fun getItemName(stack: ItemStack?): String? = unlocalizedName

    // Called in server and client
    override fun removedByPlayer(state: IBlockState?, world: World?, pos: BlockPos?, player: EntityPlayer?, willHarvest: Boolean): Boolean {
        if (world!!.isClient) {
            val tile = world.getTile<TileBase>(pos!!)
            tile?.onBreak()
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest)
    }

    // Only called in the server
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