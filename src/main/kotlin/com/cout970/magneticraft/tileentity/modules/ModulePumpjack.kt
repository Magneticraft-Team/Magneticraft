package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.block.MultiblockParts
import com.cout970.magneticraft.block.Ores
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.core.*
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.network.AverageSyncVariable
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.tileentity.modules.pumpjack.WorldIterator
import com.cout970.magneticraft.tileentity.modules.pumpjack.serializeNBT
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.getBlockPos
import com.cout970.magneticraft.util.newNbt
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
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

    val production = ValueAverage()
    var depositLeft = 0
    var depositSize = 0

    var status = Status.SEARCHING_OIL
    var firstOilSearch: WorldIterator? = null
    var depositSearch: WorldIterator? = null
    var depositStart: BlockPos = BlockPos.ORIGIN
    var placeBlocks: WorldIterator? = null
    var nextSource: WorldIterator? = null
    var nextSourcePos: BlockPos = BlockPos.ORIGIN
    var processPercent = 0f

    override fun update() {
        if (world.isClient) return

        when (status) {
            Status.SEARCHING_OIL -> {
                searchOil()
            }
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

        production.tick()
    }

    fun searchOil() {

        repeat(9) {
            val firstOilSearch = firstOilSearch

            if (firstOilSearch != null) {
                if (!firstOilSearch.hasNext()) {
                    this.firstOilSearch = null
                    return
                } else {

                    // analize 32 blocks every tick
                    val (pos) = find(firstOilSearch, oilFilter, 9) ?: return

                    depositStart = pos
                    this.firstOilSearch = null

                    val size = 5 * 16
                    this.depositSearch = WorldIterator.create(
                            BlockPos(pos.x - size, 20 - 3, pos.z - size),
                            BlockPos(pos.x + size, 20 + 3, pos.z + size)
                    )

                    depositSize = 0
                    depositLeft = 0
                    status = Status.SEARCHING_DEPOSIT
                    container.sendUpdateToNearPlayers()
                }

            } else {
                val start = ref().down()
                val size = 3

                this.firstOilSearch = WorldIterator.create(
                        BlockPos(start.x - size, start.y, start.z - size),
                        BlockPos(start.x + size, 0, start.z + size),
                        true
                )
            }
        }
    }

    fun searchDeposit() {

        if (storage.energy > Config.pumpjackConsumption) return
        val depositSearch = depositSearch

        if (depositSearch != null) {

            repeat(100) {
                if (!depositSearch.hasNext()) {
                    this.depositSearch = null

                    if (depositSize > 0) {
                        status = Status.DIGGING
                        container.sendUpdateToNearPlayers()
                        return
                    }
                } else {

                    // analize 32 blocks every tick
                    val (pos) = find(depositSearch, oilFilter, 8) ?: return@repeat
                    depositSize++

                    if (world.getBlockState(pos) != Ores.OilAmount.EMPTY.getBlockState(Ores.oilSource)) {
                        depositLeft++
                    }
                }
            }
            storage.energy -= Math.max(0, Config.pumpjackConsumption.toInt())
        } else {
            status = Status.SEARCHING_OIL
            container.sendUpdateToNearPlayers()
        }
    }

    fun digAndPlacePipes() {
        if (!container.shouldTick(20)) return
        if (storage.energy > Config.pumpjackConsumption) return

        if (placeBlocks == null) {
            val start = ref()
            placeBlocks = WorldIterator.create(
                    BlockPos(start.x, start.y, start.z),
                    BlockPos(start.x, depositStart.y, start.z),
                    true
            )
        }

        placeBlocks?.let {
            if (!it.hasNext()) {
                placeBlocks = null
                status = Status.SEARCHING_SOURCE
                container.sendUpdateToNearPlayers()
            } else {
                val (pos) = find(it, { it.block != MultiblockParts.pumpjackDrill }, 5) ?: return
                val blockstate = world.getBlockState(pos)

                if (!blockstate.block.isAir(blockstate, world, pos)) {

                    world.playEvent(2001, pos, Block.getStateId(blockstate))
                    blockstate.block.dropBlockAsItem(world, pos, blockstate, 0)
                }
                world.setBlockState(pos, MultiblockParts.pumpjackDrill.defaultState)
                storage.energy -= Math.max(0, Config.pumpjackConsumption.toInt())
            }
        }
    }

    fun searchNextSource() {

        if (depositSize == 0 || depositLeft == 0) {
            status = Status.SEARCHING_DEPOSIT
            container.sendUpdateToNearPlayers()
            return
        }

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
                container.sendUpdateToNearPlayers()
            } else {
                val result = find(it, oilFilter, 1600) ?: return
                nextSourcePos = result.first
                status = Status.EXTRACTING
            }
        }
    }

    fun extractOil() {
        if (storage.energy > Config.pumpjackConsumption) return

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
                    production += Config.pumpjackConsumption
                    storage.energy -= Math.max(0, Config.pumpjackConsumption.toInt())
                }
                // does not change 'status'
                return
            } else {
                depositLeft--
            }
        }

        status = Status.SEARCHING_SOURCE
        searchNextSource()
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
        depositSize = nbt.getInteger("depositSize")
        depositLeft = nbt.getInteger("depositLeft")
        firstOilSearch = WorldIterator.deserializeNBT(nbt.getCompoundTag("firstOilSearch"))
        depositSearch = WorldIterator.deserializeNBT(nbt.getCompoundTag("depositSearch"))
        depositStart = nbt.getBlockPos("depositStart")
        placeBlocks = WorldIterator.deserializeNBT(nbt.getCompoundTag("placeBlocks"))
        nextSource = WorldIterator.deserializeNBT(nbt.getCompoundTag("nextSource"))
        nextSourcePos = nbt.getBlockPos("nextSourcePos")
    }

    override fun serializeNBT() = newNbt {
        add("status", status.ordinal)
        add("depositSize", depositSize)
        add("depositLeft", depositLeft)
        add("firstOilSearch", firstOilSearch.serializeNBT())
        add("depositSearch", depositSearch.serializeNBT())
        add("depositStart", depositStart)
        add("placeBlocks", placeBlocks.serializeNBT())
        add("nextSource", nextSource.serializeNBT())
        add("nextSourcePos", nextSourcePos)
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(
                IntSyncVariable(DATA_ID_STATUS, status::ordinal, { status = Status.values()[it] }),
                IntSyncVariable(DATA_ID_DEPOSIT_SIZE, ::depositSize, { depositSize = it }),
                IntSyncVariable(DATA_ID_DEPOSIT_LEFT, ::depositLeft, { depositLeft = it }),
                AverageSyncVariable(DATA_ID_MACHINE_PRODUCTION, production),
                FloatSyncVariable(DATA_ID_MACHINE_PROGRESS, {
                    when (status) {
                        ModulePumpjack.Status.SEARCHING_OIL -> {
                            firstOilSearch?.let { it.doneBlocks() / it.totalBlocks().toFloat() } ?: 0f
                        }
                        ModulePumpjack.Status.SEARCHING_DEPOSIT -> {
                            depositSearch?.let { it.doneBlocks() / it.totalBlocks().toFloat() } ?: 0f
                        }
                        ModulePumpjack.Status.DIGGING -> {
                            placeBlocks?.let { it.doneBlocks() / it.totalBlocks().toFloat() } ?: 0f
                        }
                        else -> 0f
                    }
                }, { processPercent = it })
        )
    }

    enum class Status {
        SEARCHING_OIL,
        SEARCHING_DEPOSIT,
        DIGGING,
        SEARCHING_SOURCE,
        EXTRACTING
    }
}