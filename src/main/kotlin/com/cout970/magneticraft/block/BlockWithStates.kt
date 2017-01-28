package com.cout970.magneticraft.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 2017/01/28.
 */
abstract class BlockWithStates(material: Material, name: String) : BlockBase(material, name) {

    // do no edit, never, really don't touch it
    override lateinit var inventoryVariants: Map<Int, String>

    abstract val states: Array<IStatesEnum>
    open fun initProperties() {}

    override fun getItemName(
            stack: ItemStack?) = "${unlocalizedName}_${states[stack!!.metadata].stateName}"

    override fun getMetaFromState(state: IBlockState): Int = states.find { it == state }?.ordinal ?: 0

    override fun getStateFromMeta(meta: Int): IBlockState? = states[meta].blockState

    override fun createBlockState(): BlockStateContainer {
        initProperties()
        val map = mutableMapOf<Int, String>()
        states.filter { it.isVisible }.forEach { value ->
            map += value.ordinal to value.stateName
        }
        inventoryVariants = map
        return BlockStateContainer(this, *states[0].properties.toTypedArray())
    }

    override fun getCustomStateMapper(): IStateMapper? = StateMapper(blockState, states)

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