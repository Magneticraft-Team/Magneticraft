package com.cout970.magneticraft.block

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import net.minecraft.block.material.Material


/**
 * Created by cout970 on 30/06/2016.
 */
open class BlockMultiState(material: Material, name: String, vararg variants: String) : BlockMod(name, material, *variants)
//quick 'n' dirty