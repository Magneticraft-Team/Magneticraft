package com.cout970.magneticraft.tileentity.multiblock

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.vectors.minus
import coffee.cypher.mcextlib.extensions.vectors.toDoubleVec
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.registries.machines.sifter.SifterRecipeManager
import com.cout970.magneticraft.api.registries.machines.sifter.ISifterRecipe
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.multiblock.IMultiblockCenter
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.impl.MultiblockSifter
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.tileentity.electric.TileElectricBase
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.misc.CraftingProcess
import com.sun.javaws.exceptions.InvalidArgumentException
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import java.util.*

/**
 * Created by cout970 on 19/08/2016.
 */
class TileSifter : TileElectricBase(), IMultiblockCenter {

    override var multiblock: Multiblock?
        get() = MultiblockSifter
        set(value) {/* ignored */
        }

    override var centerPos: BlockPos?
        get() = BlockPos.ORIGIN
        set(value) {/* ignored */
        }

    override var multiblockFacing: EnumFacing? = null

    enum class Stage(val ord: Int) {
        PRIMARY(1), SECONDARY(2), TERTIARY(3)
    }

    val node = ElectricNode({ worldObj }, { pos + direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) })
    override val electricNodes: List<IElectricNode> get() = listOf(node)
    val inventory = ItemStackHandler(4)
    var craftingProcess: MutableMap<Stage, CraftingProcess>

    private var recipeCache: ISifterRecipe? = null
    private var inputCache: ItemStack? = null

    init {
        craftingProcess = mutableMapOf(Stage.PRIMARY to CraftingProcess({//craft
            consumeInput(Stage.PRIMARY, false)
            outputItems(Stage.PRIMARY, false)
        }, { //can craft
            canCraft(Stage.PRIMARY)
        }, { // use energy
            useEnergy()
        }, {
            getRecipe(Stage.PRIMARY)?.duration ?: 120f
        }),
                Stage.SECONDARY to CraftingProcess({//craft
                    val recipe = getRecipe(Stage.SECONDARY)!!
                    consumeInput(Stage.SECONDARY, false)
                    if (recipe.secondaryChance > 0 && Random().nextFloat() <= recipe.secondaryChance)
                        outputItems(Stage.SECONDARY, false)
                }, { //can craft
                    canCraft(Stage.SECONDARY)
                }, { // use energy
                    useEnergy()
                }, {
                    getRecipe(Stage.SECONDARY)?.duration ?: 120f
                }),
                Stage.TERTIARY to CraftingProcess({//craft
                    val recipe = getRecipe(Stage.SECONDARY)!!
                    consumeInput(Stage.TERTIARY, false)
                    if (recipe.tertiaryChance > 0 && Random().nextFloat() <= recipe.tertiaryChance)
                        outputItems(Stage.TERTIARY, false)
                }, { //can craft
                    canCraft(Stage.TERTIARY)
                }, { // use energy
                    useEnergy()
                }, {
                    getRecipe(Stage.TERTIARY)?.duration ?: 120f
                }))
    }

    private fun useEnergy() {
        val applied = Config.sifterConsumption * interpolate(node.voltage, 60.0, 70.0) / 3
        node.applyPower(-applied, false)
    }

    private fun canCraft(stage: Stage): Boolean = node.voltage > TIER_1_MACHINES_MIN_VOLTAGE
            && consumeInput(stage, true) && consumeInput(stage, false)

    override fun shouldRefresh(world: World?, pos: BlockPos?, oldState: IBlockState?, newSate: IBlockState?): Boolean {
        return oldState?.block !== newSate?.block
    }

    fun posTransform(worldPos: BlockPos): BlockPos = direction.rotatePoint(BlockPos.ORIGIN, worldPos) + pos

    fun getRecipe(input: ItemStack): ISifterRecipe? {
        if (input === inputCache) return recipeCache
        val recipe = SifterRecipeManager.findRecipe(input)
        if (recipe != null) {
            recipeCache = recipe
            inputCache = input
        }
        return recipe
    }

    fun getRecipe(stage: Stage): ISifterRecipe? = if (inventory[stage.ord - 1] != null) getRecipe(inventory[stage.ord - 1]!!) else null

    override fun update() {
        if (worldObj.isServer && active) {
            if (shouldTick(20)) sendUpdateToNearPlayers()
            craftingProcess.forEach { it.value.tick(worldObj, 1f) }
        }
        super.update()
    }

    override fun onActivate() {
        getBlockState()
        sendUpdateToNearPlayers()
    }

    override fun onDeactivate() {
    }

    val direction: EnumFacing get() = if (PROPERTY_DIRECTION.isIn(getBlockState()))
        PROPERTY_DIRECTION[getBlockState()] else EnumFacing.NORTH

    val active: Boolean get() = if (PROPERTY_ACTIVE.isIn(getBlockState()))
        PROPERTY_ACTIVE[getBlockState()] else false

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        if (multiblockFacing != null) setEnumFacing("direction", multiblockFacing!!)
        setTag("inv", inventory.serializeNBT())
        craftingProcess.forEach {
            setTag("crafting" + it.key.ord, it.value.serializeNBT())
        }
    }

    override fun load(nbt: NBTTagCompound) = nbt.run {
        if (hasKey("direction")) multiblockFacing = getEnumFacing("direction")
        if (hasKey("inv")) inventory.deserializeNBT(getCompoundTag("inv"))
        craftingProcess.forEach {
            if (hasKey("crafting" + it.key.ord)) it.value.deserializeNBT(getCompoundTag("crafting"))
        }
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = (pos - BlockPos(1, 0, 1)) to (pos + BlockPos(2, 5, 2))

    companion object {
        val ENERGY_INPUT = BlockPos(0, 1, 0)
        val ITEM_INPUT = BlockPos(0, 1, 0)
        val PRIMARY_OUTPUT = BlockPos(1, 0, 3)
        val SECONDARY_OUTPUT = BlockPos(1, 0, 2)
        val TERTIARY_OUTPUT = BlockPos(1, 0, 1)
        val ITEM_OUTPUT_OFF = BlockPos(1, 0, 0)
        val OUTPUTS = arrayListOf(PRIMARY_OUTPUT, SECONDARY_OUTPUT, TERTIARY_OUTPUT)
    }

    override fun onBreak() {
        super.onBreak()
        if (worldObj.isServer) {
            val item = inventory[0] //Slots 1 to 3 are virtual slots that should be already considered consumed
            if (item != null) {
                dropItem(item, pos)
                dropItem(item, pos)
            }
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean {
        val transPos = direction.rotatePoint(BlockPos.ORIGIN, relPos)
        if (capability == NODE_HANDLER) {
            if (ENERGY_INPUT == transPos && (facing == direction.rotateY() ||
                    facing == null))
                return true
        }
        if (capability == ITEM_HANDLER) {
            if (facing == EnumFacing.UP && ITEM_INPUT == transPos)
                return true
            if (facing == direction.rotateYCCW()) {
                OUTPUTS.forEach {
                    if (it == transPos)
                        return true
                }
            }
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?, relPos: BlockPos): T? {
        val transPos = direction.rotatePoint(BlockPos.ORIGIN, relPos)
        if (capability == NODE_HANDLER)
            if (ENERGY_INPUT == transPos && (facing == direction.rotateY()))
                return this as T
        if (capability == ITEM_HANDLER) {
            if (facing == EnumFacing.UP && ITEM_INPUT == transPos)
                return inInventory(inventory, 1) as T
            if (facing == direction.rotateYCCW()) {
                OUTPUTS.forEach {
                    if (it == transPos)
                        return outInventory() as T
                }
            }
        }
        return null
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == ITEM_HANDLER) return true
        if (capability == NODE_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == ITEM_HANDLER) return inInventory(inventory, 1) as T
        if (capability == NODE_HANDLER) return this as T
        return super.getCapability(capability, facing)
    }

    override fun shouldRenderInPass(pass: Int): Boolean {
        return if (active) super.shouldRenderInPass(pass) else pass == 1
    }

    fun consumeInput(stage: Stage, simulate: Boolean): Boolean {
        val recipe = getRecipe(stage) ?: return false
        var result: ItemStack?
        if (stage != Stage.TERTIARY) {
            result = inventory.insertItem(stage.ord, recipe.input, simulate)
        } else result = null
        if (result == null) {
            if (!simulate) inventory[stage.ord - 1]!!.consumeItem(recipe.input.stackSize)
            return true
        }
        return false
    }

    fun outputItems(stage: Stage, simulate: Boolean): Boolean {
        val ord = stage.ord
        val outputHelper = itemOutputHelper(world, posTransform(OUTPUTS[ord - 1]), direction.rotatePoint(BlockPos(0, 0, 0), ITEM_OUTPUT_OFF).toDoubleVec())
        val recipe = getRecipe(stage) ?: return false
        var output: ItemStack
        when (ord) {
            1 -> output = recipe.primary
            2 -> output = recipe.secondary
            3 -> output = recipe.tertiary
            else -> throw InvalidArgumentException(arrayOf("Sifter output corruption detected."))
        }
        if (inventory[ord - 1]!!.stackSize < recipe.input.stackSize) return false
        if (outputHelper.ejectItems(output, simulate) != null) return false
        return true
    }

    class inInventory(val inv: IItemHandler, private val in_size: Int) : IItemHandler by inv {
        override fun getSlots(): Int {
            return in_size
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
            return null
        }
    }

    class outInventory() : IItemHandler {
        override fun getSlots(): Int {
            return 0
        }

        override fun getStackInSlot(slot: Int): ItemStack? {
            return null
        }

        override fun insertItem(slot: Int, stack: ItemStack?, simulate: Boolean): ItemStack? {
            return stack
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
            return null
        }
    }
}