package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.block.Ores
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.tileentity.modules.pumpjack.WorldIterator
import com.cout970.magneticraft.tileentity.modules.pumpjack.serializeNBT
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.getBlockPos
import com.cout970.magneticraft.util.newNbt
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack

class ModulePumpjack(
        val storage: ModuleInternalStorage,
        val tank: Tank,
        val ref: () -> BlockPos,
        override val name: String = "module_pumpjack"
) : IModule {

    override lateinit var container: IModuleContainer

    companion object {
        val oilFilter: (IBlockState) -> Boolean = { it.block == Ores.oilSource }
    }

    var status = Status.SEARCHING_DEPOSIT
    var depositSearch: WorldIterator? = null
    var depositStart: BlockPos = BlockPos.ORIGIN
    var placeBlocks: WorldIterator? = null
    var nextSource: WorldIterator? = null
    var nextSourcePos: BlockPos = BlockPos.ORIGIN

    override fun update() {
        if (world.isClient) return

        when (status) {
            Status.SEARCHING_DEPOSIT -> {
                searchDeposit()
            }
            Status.DIGGING -> {
                digAndPlacePipes()
            }
            Status.SEARCHING_SOURCE -> {
                searchNextSource()
            }
            Status.EXTRACTING -> {
                extractOil()
            }
        }
    }


    fun searchDeposit() {
        if (!container.shouldTick(20)) return

        if (depositSearch == null) {
            val start = ref().down()
            val size = 3

            depositSearch = WorldIterator.create(
                    BlockPos(start.x - size, start.y, start.z - size),
                    BlockPos(start.x + size, 0, start.z + size)
            )
        }

        depositSearch?.let {

            if (!it.hasNext()) {
                depositSearch = null
            } else {

                // analize 32 blocks every tick
                val (pos) = find(it, oilFilter, 32) ?: return

                depositStart = pos

                depositSearch = null
                status = Status.DIGGING
            }
        }
    }


    fun digAndPlacePipes() {
        if (!container.shouldTick(10)) return

        if (placeBlocks == null) {
            val start = ref().down()
            placeBlocks = WorldIterator.create(
                    BlockPos(start.x, start.y, start.z),
                    BlockPos(start.x, depositStart.y, start.z)
            )
        }

        placeBlocks?.let {
            if (!it.hasNext()) {
                placeBlocks = null
                status = Status.SEARCHING_SOURCE
            } else {
                val (pos) = find(it, { it.block != Blocks.AIR }, 5) ?: return
                world.destroyBlock(pos, true)
            }
        }
    }

    fun searchNextSource() {

        if (nextSource == null) {
            val start = ref().down()
            val size = 128

            nextSource = WorldIterator.create(
                    BlockPos(start.x - size, depositStart.y - 10, start.z - size),
                    BlockPos(start.x + size, depositStart.y + 20, start.z + size)
            )
        }

        nextSource?.let {
            if (!it.hasNext()) {
                status = Status.SEARCHING_DEPOSIT
                nextSource = null
            } else {
                val result = find(it, oilFilter, 1280) ?: return
                nextSourcePos = result.first
                status = Status.EXTRACTING
            }
        }
    }

    fun extractOil() {
//        if (!container.shouldTick(10)) return

        val state = world.getBlockState(nextSourcePos)

        if (state.block == Ores.oilSource) {
            val amount = state[Ores.PROPERTY_OIL_AMOUNT]!!.amount

            if (amount > 0) {
                val oil = FluidRegistry.getFluid("oil") ?: error("Oil not found")
                val fluid = FluidStack(oil, 1000)

                if (tank.fill(fluid, false) == 1000) {
                    val newState = Ores.OilAmount.fromAmount(amount - 1)!!.getBlockState(Ores.oilSource)

                    world.setBlockState(nextSourcePos, newState)
                    tank.fill(fluid, true)
                }
                // does not change 'status'
                return
            } else {
                world.setBlockState(nextSourcePos, Blocks.GLASS.defaultState)
            }
        }

        status = Status.SEARCHING_SOURCE
    }

    fun find(iterator: WorldIterator, filter: (IBlockState) -> Boolean, limit: Int): Pair<BlockPos, IBlockState>? {
        var count = 0

        while (count < limit && iterator.hasNext()) {
            val pos = iterator.next()
            val blockState = world.getBlockState(pos)

            if (filter(blockState)) {
                return pos to blockState
            }
            count++
        }
        return null
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        status = Status.values()[nbt.getInteger("status")]
        depositSearch = WorldIterator.deserializeNBT(nbt.getCompoundTag("depositSearch"))
        depositStart = nbt.getBlockPos("depositStart")
        placeBlocks = WorldIterator.deserializeNBT(nbt.getCompoundTag("placeBlocks"))
        nextSource = WorldIterator.deserializeNBT(nbt.getCompoundTag("nextSource"))
        nextSourcePos = nbt.getBlockPos("nextSourcePos")
    }

    override fun serializeNBT() = newNbt {
        add("status", status.ordinal)
        add("depositSearch", depositSearch.serializeNBT())
        add("depositStart", depositStart)
        add("placeBlocks", placeBlocks.serializeNBT())
        add("nextSource", nextSource.serializeNBT())
        add("nextSourcePos", nextSourcePos)
    }

    enum class Status {
        SEARCHING_DEPOSIT,
        DIGGING,
        SEARCHING_SOURCE,
        EXTRACTING
    }
}