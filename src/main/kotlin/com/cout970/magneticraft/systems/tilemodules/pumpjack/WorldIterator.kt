package com.cout970.magneticraft.systems.tilemodules.pumpjack

import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.getBlockPos
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.vector.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import kotlin.math.max
import kotlin.math.min

class WorldIterator(val start: BlockPos, val end: BlockPos, val inverted: Boolean = false) : Iterator<BlockPos> {

    companion object {
        fun create(a: BlockPos, b: BlockPos, inverted: Boolean = false): WorldIterator {
            return WorldIterator(
                BlockPos(min(a.x, b.x), max(0, min(a.y, b.y)), min(a.z, b.z)),
                BlockPos(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z)),
                inverted
            )
        }

        fun deserializeNBT(nbt: NBTTagCompound): WorldIterator? {
            return if (nbt.hasKey("start")) {
                create(nbt.getBlockPos("start"), nbt.getBlockPos("end"), nbt.getBoolean("inverted"))
                    .apply { current = nbt.getBlockPos("current") }
            } else null
        }
    }

    var current: BlockPos = if (inverted) end else start

    override fun hasNext(): Boolean = current != (if (inverted) start else end)

    fun reset() {
        current = if (inverted) end else start
    }

    override fun next(): BlockPos {
        if (current.xi < end.xi) {
            current = BlockPos(current.xi + 1, current.yi, current.zi)
        } else {
            if (current.zi < end.zi) {
                current = BlockPos(start.xi, current.yi, current.zi + 1)
            } else {
                if (inverted) {
                    if (current.yi > start.yi) {
                        current = BlockPos(start.xi, current.yi - 1, start.zi)
                    } else {
                        error("Iterator has no more elements")
                    }
                } else {
                    if (current.yi < end.yi) {
                        current = BlockPos(start.xi, current.yi + 1, start.zi)
                    } else {
                        error("Iterator has no more elements")
                    }
                }
            }
        }
        return current
    }

    fun totalBlocks(): Int {
        val totalArea = (end - start) + BlockPos(1, 1, 1)
        return totalArea.x * totalArea.y * totalArea.z
    }

    fun doneBlocks(): Int {
        val totalArea = (end - start) + BlockPos(1, 1, 1)

        val yLayers = if (!inverted) {
            (current.y - start.y) * (totalArea.x * totalArea.z)
        } else {
            (end.y - current.y) * (totalArea.x * totalArea.z)
        }

        val zLayers = (current.z - start.z) * totalArea.x
        val xLayers = (current.x - start.x)

        return xLayers + yLayers + zLayers
    }
}

fun WorldIterator?.serializeNBT() = newNbt {
    val iter = this@serializeNBT
    if (iter != null) {
        add("start", iter.start)
        add("end", iter.end)
        add("current", iter.current)
        add("inverted", iter.inverted)
    }
}