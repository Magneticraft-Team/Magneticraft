package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.features.electric_machines.Blocks
import com.cout970.magneticraft.features.electric_machines.TileWindTurbineGap
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.DATA_ID_MACHINE_PRODUCTION
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.gen.NoiseGeneratorPerlin
import java.util.*

class ModuleWindTurbine(
    val electricNode: IElectricNode,
    val facingGetter: () -> EnumFacing,
    override val name: String = "module_steam_generator"
) : IModule {

    override lateinit var container: IModuleContainer

    companion object {
        val windNoise = NoiseGeneratorPerlin(Random(1234L), 1)
    }

    var openSpace = 0f
        private set
    var currentWind = 0f
        private set

    // This is needed because calling facingGetter in onBreak returns NORTH, facingGetter tries to get the orientation
    // from the current IBlockState, but it is already removed when onBreak is called
    var facingCache = EnumFacing.UP
    val production = ValueAverage()
    private var nextWind = 0f
    private var counter = 150
    var hasTurbineHitbox = false
    var rotation = 0f
    var rotationSpeed = 0f

    override fun update() {

        if (facingCache == EnumFacing.UP) {
            facingCache = facingGetter()
        }

        counter++
        if (counter > 200) {
            counter = 0
            updateOpenSpace()
            updateNextWind()

            if (world.isServer) {
                hasTurbineHitbox = checkTurbineHitbox()
                if (!hasTurbineHitbox && canPlaceTurbineHitbox()) {
                    placeTurbineHitbox()
                    hasTurbineHitbox = true
                }
                container.sendUpdateToNearPlayers()
            }
        }

        updateWind()

        if (world.isClient) return

        if (hasTurbineHitbox && electricNode.voltage < ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE) {
            val power = Config.windTurbineMaxProduction * openSpace * currentWind * (pos.y / 256.0)
            electricNode.applyPower(power, false)
            production += power
        }

        production.tick()
    }

    fun getReplaceBlock(): IBlockState = Blocks.windTurbineGap.defaultState

    fun isBlockValid(blockPos: BlockPos): Boolean {
        val state = world.getBlockState(blockPos)
        if (state != getReplaceBlock()) {
            return false
        } else {
            val tile = world.getTile<TileWindTurbineGap>(blockPos)
            if (tile == null || tile.centerPos != pos) {
                return false
            }
        }
        return true
    }

    fun checkTurbineHitbox(): Boolean {
        var formed = true
        iterateHitbox { base ->
            val blockPos = pos + base
            if (!isBlockValid(blockPos)) formed = false
        }
        return formed
    }

    fun canPlaceTurbineHitbox(): Boolean {
        var canPlace = true
        iterateHitbox { base ->
            val blockPos = pos + base
            val state = world.getBlockState(blockPos)
            val replaceable = state.block.isReplaceable(world, blockPos)

            if (!replaceable && !isBlockValid(blockPos))
                canPlace = false
        }
        return canPlace
    }

    fun placeTurbineHitbox() {
        iterateHitbox { base ->
            val blockPos = pos + base
            world.setBlockState(blockPos, getReplaceBlock())
            val tile = world.getTile<TileWindTurbineGap>(blockPos)
            tile?.centerPos = pos
        }
    }

    fun removeTurbineHitbox(centerBreaking: Boolean = false) {
        hasTurbineHitbox = false
        iterateHitbox { base ->
            if (world.getBlockState(pos + base) == getReplaceBlock()) {
                world.setBlockToAir(pos + base)
            }
        }
        if (!centerBreaking) {
            container.sendUpdateToNearPlayers()
        }
    }

    fun updateWind() {
        if (world.isClient && hasTurbineHitbox) {
            rotationSpeed = openSpace * currentWind * 5f
            rotation += rotationSpeed
        }
        if (currentWind != nextWind) {
            val diff = nextWind - currentWind
            currentWind += diff * 0.01f
        }
    }

    fun updateNextWind() {
        val posNoise = windNoise.getValue(pos.xd / 16.0, pos.zd / 16.0)
        val timeNoise = windNoise.getValue(world.totalWorldTime.toDouble(), pos.yd)
        val noise = (posNoise + timeNoise).toFloat() // -2..2
        nextWind = noise * 0.25f + 0.5f
    }

    fun updateOpenSpace() {
        var total = 0
        var open = 0

        val facing = facingGetter()
        iterateHitbox { base ->
            var dist = 0
            for (k in 1..16) {
                val offset = facing.rotatePoint(point = BlockPos(0, 0, -k)) + base
                val worldPos = pos + offset
                val state = world.getBlockState(worldPos)

                if (!state.material.blocksMovement()) {
                    dist = k
                } else {
                    break
                }
            }
            open += dist
            total += 16
        }
        openSpace = open / total.toFloat()
    }

    inline fun iterateHitbox(func: (BlockPos) -> Unit) {
        val facing = facingCache
        for (i in -5..5) {
            for (j in -5..5) {
                val dist = i * i + j * j
                if (dist < 35) {
                    val offset = facing.rotatePoint(point = BlockPos(i, j, -1))
                    func(offset)
                }
            }
        }
    }

    override fun onBreak() {
        if (world.isServer) {
            removeTurbineHitbox(true)
        }
    }

    override fun serializeNBT(): NBTTagCompound = newNbt { add("hasTurbine", hasTurbineHitbox) }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        hasTurbineHitbox = nbt.getBoolean("hasTurbine")
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(production.toSyncVariable(DATA_ID_MACHINE_PRODUCTION))
    }
}