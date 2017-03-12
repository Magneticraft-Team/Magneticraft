package com.cout970.magneticraft.tileentity.multiblock

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.registries.machines.sifter.SifterRecipeManager
import com.cout970.magneticraft.api.registries.machines.sifter.ISifterRecipe
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.isIn
import com.cout970.magneticraft.misc.crafting.CraftingProcess
import com.cout970.magneticraft.misc.inventory.ItemOutputHelper
import com.cout970.magneticraft.misc.inventory.consumeItem
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.set
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.multiblock.IMultiblockCenter
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.impl.MultiblockSifter
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.getEnumFacing
import com.cout970.magneticraft.util.interpolate
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.*
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
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
@TileRegister("sifter")
class TileSifter : TileBase(), IMultiblockCenter {

    override var multiblock: Multiblock? get() = MultiblockSifter
        set(value) {/* ignored */}

    override var centerPos: BlockPos? get() = BlockPos.ORIGIN
        set(value) {/* ignored */}

    override var multiblockFacing: EnumFacing? = null

    enum class Stage(val ord: Int) {
        PRIMARY (1), SECONDARY(2), TERTIARY(3)
    }

    val node = ElectricNode({ worldObj }, { pos + direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) })

    val traitElectricity = TraitElectricity(this, listOf(node))

    override val traits: List<ITileTrait> = listOf(traitElectricity)

    val inventory = ItemStackHandler(4)
    var craftingProcess: Map<Stage, CraftingProcess>

    private var recipeCache: ISifterRecipe? = null
    private var inputCache: ItemStack? = null

    init {
        craftingProcess = mapOf(
                Stage.PRIMARY to CraftingProcess({
                    //craft
                    consumeInput(Stage.PRIMARY, false)
                    outputItems(Stage.PRIMARY, false)
                }, {
                    //can craft
                    canCraft(Stage.PRIMARY)
                }, {
                    // use energy
                    useEnergy()
                }, {
                    getRecipe(Stage.PRIMARY)?.duration ?: 120f
                }),
                Stage.SECONDARY to CraftingProcess({
                    //craft
                    val recipe = getRecipe(Stage.SECONDARY)!!
                    consumeInput(Stage.SECONDARY, false)
                    if (recipe.secondaryChance > 0 && Random().nextFloat() <= recipe.secondaryChance)
                        outputItems(Stage.SECONDARY, false)
                }, {
                    //can craft
                    canCraft(Stage.SECONDARY)
                }, {
                    // use energy
                    useEnergy()
                }, {
                    getRecipe(Stage.SECONDARY)?.duration ?: 120f
                }),
                Stage.TERTIARY to CraftingProcess({
                    //craft
                    val recipe = getRecipe(Stage.SECONDARY)!!
                    consumeInput(Stage.TERTIARY, false)
                    if (recipe.tertiaryChance > 0 && Random().nextFloat() <= recipe.tertiaryChance)
                        outputItems(Stage.TERTIARY, false)
                }, {
                    //can craft
                    canCraft(Stage.TERTIARY)
                }, {
                    // use energy
                    useEnergy()
                }, {
                    getRecipe(Stage.TERTIARY)?.duration ?: 120f
                }))
    }

    private fun useEnergy() {
        val applied = Config.sifterConsumption * interpolate(node.voltage, 60.0, 70.0) / 3
        node.applyPower(-applied, false)
    }

    private fun canCraft(stage: Stage): Boolean {
        return node.voltage > ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE && consumeInput(stage, true)
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block !== newSate.block
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

    fun getRecipe(stage: Stage): ISifterRecipe? {
        return if (inventory[stage.ord - 1] != null) getRecipe(inventory[stage.ord - 1]!!) else null
    }

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

    override fun onDeactivate() = Unit

    val direction: EnumFacing get() = if (PROPERTY_DIRECTION.isIn(getBlockState()))
        getBlockState()[PROPERTY_DIRECTION] else EnumFacing.NORTH

    val active: Boolean get() = if (PROPERTY_ACTIVE.isIn(getBlockState()))
        getBlockState()[PROPERTY_ACTIVE] else false

    override fun save(): NBTTagCompound {
        val nbt = newNbt {
            if (multiblockFacing != null){
                add("direction", multiblockFacing!!)
            }
            add("inv", inventory.serializeNBT())
            craftingProcess.forEach {
                add("crafting" + it.key.ord, it.value.serializeNBT())
            }
        }
        return super.save().also { it.merge(nbt) }
    }

    override fun load(nbt: NBTTagCompound) = nbt.run {
        if (hasKey("direction")) multiblockFacing = getEnumFacing("direction")
        if (hasKey("inv")) inventory.deserializeNBT(getCompoundTag("inv"))
        craftingProcess.forEach {
            if (hasKey("crafting" + it.key.ord)) it.value.deserializeNBT(getCompoundTag("crafting"))
        }
        super.load(nbt)
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = (BlockPos.ORIGIN toAABBWith direction.rotatePoint(BlockPos.ORIGIN,
            multiblock!!.size)).offset(direction.rotatePoint(BlockPos.ORIGIN, -multiblock!!.center)).offset(pos)

    companion object {
        val ENERGY_INPUT = BlockPos(0, 1, 0)
        val ITEM_INPUT = BlockPos(0, 1, 0)
        val PRIMARY_OUTPUT = BlockPos(0, -1, 1)
        val SECONDARY_OUTPUT = BlockPos(0, -1, 2)
        val TERTIARY_OUTPUT = BlockPos(0, -1, 3)
        val ITEM_OUTPUT_OFF = BlockPos(0, 0, 4)
        val OUTPUTS = arrayListOf(PRIMARY_OUTPUT, SECONDARY_OUTPUT, TERTIARY_OUTPUT)
    }

    override fun onBreak() {
        super.onBreak()
        if (worldObj.isServer) {
            val item = inventory[0] //Slots 1 to 3 are virtual slots that should be already considered consumed
            if (item != null) {
                dropItem(item, pos)
            }
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean {
        val transPos = direction.rotatePoint(BlockPos.ORIGIN, relPos)
        if (capability == ELECTRIC_NODE_HANDLER) {
            if (ENERGY_INPUT == transPos && (facing == direction.rotateY() || facing == null))
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
        if (capability == ELECTRIC_NODE_HANDLER)
            if (ENERGY_INPUT == transPos && (facing == direction.rotateY()))
                return this as T
        if (capability == ITEM_HANDLER) {
            if (facing == EnumFacing.UP && ITEM_INPUT == transPos)
                return InputInventory(inventory, 1) as T
            if (facing == direction.rotateYCCW()) {
                OUTPUTS.forEach {
                    if (it == transPos)
                        return OutputInventory() as T
                }
            }
        }
        return null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability == ITEM_HANDLER) return true
        if (capability == ELECTRIC_NODE_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ITEM_HANDLER) return InputInventory(inventory, 1) as T
        if (capability == ELECTRIC_NODE_HANDLER) return this as T
        return super.getCapability(capability, facing)
    }

    override fun shouldRenderInPass(pass: Int): Boolean {
        return if (active) super.shouldRenderInPass(pass) else pass == 1
    }

    fun consumeInput(stage: Stage, simulate: Boolean): Boolean {
        val recipe = getRecipe(stage) ?: return false
        val result: ItemStack?

        if (stage != Stage.TERTIARY) {
            result = inventory.extractItem(stage.ord, recipe.input.stackSize, simulate)
        } else {
            result = null
        }

        if (result == null) {
            if (!simulate) {
                inventory[stage.ord - 1] = inventory[stage.ord - 1]!!.consumeItem(recipe.input.stackSize)
            }
            return true
        }
        return false
    }

    fun outputItems(stage: Stage, simulate: Boolean): Boolean {
        val ord = stage.ord
        val outputHelper = ItemOutputHelper(world, posTransform(OUTPUTS[ord - 1]),
                direction.rotatePoint(BlockPos(0, 0, 0), ITEM_OUTPUT_OFF).toVec3d())
        val recipe = getRecipe(stage) ?: return false
        val output: ItemStack
        when (ord) {
            1 -> output = recipe.primary
            2 -> output = recipe.secondary
            3 -> output = recipe.tertiary
            else -> throw IllegalArgumentException("Sifter output corruption detected.")
        }
        if (inventory[ord - 1]!!.stackSize < recipe.input.stackSize) return false
        if (outputHelper.ejectItems(output, simulate) != null) return false
        return true
    }

    class InputInventory(val inv: IItemHandler, private val in_size: Int) : IItemHandler by inv {
        override fun getSlots(): Int {
            return in_size
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
            return null
        }
    }

    class OutputInventory : IItemHandler {
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