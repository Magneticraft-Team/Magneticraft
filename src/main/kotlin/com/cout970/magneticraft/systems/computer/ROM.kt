package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.computer.IROM
import net.minecraft.util.ResourceLocation
import java.io.InputStream

/**
 * Created by cout970 on 2016/09/30.
 */
class ROM(val path: String) : IROM {

    constructor(res: ResourceLocation) : this("/assets/$MOD_ID/cpu/${res.resourcePath}")

    override fun getBIOS(): InputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(path)
        ?: error("Resource not found at location: $path")
}