package com.cout970.magneticraft.misc.network

import com.cout970.magneticraft.misc.gui.ValueAverage

/**
 * Created by cout970 on 2017/07/01.
 */

abstract class SyncVariable(val id: Int) {

    abstract fun read(ibd: IBD)

    abstract fun write(ibd: IBD)
}

class FloatSyncVariable(id: Int, val getter: () -> Float, val setter: (Float) -> Unit) : SyncVariable(id) {
    override fun read(ibd: IBD) = ibd.getFloat(id, setter)
    override fun write(ibd: IBD) {
        ibd.setFloat(id, getter())
    }
}

class IntSyncVariable(id: Int, val getter: () -> Int, val setter: (Int) -> Unit) : SyncVariable(id) {
    override fun read(ibd: IBD) = ibd.getInteger(id, setter)
    override fun write(ibd: IBD) {
        ibd.setInteger(id, getter())
    }
}

class StringSyncVariable(id: Int, val getter: () -> String, val setter: (String) -> Unit) : SyncVariable(id) {
    override fun read(ibd: IBD) = ibd.getString(id, setter)
    override fun write(ibd: IBD) {
        ibd.setString(id, getter())
    }
}

class AverageSyncVariable(id: Int, val valueAverage: ValueAverage) : SyncVariable(id) {
    override fun read(ibd: IBD) = ibd.getFloat(id) { valueAverage.storage = it }
    override fun write(ibd: IBD) {
        ibd.setFloat(id, valueAverage.average)
    }
}