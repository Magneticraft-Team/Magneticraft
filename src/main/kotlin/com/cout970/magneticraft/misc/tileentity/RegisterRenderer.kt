package com.cout970.magneticraft.misc.tileentity

import kotlin.reflect.KClass

/**
 * Created by cout970 on 2017/07/02.
 */
annotation class RegisterRenderer(val tileEntity: KClass<*>)

annotation class RegisterTileEntity(val name: String)