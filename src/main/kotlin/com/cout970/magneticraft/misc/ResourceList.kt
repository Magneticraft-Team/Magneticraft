package com.cout970.magneticraft.misc

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.Magneticraft
import net.minecraft.util.ResourceLocation
import java.io.File
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Created by cout970 on 2017/08/25.
 */
object ResourceList {

    private fun getResourcesFromJarFile(file: File, pattern: Regex): List<String> {
        val returnVal = ArrayList<String>()
        val zf: ZipFile
        try {
            zf = ZipFile(file)
        } catch (e: IOException) {
            throw Error(e)
        }

        val e = zf.entries()
        while (e.hasMoreElements()) {
            val ze = e.nextElement() as ZipEntry
            val fileName = ze.name
            if (pattern.matches(fileName)) {
                returnVal.add(fileName)
            }
        }
        try {
            zf.close()
        } catch (e1: IOException) {
            throw Error(e1)
        }

        return returnVal
    }

    private fun getResourcesFromDirectory(directory: File, pattern: Regex): List<String> {
        val returnVal = ArrayList<String>()
        val fileList = directory.listFiles()!!
        for (file in fileList) {
            if (file.isDirectory) {
                returnVal.addAll(getResourcesFromDirectory(file, pattern))
            } else {
                try {
                    val fileName = file.canonicalPath
                    if (pattern.matches(fileName)) {
                        returnVal.add(fileName)
                    }
                } catch (e: IOException) {
                    throw Error(e)
                }
            }
        }
        return returnVal
    }

    fun getModAssets(): List<String> {
        val file = Magneticraft.sourceFile
        val pattern = """.*assets[/\\]magneticraft[/\\].*""".toRegex()
        return if (file.isDirectory) {
            getResourcesFromDirectory(file, pattern)
        } else {
            getResourcesFromJarFile(file, pattern)
        }
    }

    fun getGuideBookLanguages(): List<String> {
        val file = Magneticraft.sourceFile
        val pattern = """.*assets[/\\]magneticraft[/\\]guide[/\\].*""".toRegex()
        val files = if (file.isDirectory) {
            getResourcesFromDirectory(file, pattern)
        } else {
            getResourcesFromJarFile(file, pattern)
        }
        return files
            .map {
                it.replace(""".*assets[/\\]magneticraft[/\\]guide[/\\](.*)[/\\].*""".toRegex(), "$1")
            }
            .distinct()
    }

    fun getGuideBookPages(lang: String): List<ResourceLocation> {
        val file = Magneticraft.sourceFile
        val pattern = """.*assets[/\\]magneticraft[/\\]guide[/\\]$lang[/\\].*""".toRegex()
        val files = if (file.isDirectory) {
            getResourcesFromDirectory(file, pattern)
        } else {
            getResourcesFromJarFile(file, pattern)
        }
        return files.map {
            it.replace(""".*assets[/\\]magneticraft[/\\]guide[/\\]$lang[/\\](.*)""".toRegex(), "$1")
        }.map { name ->
            ResourceLocation(MOD_ID, "guide/$lang/$name")
        }
    }
}
