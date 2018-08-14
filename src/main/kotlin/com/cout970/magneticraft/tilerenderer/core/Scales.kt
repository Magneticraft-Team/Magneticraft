package com.cout970.magneticraft.tilerenderer.core

/**
 * Created by cout970 on 2017/08/29.
 */


inline val Number.px get() = toDouble() * PIXEL

inline val Double.f get() = toFloat()
inline val Float.d get() = toDouble()