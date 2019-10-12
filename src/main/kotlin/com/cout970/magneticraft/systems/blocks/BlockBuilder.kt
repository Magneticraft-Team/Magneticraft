package com.cout970.magneticraft.systems.blocks

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.misc.resource
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider


/**
 * Created by cout970 on 2017/06/08.
 */
class BlockBuilder {

    var constructor: (Material, List<IStatesEnum>) -> BlockBase = { a, b ->
        BlockBase.states_ = b
        BlockBase(a)
    }
    var tileConstructor: (Material, List<IStatesEnum>, (World, IBlockState) -> TileEntity?,
                          ((IBlockState) -> Boolean)?) -> BlockBase = { a, b, c, d ->
        BlockBase.states_ = b
        BlockTileBase(c, d, a)
    }
    var forceModelBake = false
    var customModels: List<Pair<String, ResourceLocation>> = emptyList()

    var factory: ((World, IBlockState) -> TileEntity?)? = null
    var factoryFilter: ((IBlockState) -> Boolean)? = null
    var registryName: ResourceLocation? = null
    var material: Material? = null
    var creativeTab: CreativeTabs? = null
    var boundingBox: ((BoundingBoxArgs) -> List<AABB>)? = null
    // default value if false
    var onActivated: ((OnActivatedArgs) -> Boolean)? = null
    var stateMapper: ((IBlockState) -> ModelResourceLocation)? = null
    var onBlockPlaced: ((OnBlockPlacedArgs) -> IBlockState)? = null
    var pickBlock: ((PickBlockArgs) -> ItemStack)? = null
    var canPlaceBlockOnSide: ((CanPlaceBlockOnSideArgs) -> Boolean)? = null
    var capabilityProvider: ICapabilityProvider? = null
    var onNeighborChanged: ((OnNeighborChangedArgs) -> Unit)? = null
    var blockStatesToPlace: ((BlockStatesToPlaceArgs) -> List<Pair<BlockPos, IBlockState>>)? = null
    var onBlockBreak: ((BreakBlockArgs) -> Unit)? = null
    var onDrop: ((DropsArgs) -> List<ItemStack>)? = null
    var onBlockPostPlaced: ((OnBlockPostPlacedArgs) -> Unit)? = null
    var onUpdateTick: ((OnUpdateTickArgs) -> Unit)? = null
    var collisionBox: ((CollisionBoxArgs) -> AABB?)? = null
    var shouldSideBeRendered: ((ShouldSideBeRendererArgs) -> Boolean)? = null
    var onEntityCollidedWithBlock: ((OnEntityCollidedWithBlockArgs) -> Unit)? = null
    var canConnectRedstone: ((CanConnectRedstoneArgs) -> Boolean)? = null
    var redstonePower: ((RedstonePowerArgs) -> Int)? = null
    var blockFaceShape: ((GetBlockFaceShapeArgs) -> BlockFaceShape)? = null
    var addInformation: ((AddInformationArgs) -> Unit)? = null

    var states: List<IStatesEnum>? = null
    var hardness = 1.5f
    var explosionResistance = 10.0f
    var lightEmission = 0.0f
    var generateDefaultItemBlockModel = true
    var enableOcclusionOptimization = true
    var translucent = false
    var tickRandomly = false
    var alwaysDropDefault = false
    var blockLayer: BlockRenderLayer? = null
    var tickRate = 10
    var dropWithTileNBT: Boolean = false

    var hasCustomModel: Boolean = false
        set(value) {
            field = value
            enableOcclusionOptimization = false
            translucent = true
        }

    fun withName(name: String): BlockBuilder {
        registryName = resource(name)
        return this
    }

    fun build(): BlockBase {
        requireNotNull(registryName) { "registryName was null" }
        requireNotNull(material) { "material was null" }

        val block = if (factory != null)
            tileConstructor(material!!, states ?: listOf(IStatesEnum.default), factory!!, factoryFilter)
        else
            constructor(material!!, states ?: listOf(IStatesEnum.default))

        block.apply {
            registryName = this@BlockBuilder.registryName!!
            creativeTab?.let { setCreativeTab(it) }
            boundingBox?.let { aabb = it }
            this@BlockBuilder.blockLayer?.let { blockLayer_ = it }

            setHardness(hardness)
            setResistance(explosionResistance)
            setLightOpacity(if (translucent) 0 else 255)
            setLightLevel(lightEmission)

            // @formatter:off
            unlocalizedName = "${registryName?.resourceDomain}.${registryName?.resourcePath}"
            dropWithTileNBT = this@BlockBuilder.dropWithTileNBT
            onActivated = this@BlockBuilder.onActivated
            stateMapper = this@BlockBuilder.stateMapper
            enableOcclusionOptimization = this@BlockBuilder.enableOcclusionOptimization
            translucent_ = this@BlockBuilder.translucent
            onBlockPlaced = this@BlockBuilder.onBlockPlaced
            customModels = this@BlockBuilder.customModels
            forceModelBake = this@BlockBuilder.forceModelBake
            pickBlock = this@BlockBuilder.pickBlock
            generateDefaultItemModel = this@BlockBuilder.generateDefaultItemBlockModel
            canPlaceBlockOnSide = this@BlockBuilder.canPlaceBlockOnSide
            capabilityProvider = this@BlockBuilder.capabilityProvider
            onNeighborChanged = this@BlockBuilder.onNeighborChanged
            alwaysDropDefault = this@BlockBuilder.alwaysDropDefault
            blockStatesToPlace = this@BlockBuilder.blockStatesToPlace
            onBlockBreak = this@BlockBuilder.onBlockBreak
            onDrop = this@BlockBuilder.onDrop
            onBlockPostPlaced = this@BlockBuilder.onBlockPostPlaced
            tickRandomly = this@BlockBuilder.tickRandomly
            onUpdateTick = this@BlockBuilder.onUpdateTick
            collisionBox = this@BlockBuilder.collisionBox
            shouldSideBeRendered_ = this@BlockBuilder.shouldSideBeRendered
            tickRate_ = this@BlockBuilder.tickRate
            onEntityCollidedWithBlock = this@BlockBuilder.onEntityCollidedWithBlock
            canConnectRedstone = this@BlockBuilder.canConnectRedstone
            redstonePower = this@BlockBuilder.redstonePower
            getBlockFaceShape = this@BlockBuilder.blockFaceShape
            addInformation = this@BlockBuilder.addInformation
            // @formatter:on
        }
        return block
    }

    inline fun factoryOf(crossinline func: () -> TileEntity): ((World, IBlockState) -> TileEntity?) = { _, _ -> func() }

    fun copy(func: BlockBuilder.() -> Unit): BlockBuilder {
        val newBuilder = BlockBuilder()

        newBuilder.constructor = constructor
        newBuilder.tileConstructor = tileConstructor
        newBuilder.customModels = customModels
        newBuilder.factory = factory
        newBuilder.registryName = registryName
        newBuilder.material = material
        newBuilder.creativeTab = creativeTab
        newBuilder.dropWithTileNBT = dropWithTileNBT
        newBuilder.boundingBox = boundingBox
        newBuilder.onActivated = onActivated
        newBuilder.stateMapper = stateMapper
        newBuilder.onBlockPlaced = onBlockPlaced
        newBuilder.pickBlock = pickBlock
        newBuilder.states = states
        newBuilder.hardness = hardness
        newBuilder.explosionResistance = explosionResistance
        newBuilder.enableOcclusionOptimization = enableOcclusionOptimization
        newBuilder.translucent = translucent
        newBuilder.generateDefaultItemBlockModel = generateDefaultItemBlockModel
        newBuilder.canPlaceBlockOnSide = canPlaceBlockOnSide
        newBuilder.capabilityProvider = capabilityProvider
        newBuilder.onNeighborChanged = onNeighborChanged
        newBuilder.alwaysDropDefault = alwaysDropDefault
        newBuilder.blockStatesToPlace = blockStatesToPlace
        newBuilder.onBlockBreak = onBlockBreak
        newBuilder.onDrop = onDrop
        newBuilder.onBlockPostPlaced = onBlockPostPlaced
        newBuilder.tickRandomly = tickRandomly
        newBuilder.blockLayer = blockLayer
        newBuilder.onUpdateTick = onUpdateTick
        newBuilder.shouldSideBeRendered = shouldSideBeRendered
        newBuilder.tickRate = tickRate
        newBuilder.onEntityCollidedWithBlock = onEntityCollidedWithBlock
        newBuilder.canConnectRedstone = canConnectRedstone
        newBuilder.redstonePower = redstonePower
        newBuilder.addInformation = addInformation

        func(newBuilder)

        return newBuilder
    }
}


