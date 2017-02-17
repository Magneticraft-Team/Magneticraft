package com.cout970.magneticraft.misc.render

/**
 * Created by cout970 on 2016/12/25.
 */
open class CacheNode<K, V>(
        key: K,
        var compute: (K) -> V,
        var equals: (K, K) -> Boolean = { a, b -> a == b },
        var reset: (K, V) -> Unit = { k, v -> Unit }
) {

    private var value: V? = null
    private var key: K = key
        set(i) {
            reset()
            field = i
        }

    fun getOrChange(key: K): V{
        if (!equals(this.key, key)){
            this.key = key
        }
        return getOrCompute()
    }

    fun get(): V = value!!

    fun getOrNull(): V? = value

    fun getOrCompute(): V {
        if (value == null) {
            value = compute(key)
        }
        return value!!
    }

    fun reset() {
        if (value != null) {
            reset(key, value!!)
            value = null
        }
    }
}