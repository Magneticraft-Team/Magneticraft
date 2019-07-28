package com.cout970.magneticraft

import com.cout970.magneticraft.misc.vector.Vec2d
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.FMLModContainer
import net.minecraftforge.fml.common.ILanguageAdapter
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.relauncher.Side
import java.lang.reflect.Field
import java.lang.reflect.Method

// Mod metadata used by Forge
const val MOD_ID = "magneticraft"
const val MOD_NAME = "Magneticraft"
const val LANG_ADAPTER = "com.cout970.magneticraft.KotlinAdapter"

typealias AABB = AxisAlignedBB
typealias IVector3 = Vec3d
typealias IVector2 = Vec2d
typealias Sprite = TextureAtlasSprite

/**
 * This class allows to load the Mod class (Magneticraft.kt) into forge, this is needed because this
 * class is an object (Singleton)
 */
@Suppress("unused")
class KotlinAdapter : ILanguageAdapter {

    init {
        try {
            Class.forName("kotlin.jvm.internal.Intrinsics")
        } catch (error: NoClassDefFoundError) {
            FMLCommonHandler.instance().raiseException(error, "Mod Magneticraft requires the Kotlin standard library, please install Forgelin", true)
        }
    }

    override fun supportsStatics() = false

    override fun setProxy(target: Field?, proxyTarget: Class<*>?, proxy: Any?) {
        target?.set(null, proxy)
    }

    override fun getNewInstance(mod: FMLModContainer?, modClass: Class<*>?, loader: ClassLoader?, factory: Method?): Any? {
        return modClass?.getField("INSTANCE")?.get(null)
    }

    override fun setInternalProxies(mod: ModContainer?, side: Side?, loader: ClassLoader?) = Unit
}
