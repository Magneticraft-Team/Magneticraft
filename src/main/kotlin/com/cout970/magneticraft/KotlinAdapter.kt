package com.cout970.magneticraft

import net.minecraftforge.fml.common.FMLModContainer
import net.minecraftforge.fml.common.ILanguageAdapter
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.relauncher.Side
import java.lang.reflect.Field
import java.lang.reflect.Method

class KotlinAdapter : ILanguageAdapter {
    override fun supportsStatics() = false

    override fun setProxy(target: Field?, proxyTarget: Class<*>?, proxy: Any?) {
        target?.set(null, proxy)
    }

    override fun getNewInstance(mod: FMLModContainer?, modClass: Class<*>?, loader: ClassLoader?, factory: Method?) =
        modClass?.getField("INSTANCE")?.get(null)

    override fun setInternalProxies(mod: ModContainer?, side: Side?, loader: ClassLoader?) {
    }
}