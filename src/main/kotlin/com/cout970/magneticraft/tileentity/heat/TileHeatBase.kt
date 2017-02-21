package com.cout970.magneticraft.tileentity.heat

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.misc.tileentity.HeatHandler
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.MAX_EMISSION_TEMP
import com.cout970.magneticraft.util.MIN_EMISSION_TEMP
import com.cout970.magneticraft.util.interpolate
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable

/**
 * Created by cout970 on 04/07/2016.
 */
abstract class TileHeatBase : TileBase(), ITickable {

    abstract val heatNodes: List<IHeatNode>
    val heatHandler: HeatHandler = HeatHandler(this, heatNodes)
    override val traits: List<ITileTrait> = listOf(heatHandler)
    val lightLevelUpdateDelay = 20
    var lightLevelCache = 0.0f

    var firstTicks = -1

    override fun update() {

        if (shouldTick(lightLevelUpdateDelay)) {
            heatNodes.forEach {
                val lightLevel: Float = interpolate(it.temperature, MIN_EMISSION_TEMP, MAX_EMISSION_TEMP).toFloat()
                if (lightLevelCache != lightLevel) {
                    lightLevelCache = lightLevel
                    //world.getBlock<BlockBase>(pos)?.setLightLevel(lightLevel)
                    //sendUpdateToNearPlayers()
                }
            }
        }
        if (world.isServer) {
            //update to sync connections every 20 seconds
            if (shouldTick(400)) {
                heatHandler.updateHeatConnections()
                sendUpdateToNearPlayers()
            }
            // when the world is loaded the player take some ticks to
            // load so this wait for the player to send an update
            if (firstTicks > 0) {
                firstTicks--
                if (firstTicks % 20 == 0) {
                    sendUpdateToNearPlayers()
                }
            }
        }
    }

    override fun save(): NBTTagCompound = super.save().apply {
        setFloat("lightLevelCache", lightLevelCache)
    }

    override fun load(nbt: NBTTagCompound) {
        super.load(nbt)
        lightLevelCache = nbt.getFloat("lightLevelCache")
    }
}