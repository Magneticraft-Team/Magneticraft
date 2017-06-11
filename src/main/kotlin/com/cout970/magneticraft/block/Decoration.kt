package com.cout970.magneticraft.block

import com.cout970.magneticraft.util.resource
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.util.IStringSerializable

/**
 * Created by cout970 on 2017/06/08.
 */

object Decoration {

    internal val builder = BlockBuilder().apply {
        material = Material.IRON
        creativeTab = CreativeTabs.FOOD
        states = LimestoneKind.values().toList()
    }

    val limestone = builder.apply { registryName = resource("limestone") }.build()
    val burnLimestone = builder.apply { registryName = resource("burn_limestone") }.build()



    val PROPERTY_LIMESTONE_KIND = PropertyEnum.create("limestone_kind", LimestoneKind::class.java)!!

    enum class LimestoneKind(
            override val stateName: String,
            override val isVisible: Boolean
    ) : IStatesEnum, IStringSerializable {
        NORMAL("normal", true),
        BRICK("brick", true),
        COBBLE("cobble", true);

        override val blockState: IBlockState
            get() = limestone.defaultState.withProperty(PROPERTY_LIMESTONE_KIND, this)

        override val properties: List<IProperty<*>> = listOf(PROPERTY_LIMESTONE_KIND)

        override fun getName() = name.toLowerCase()
    }
}