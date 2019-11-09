package com.cout970.magneticraft

import com.cout970.magneticraft.misc.vector.Vec2d
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d

// Mod metadata used by Forge
const val MOD_ID = "magneticraft"

typealias AABB = AxisAlignedBB
typealias IVector3 = Vec3d
typealias IVector2 = Vec2d
typealias Sprite = TextureAtlasSprite
typealias TileType = TileEntityType<*>