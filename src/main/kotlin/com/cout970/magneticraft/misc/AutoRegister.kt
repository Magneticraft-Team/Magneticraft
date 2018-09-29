package com.cout970.magneticraft.misc

import kotlin.reflect.KClass

annotation class RegisterBlocks

annotation class RegisterRenderer(val tileEntity: KClass<*>)

annotation class RegisterTileEntity(val name: String)

annotation class RegisterContainer(val tile: KClass<*>)

annotation class RegisterGui(val container: KClass<*>)