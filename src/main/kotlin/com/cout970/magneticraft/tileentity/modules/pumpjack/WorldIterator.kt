package com.cout970.magneticraft.tileentity.modules.pumpjack

import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.getBlockPos
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.xi
import com.cout970.magneticraft.util.vector.yi
import com.cout970.magneticraft.util.vector.zi
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import kotlin.math.max
import kotlin.math.min

class WorldIterator(val start: BlockPos, val end: BlockPos) : Iterator<BlockPos> {

    companion object {
        fun create(a: BlockPos, b: BlockPos): WorldIterator {
            return WorldIterator(
                    BlockPos(min(a.x, b.x), max(0, min(a.y, b.y)), min(a.z, b.z)),
                    BlockPos(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z))
            )
        }

        fun deserializeNBT(nbt: NBTTagCompound): WorldIterator? {
            return if (nbt.hasKey("start")) create(nbt.getBlockPos("start"), nbt.getBlockPos("end")) else null
        }
    }

    var current: BlockPos = start

    fun reset() {
        current = start
    }

    override fun hasNext(): Boolean = current != end

    override fun next(): BlockPos {
        if (current.xi < end.xi) {
            current = BlockPos(current.xi + 1, current.yi, current.zi)
        } else {
            if (current.zi < end.zi) {
                current = BlockPos(start.xi, current.yi, current.zi + 1)
            } else {
                if (current.yi < end.yi) {
                    current = BlockPos(start.xi, current.yi + 1, start.zi)
                } else {
                    error("Iterator has no more elements")
                }
            }
        }
        return current
    }
}

fun WorldIterator?.serializeNBT() = newNbt {
    val iter = this@serializeNBT
    if (iter != null) {
        add("start", iter.start)
        add("end", iter.end)
    }
}