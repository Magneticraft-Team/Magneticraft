package com.cout970.magneticraft.misc

import com.cout970.magneticraft.Magneticraft

object Asm {

    fun getPrivateField(instance: Any, filed: String): Any? {
        return try {
            val f = instance.javaClass.getDeclaredField(filed)
            f.isAccessible = true
            f.get(instance)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    fun setPrivateField(instance: Any, filed: String, value: Any?) {
        try {
            val f = instance.javaClass.getDeclaredField(filed)
            f.isAccessible = true
            f.set(instance, value)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun findAnnotated(annotationClass: Class<*>): List<Pair<Class<*>, Map<String, Any>>> {
        return Magneticraft.asmData.annotations
            .filter { it.annotationType == annotationClass }
            .map { it.classType as Class<*> to it.annotationData }
    }

    fun <O> findAnnotatedObjects(annotationClass: Class<*>): List<O> {
        return Magneticraft.asmData.annotations
            .filter { it.annotationType == annotationClass }
            .map { it.classType as Class<*> }
            .map { it.kotlin.objectInstance as O }
    }

    fun <O : Any> forEachAnnotatedObjects(annotationClass: Class<*>, func: (O) -> Unit) {
        findAnnotatedObjects<O>(annotationClass).forEach { obj ->
            try {
                func(obj)
            } catch (e: Exception) {
                logError("Error auto-registering object: ${obj::class.java}")
                e.printStackTrace()
            }
        }
    }

    fun forEachAnnotatedClass(annotationClass: Class<*>, func: (Class<*>, Map<String, Any?>) -> Unit) {
        findAnnotated(annotationClass).forEach { (clazz, map) ->
            try {
                func(clazz, map)
            } catch (e: Exception) {
                logError("Error auto-registering class: ${clazz.canonicalName}")
                e.printStackTrace()
            }
        }
    }
}