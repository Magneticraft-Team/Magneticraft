package com.cout970.magneticraft

import net.minecraft.block.BlockState
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.state.EnumProperty
import net.minecraft.state.IProperty
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.shapes.IBooleanFunction
import net.minecraft.util.math.shapes.VoxelShapes
import net.minecraft.world.IBlockReader
import net.minecraft.world.World


// Port mappings
typealias EnumFacing = Direction

typealias NBTTagCompound = CompoundNBT
typealias NBTTagList = ListNBT
typealias EntityPlayer = PlayerEntity
typealias IBlockState = BlockState
typealias PropertyEnum<T> = EnumProperty<T>
typealias ItemBlock = BlockItem
typealias IBlockAccess = IBlockReader
typealias TextureMap = AtlasTexture


var ItemStack.tagCompound: NBTTagCompound?
    get() = this.tag
    set(it) {
        this.tag = it
    }

fun NBTTagCompound.hasKey(key: String) = this.contains(key)
fun NBTTagCompound.getCompoundTag(key: String): CompoundNBT = this.getCompound(key)
fun NBTTagCompound.getInteger(key: String): Int = this.getInt(key)

val World.totalWorldTime: Long get() = gameTime

fun BlockState.isSideSolid(world: IBlockReader, pos: BlockPos, side: Direction): Boolean {
    val shape = getCollisionShape(world, pos).project(side)
    return !VoxelShapes.compare(shape, VoxelShapes.fullCube(), IBooleanFunction.ONLY_SECOND)
}

fun <T : Comparable<T>, V : T> BlockState.withProperty(prop: IProperty<T>, value: V): BlockState = with(prop, value)