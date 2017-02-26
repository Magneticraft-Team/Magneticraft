@file:Suppress("unused")

package com.cout970.magneticraft.misc.world

import net.minecraft.world.World

/**
 * Created by cout970 on 2017/02/20.
 */

inline val World.isServer: Boolean get() = !isRemote
inline val World.isClient: Boolean get() = isRemote