package com.cout970.magneticraft.block

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.toAABBWith
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/06/08.
 */
class BlockBuilder {

    val constructor: (Material, List<IStatesEnum>?) -> BaseBlock = { a, b -> BaseBlock(a, b) }
    var registryName: ResourceLocation? = null
    var material: Material? = null
    var creativeTab: CreativeTabs? = null
    var boundingBox: ((BoundingBoxArgs) -> AABB)? = null
    var onActivated: ((OnActivatedArgs) -> Boolean)? = null
    var states: List<IStatesEnum>? = null
    var hardness = 1.5f
    var explosionResistance = 10.0f


    fun build(): Block {
        requireNotNull(registryName) { "registryName was null" }
        requireNotNull(material) { "material was null" }
        val block = constructor(material!!, states)
        block.registryName = registryName!!

        creativeTab?.let { block.setCreativeTab(it) }
        boundingBox?.let { block.aabb = it }
        block.apply {
            setHardness(hardness)
            setResistance(explosionResistance)
            unlocalizedName = "${registryName?.resourceDomain}.${registryName?.resourcePath}"
        }

        return block
    }

    companion object {

        fun create(func: BlockBuilder.() -> Unit): Block {
            val builder = BlockBuilder()
            func(builder)
            return builder.build()
        }

        fun test() {
            create {
                material = Material.IRON
                registryName = resource("test")
                creativeTab = CreativeTabs.FOOD
                boundingBox = { vec3Of(0, 0, 0) toAABBWith vec3Of(1, 0.5, 1) }
            }
        }
    }
}

data class BaseBlock(val material: Material, val states_: List<IStatesEnum>?) : Block(material) {

    var aabb: ((BoundingBoxArgs) -> AABB)? = null
    var onActivated: ((OnActivatedArgs) -> Boolean)? = null

    lateinit var states: List<IStatesEnum>

    val inventoryVariants = run {
        val map = mutableMapOf<Int, String>()
        states.filter { it.isVisible }.forEach { value ->
            map += value.ordinal to value.stateName
        }
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return aabb?.invoke(BoundingBoxArgs(state, source, pos)) ?: FULL_BLOCK_AABB
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                  hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float,
                                  hitZ: Float): Boolean {
        val heldItem = playerIn.getHeldItem(hand)

        return onActivated?.invoke(
                OnActivatedArgs(worldIn, pos, state, playerIn, hand, heldItem, side, vec3Of(hitX, hitY, hitZ)))
               ?: super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ)
    }


    fun getItemName(stack: ItemStack?) = "${unlocalizedName}_${states[stack!!.metadata].stateName}"

    override fun getMetaFromState(state: IBlockState): Int = states.find { it == state }?.ordinal ?: 0

    override fun getStateFromMeta(meta: Int): IBlockState? = states[meta].blockState

    override fun createBlockState(): BlockStateContainer {
        if (states_ == null) {
            states = listOf(object : IStatesEnum {
                override val blockState: IBlockState get() = defaultState
                override val isVisible: Boolean = true
                override val stateName: String = "normal"
                override val properties: List<IProperty<*>> = emptyList()
                override val ordinal: Int = 0
            })
        }else{
            states = states_
        }
        return BlockStateContainer(this, *states[0].properties.toTypedArray())
    }

    fun getCustomStateMapper(): IStateMapper? = StateMapper(blockState, states.toTypedArray())

    class StateMapper(val container: BlockStateContainer, val states: Array<IStatesEnum>) : IStateMapper {

        override fun putStateModelLocations(blockIn: Block): MutableMap<IBlockState, ModelResourceLocation> {
            return mutableMapOf<IBlockState, ModelResourceLocation>().apply {
                states.map {
                    this += (it.blockState to ModelResourceLocation(REGISTRY.getNameForObject(blockIn), it.stateName))
                }
            }
        }
    }
}

data class BoundingBoxArgs(val state: IBlockState, val source: IBlockAccess, val pos: BlockPos)

data class OnActivatedArgs(val worldIn: World, val pos: BlockPos, val state: IBlockState, val playerIn: EntityPlayer,
                           val hand: EnumHand, val heldItem: ItemStack, val side: EnumFacing, val hit: IVector3)



