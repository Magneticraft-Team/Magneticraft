package com.cout970.magneticraft.computer

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.computer.IROM
import net.minecraft.util.ResourceLocation
import java.io.InputStream

/**
 * Created by cout970 on 2016/09/30.
 */
class ROM(val res: ResourceLocation): IROM {
    override fun getBIOS(): InputStream = ROM::class.java.getResourceAsStream("/assets/${MOD_ID}/cpu/${res.resourcePath}")
}