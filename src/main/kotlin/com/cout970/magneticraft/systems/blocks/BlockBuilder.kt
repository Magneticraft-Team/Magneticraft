package com.cout970.magneticraft.systems.blocks

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IBlockState
import com.cout970.magneticraft.RegistryEvents
import com.cout970.magneticraft.misc.resource
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.model.ModelResourceLocation
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockReader
import net.minecraftforge.common.capabilities.ICapabilityProvider
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


/**
 * Created by cout970 on 2017/06/08.
 */
class BlockBuilder {

    var constructor: (Block.Properties, List<IStatesEnum>) -> BlockBase = { a, b ->
        BlockBase.states_ = b
        BlockBase(a)
    }
    var tileConstructor: (Block.Properties, List<IStatesEnum>, (IBlockReader, IBlockState) -> TileEntity?,
                          ((IBlockState) -> Boolean)?) -> BlockBase = { a, b, c, d ->
        BlockBase.states_ = b
        BlockTileBase(c, d, a)
    }
    var forceModelBake = false
    var customModels: List<Pair<String, ResourceLocation>> = emptyList()

    var factory: ((IBlockReader, IBlockState) -> TileEntity?)? = null
    var factoryFilter: ((IBlockState) -> Boolean)? = null
    var registryName: ResourceLocation? = null
    var material: Material? = null
    var creativeTab: ItemGroup = ItemGroup.MISC
    var boundingBox: ((BoundingBoxArgs) -> List<AABB>)? = null
    // default value if false
    var onActivated: ((OnActivatedArgs) -> Boolean)? = null
    var stateMapper: ((IBlockState) -> ModelResourceLocation)? = null
    var onBlockPlaced: ((OnBlockPlacedArgs) -> IBlockState)? = null
    var pickBlock: ((PickBlockArgs) -> ItemStack)? = null
    var capabilityProvider: ICapabilityProvider? = null
    var onNeighborChanged: ((OnNeighborChangedArgs) -> Unit)? = null
    var blockStatesToPlace: ((BlockStatesToPlaceArgs) -> List<Pair<BlockPos, IBlockState>>)? = null
    var onBlockBreak: ((BreakBlockArgs) -> Unit)? = null
    var onDrop: ((DropsArgs) -> List<ItemStack>)? = null
    var onBlockPostPlaced: ((OnBlockPostPlacedArgs) -> Unit)? = null
    var onUpdateTick: ((OnUpdateTickArgs) -> Unit)? = null
    var collisionBox: ((CollisionBoxArgs) -> AABB?)? = null
    var onEntityCollidedWithBlock: ((OnEntityCollidedWithBlockArgs) -> Unit)? = null
    var canConnectRedstone: ((CanConnectRedstoneArgs) -> Boolean)? = null
    var redstonePower: ((RedstonePowerArgs) -> Int)? = null
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

        val props = Block.Properties.create(material)
        props.hardnessAndResistance(hardness, explosionResistance)
        props.lightValue((lightEmission * 15).toInt())
        if (tickRandomly) props.tickRandomly()

        val block = if (factory != null)
            tileConstructor(props, states ?: listOf(IStatesEnum.default), factory!!, factoryFilter)
        else
            constructor(props, states ?: listOf(IStatesEnum.default))

        block.apply {
            registryName = this@BlockBuilder.registryName!!
            creativeTab = this@BlockBuilder.creativeTab
            boundingBox?.let { aabb = it }
            this@BlockBuilder.blockLayer?.let { blockLayer_ = it }

            // @formatter:off
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
            capabilityProvider = this@BlockBuilder.capabilityProvider
            onNeighborChanged = this@BlockBuilder.onNeighborChanged
            blockStatesToPlace = this@BlockBuilder.blockStatesToPlace
            onBlockBreak = this@BlockBuilder.onBlockBreak
            onDrop = this@BlockBuilder.onDrop
            onBlockPostPlaced = this@BlockBuilder.onBlockPostPlaced
            onUpdateTick = this@BlockBuilder.onUpdateTick
            collisionBox = this@BlockBuilder.collisionBox
            tickRate_ = this@BlockBuilder.tickRate
            onEntityCollidedWithBlock = this@BlockBuilder.onEntityCollidedWithBlock
            canConnectRedstone = this@BlockBuilder.canConnectRedstone
            redstonePower = this@BlockBuilder.redstonePower
            addInformation = this@BlockBuilder.addInformation
            // @formatter:on
        }
        return block
    }

    fun <T : TileEntity> factoryOf(kclass: KClass<T>): ((IBlockReader, IBlockState) -> TileEntity?) {
        @Suppress("UNCHECKED_CAST")
        val type: TileEntityType<*> = RegistryEvents.tiles[kclass.java as Class<TileEntity>]
            ?: error("Invalid tile entity: ${kclass.qualifiedName}")
        val primaryConstructor = kclass.primaryConstructor
            ?: error("Invalid tile entity constructor: ${kclass.qualifiedName}")

        return { _, _ -> primaryConstructor.call(type) }
    }

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
        newBuilder.tickRate = tickRate
        newBuilder.onEntityCollidedWithBlock = onEntityCollidedWithBlock
        newBuilder.canConnectRedstone = canConnectRedstone
        newBuilder.redstonePower = redstonePower
        newBuilder.addInformation = addInformation

        func(newBuilder)

        return newBuilder
    }

    companion object {
        fun <T : TileEntity> createTile(kclass: KClass<T>): TileEntity {
            @Suppress("UNCHECKED_CAST")
            val type: TileEntityType<*> = RegistryEvents.tiles[kclass.java as Class<TileEntity>]
                ?: error("Invalid tile entity: ${kclass.qualifiedName}")
            val primaryConstructor = kclass.primaryConstructor
                ?: error("Invalid tile entity constructor: ${kclass.qualifiedName}")

            return primaryConstructor.call(type)
        }

    }
}


