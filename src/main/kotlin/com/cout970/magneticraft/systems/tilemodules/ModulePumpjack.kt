package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.features.ores.Blocks
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.energy.IMachineEnergyInterface
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.getBlockPos
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.network.AverageSyncVariable
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.vector.yi
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.*
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import com.cout970.magneticraft.systems.tilemodules.pumpjack.WorldIterator
import com.cout970.magneticraft.systems.tilemodules.pumpjack.serializeNBT
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import com.cout970.magneticraft.features.multiblock_parts.Blocks as MultiblockParts

class ModulePumpjack(
    val energy: IMachineEnergyInterface,
    val tank: Tank,
    val ref: () -> BlockPos,
    val active: () -> Boolean,
    override val name: String = "module_pumpjack"
) : IModule {

    override lateinit var container: IModuleContainer

    companion object {
        val oilFilter: (IBlockState) -> Boolean = { it.block == Blocks.oilSource }
        val pipeFilter: (IBlockState) -> Boolean = { it.block != MultiblockParts.pumpjackDrill }

        val nonEmptyOilFilter: (IBlockState) -> Boolean = {
            it.block == Blocks.oilSource && it.getValue(Blocks.PROPERTY_OIL_AMOUNT) != Blocks.OilAmount.EMPTY
        }
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

                    // analyze 32 blocks every tick
                    val (pos) = find(firstOilSearch, oilFilter, 9) ?: return

                    depositStart = pos
                    this.firstOilSearch = null

                    val size = 32 // 5 * 16
                    this.depositSearch = WorldIterator.create(
                        BlockPos(pos.x - size, pos.yi - 7, pos.z - size),
                        BlockPos(pos.x + size, pos.yi + 4, pos.z + size)
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

        if (!energy.hasEnergy(Config.pumpjackConsumption)) return
        val depositSearch = depositSearch

        if (depositSearch != null) {

            repeat(Config.pumpjackScanSpeed) {
                if (!depositSearch.hasNext()) {
                    this.depositSearch = null

                    if (depositSize > 0) {
                        status = Status.DIGGING
                        container.sendUpdateToNearPlayers()
                        return
                    }
                } else {

                    val (pos) = find(depositSearch, oilFilter, 8) ?: return@repeat
                    depositSize++

                    if (world.getBlockState(pos) != Blocks.OilAmount.EMPTY.getBlockState(Blocks.oilSource)) {
                        depositLeft++
                    }
                }
            }

            energy.useEnergy(Config.pumpjackConsumption)
        } else {
            status = Status.SEARCHING_OIL
            container.sendUpdateToNearPlayers()
        }
    }

    fun searchNextSource() {

        if (container.shouldTick(80)) {
            if (!scanPipes()) {
                status = Status.DIGGING
                container.sendUpdateToNearPlayers()
                return
            }
        }

        if (depositSize == 0 || depositLeft == 0) {
            status = Status.SEARCHING_DEPOSIT
            container.sendUpdateToNearPlayers()
            return
        }

        if (nextSource == null) {
            val start = ref().down()
            val size = 32

            nextSource = WorldIterator.create(
                BlockPos(start.x - size, depositStart.y - 10, start.z - size),
                BlockPos(start.x + size, depositStart.y + 20, start.z + size)
            )
        }

        repeat(Config.pumpjackScanSpeed) {
            val source = nextSource!!
            if (!source.hasNext()) {
                status = Status.SEARCHING_SOURCE
                nextSource = null
                container.sendUpdateToNearPlayers()
                return
            } else {
                val result = find(source, nonEmptyOilFilter, 8) ?: return@repeat
                nextSourcePos = result.first
                status = Status.EXTRACTING
            }
        }
    }

    fun extractOil() {
        if (!energy.hasEnergy(Config.pumpjackConsumption)) return
        production += Config.pumpjackConsumption
        energy.useEnergy(Config.pumpjackConsumption)


        if (!container.shouldTick(20)) return

        val state = world.getBlockState(nextSourcePos)

        if (state.block != Blocks.oilSource || state[Blocks.PROPERTY_OIL_AMOUNT] == Blocks.OilAmount.EMPTY) {
            status = Status.SEARCHING_SOURCE
            searchNextSource()
            return
        }

        val oil = FluidRegistry.getFluid("oil") ?: error("Oil not found, please report to mod author")
        val fluid = FluidStack(oil, Config.oilPerStage)

        if (tank.fill(fluid, false) == Config.oilPerStage) {

            val newAmount = state[Blocks.PROPERTY_OIL_AMOUNT]!!.amount - 1
            val newState = Blocks.OilAmount.fromAmount(newAmount)!!.getBlockState(Blocks.oilSource)

            if (newAmount == 0) {
                depositLeft--
                status = Status.SEARCHING_SOURCE
            }

            world.setBlockState(nextSourcePos, newState)
            tank.fill(fluid, true)
        }
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

    fun digAndPlacePipes() {
        if (!container.shouldTick(20)) return
        if (!energy.hasEnergy(Config.pumpjackConsumption)) return

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
                val (pos) = find(it, pipeFilter, 5) ?: return
                val blockstate = world.getBlockState(pos)

                if (!blockstate.block.isAir(blockstate, world, pos)) {
                    world.playEvent(2001, pos, Block.getStateId(blockstate))
                    blockstate.block.dropBlockAsItem(world, ref().up(), blockstate, 0)
                }
                if(blockstate.getBlockHardness(world, pos) >= 0){
                    world.setBlockState(pos, MultiblockParts.pumpjackDrill.defaultState)
                }
                energy.useEnergy(Config.pumpjackConsumption)
            }
        }
    }

    // true if the pipes are fine, false if they need repair
    fun scanPipes(): Boolean {
        val start = ref()

        repeat(start.y - depositStart.y) {
            val block = world.getBlockState(BlockPos(start.x, it, start.z))

            if (!pipeFilter(block)) {
                return false
            }
        }
        return true
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
            IntSyncVariable(DATA_ID_STATUS, status::ordinal) { status = Status.values()[it] },
            IntSyncVariable(DATA_ID_DEPOSIT_SIZE, ::depositSize) { depositSize = it },
            IntSyncVariable(DATA_ID_DEPOSIT_LEFT, ::depositLeft) { depositLeft = it },
            AverageSyncVariable(DATA_ID_MACHINE_PRODUCTION, production),
            FloatSyncVariable(DATA_ID_MACHINE_PROGRESS, {
                when (status) {
                    Status.SEARCHING_OIL -> {
                        firstOilSearch?.let { it.doneBlocks() / it.totalBlocks().toFloat() } ?: 0f
                    }
                    Status.SEARCHING_DEPOSIT -> {
                        depositSearch?.let { it.doneBlocks() / it.totalBlocks().toFloat() } ?: 0f
                    }
                    Status.DIGGING -> {
                        placeBlocks?.let { it.doneBlocks() / it.totalBlocks().toFloat() } ?: 0f
                    }
                    Status.SEARCHING_SOURCE -> {
                        nextSource?.let { it.doneBlocks() / it.totalBlocks().toFloat() } ?: 0f
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
        EXTRACTING,
        SEARCHING_SOURCE
    }
}