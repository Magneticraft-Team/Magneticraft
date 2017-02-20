@file:Suppress("unused")

package com.cout970.magneticraft.misc.world

import net.minecraft.world.World

/**
 * Created by cout970 on 2017/02/20.
 */

val World.isServer: Boolean get() = !isRemote
val World.isClient: Boolean get() = isRemote