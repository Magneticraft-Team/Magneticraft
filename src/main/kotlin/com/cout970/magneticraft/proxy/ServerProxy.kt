package com.cout970.magneticraft.proxy

import net.minecraftforge.fml.relauncher.Side

/**
 * Created by cout970 on 16/07/2016.
 *
 * Class loaded by reflection
 */
@Suppress("unused")
class ServerProxy : CommonProxy() {

    override fun getSide() = Side.SERVER
}