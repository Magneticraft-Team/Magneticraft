package com.cout970.magneticraft.block.core

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.xi
import com.cout970.magneticraft.util.vector.yi
import com.cout970.magneticraft.util.vector.zi
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World


/**
 * Created by cout970 on 2017/06/08.
 */
class BlockBuilder {

    companion object {
        val openGui: (OnActivatedArgs) -> Boolean = {
            if (it.worldIn.isServer && !it.playerIn.isSneaking) {
                it.playerIn.openGui(Magneticraft, -1, it.worldIn, it.pos.xi, it.pos.yi, it.pos.zi)
                true
            } else false
        }
    }

    val constructor: (Material, List<IStatesEnum>) -> BlockBase = { a, b ->
        BlockBase.states_ = b
        BlockBase(a)
    }
    val tileConstructor: (Material, List<IStatesEnum>, (World, IBlockState) -> TileEntity?) -> BlockBase = { a, b, c ->
        BlockBase.states_ = b
        BlockTileBase(c, a)
    }

    var factory: ((World, IBlockState) -> TileEntity?)? = null
    var registryName: ResourceLocation? = null
    var material: Material? = null
    var creativeTab: CreativeTabs? = null
    var boundingBox: ((BoundingBoxArgs) -> AABB)? = null
    var onActivated: ((OnActivatedArgs) -> Boolean)? = null
    var stateMapper: ((IBlockState) -> ModelResourceLocation)? = null
    var states: List<IStatesEnum>? = null
    var hardness = 1.5f
    var explosionResistance = 10.0f

    fun withName(name: String): BlockBuilder {
        registryName = resource(name)
        return this
    }

    fun build(): BlockBase {
        requireNotNull(registryName) { "registryName was null" }
        requireNotNull(material) { "material was null" }

        val block = if (factory != null)
            tileConstructor(material!!, states ?: listOf(IStatesEnum.default), factory!!)
        else
            constructor(material!!, states ?: listOf(IStatesEnum.default))

        block.apply {
            registryName = this@BlockBuilder.registryName!!
            creativeTab?.let { setCreativeTab(it) }
            boundingBox?.let { aabb = it }
            setHardness(hardness)
            setResistance(explosionResistance)
            unlocalizedName = "${registryName?.resourceDomain}.${registryName?.resourcePath}"
            onActivated = this@BlockBuilder.onActivated
            stateMapper = this@BlockBuilder.stateMapper
        }
        return block
    }
}


