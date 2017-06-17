package com.cout970.magneticraft.block.core

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

@Suppress("OverridingDeprecatedMember")
open class BlockBase(material: Material) : Block(material) {

    companion object {
        // Because mojang is stupid and createBlockState is called BEFORE the constructor
        var states_: List<IStatesEnum>? = null
    }

    val states: List<IStatesEnum> = states_!!
    var customModels: List<Pair<String, ResourceLocation>> = emptyList()

    var aabb: ((BoundingBoxArgs) -> AABB)? = null
    var onActivated: ((OnActivatedArgs) -> Boolean)? = null
    var stateMapper: ((IBlockState) -> ModelResourceLocation)? = null
    var onBlockPlaced: ((OnBlockPlacedArgs) -> IBlockState)? = null
    var enableOcclusionOptimization = true
    var translucent_ = false

    // ItemBlock stuff
    val inventoryVariants: Map<Int, String> = run {
        val map = mutableMapOf<Int, String>()
        states.filter { it.isVisible }.forEach { value ->
            map += value.ordinal to value.stateName
        }
        map
    }

    fun getItemName(stack: ItemStack?) = "${unlocalizedName}_${states[stack!!.metadata].stateName}"

    // metadata and block state stuff
    override fun getMetaFromState(state: IBlockState): Int = states.find {
        it.getBlockState(this) == state
    }?.ordinal ?: 0

    override fun getStateFromMeta(meta: Int): IBlockState = states[meta].getBlockState(this)

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *states_!![0].properties.toTypedArray())
    }

    // event stuff
    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AABB {
        return aabb?.invoke(BoundingBoxArgs(state, source, pos)) ?: FULL_BLOCK_AABB
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                  hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float,
                                  hitZ: Float): Boolean {
        val heldItem = playerIn.getHeldItem(hand)

        return onActivated?.invoke(
                OnActivatedArgs(worldIn, pos, state, playerIn, hand, heldItem, side,
                        vec3Of(hitX, hitY, hitZ)))
               ?: super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ)
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    // Called in server and client
    override fun removedByPlayer(state: IBlockState, world: World, pos: BlockPos, player: EntityPlayer?,
                                 willHarvest: Boolean): Boolean {
        if (world.isClient) {
            world.getTile<TileBase>(pos)?.onBreak()
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest)
    }

    // Only called in the server
    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        worldIn.getTile<TileBase>(pos)?.onBreak()
        super.breakBlock(worldIn, pos, state)
    }

    override fun toString(): String {
        return "BlockBase($registryName)"
    }

    fun getCustomStateMapper(): IStateMapper? = object : StateMapperBase() {
        override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
            stateMapper?.let { return it.invoke(state) }
            val variant = states.find { it.getBlockState(this@BlockBase) == state }?.stateName ?: "normal"
            return ModelResourceLocation(registryName, variant)
        }
    }

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing,
                                      hitX: Float, hitY: Float, hitZ: Float, meta: Int,
                                      placer: EntityLivingBase?, hand: EnumHand): IBlockState {

        val state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand)
        onBlockPlaced?.let {
            return it.invoke(
                    OnBlockPlacedArgs(world, pos, facing, vec3Of(hitX, hitY, hitZ), meta, placer, hand, defaultState)
            )
        }
        return state
    }

    override fun isFullBlock(state: IBlockState?): Boolean = !translucent_
    override fun isOpaqueCube(state: IBlockState?) = enableOcclusionOptimization
    override fun isFullCube(state: IBlockState?) = !translucent_
}

class BlockTileBase(val factory: (World, IBlockState) -> TileEntity?, material: Material)
    : BlockBase(material), ITileEntityProvider {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? = factory(worldIn, getStateFromMeta(meta))
}

data class BoundingBoxArgs(val state: IBlockState, val source: IBlockAccess, val pos: BlockPos)

data class OnActivatedArgs(val worldIn: World, val pos: BlockPos, val state: IBlockState, val playerIn: EntityPlayer,
                           val hand: EnumHand, val heldItem: ItemStack, val side: EnumFacing, val hit: IVector3)

data class OnBlockPlacedArgs(val world: World, val pos: BlockPos, val facing: EnumFacing,
                             val hit: IVector3, val itemMetadata: Int,
                             val placer: EntityLivingBase?, val hand: EnumHand, val defaultValue: IBlockState)