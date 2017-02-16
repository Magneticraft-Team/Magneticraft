package com.cout970.magneticraft.util

import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import io.netty.buffer.ByteBuf
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import java.util.*

/**
 * Created by cout970 on 29/06/2016.
 */

fun TileEntity.shouldTick(time: Int): Boolean {
    return (world.totalWorldTime + pos.hashCode()) % time == 0L
}

operator fun <T : Comparable<T>> IProperty<T>.get(state: IBlockState): T = state.getValue(this)
operator fun <T : Comparable<T>> IBlockState.get(property: IProperty<T>): T = getValue(property)

fun <T : Comparable<T>> IProperty<T>.isIn(state: IBlockState): Boolean = this in state.properties

operator fun IElectricConnection.contains(node: IElectricNode) = this.firstNode == node || this.secondNode == node

fun ByteBuf.readString(): String {
    val size = Math.abs(this.readShort().toInt())
    val buffer = ByteArray(size)
    for (i in 0 until size) {
        buffer[i] = this.readByte()
    }
    return kotlin.text.String(buffer, charset = Charsets.UTF_8)
}

fun ByteBuf.writeString(str: String) {
    val array = str.toByteArray(Charsets.UTF_8)
    this.writeShort(array.size)
    for (i in 0 until array.size) {
        this.writeByte(array[i].toInt())
    }
}

fun ByteBuf.readByteArray(): ByteArray {
    val size = Math.abs(this.readShort().toInt())
    val buffer = ByteArray(size)
    for (i in 0 until size) {
        buffer[i] = this.readByte()
    }
    return buffer
}

fun ByteBuf.writeByteArray(array: ByteArray) {
    this.writeShort(array.size)
    for (i in 0 until array.size) {
        this.writeByte(array[i].toInt())
    }
}

fun ByteBuf.writeUUID(uuid: UUID) {
    this.writeLong(uuid.mostSignificantBits)
    this.writeLong(uuid.leastSignificantBits)
}

fun ByteBuf.readUUID(): UUID {
    return UUID(this.readLong(), this.readLong())
}

fun EntityPlayer.sendMessage(str: String, vararg args: Any) {
    addChatComponentMessage(TextComponentTranslation(str, *args))
}

fun EntityPlayer.sendMessage(str: String, vararg args: Any, color: TextFormatting) {
    addChatComponentMessage(TextComponentTranslation(str, *args).apply { style.color = color })
}

fun translate(str: String, vararg args: Any) = TextComponentTranslation(str, *args)

val World.isServer: Boolean get() = !isRemote
val World.isClient: Boolean get() = isRemote

fun AxisAlignedBB.cut(other: AxisAlignedBB): AxisAlignedBB? {
    if (!this.intersectsWith(other)) return null
    return AxisAlignedBB(
            Math.max(minX, other.minX), Math.max(minY, other.minY), Math.max(minZ, other.minZ),
            Math.min(maxX, other.maxX), Math.min(maxY, other.maxY), Math.min(maxZ, other.maxZ))
}

val EMPTY_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

/**
 * Gets all slots range is a part of player inventory.
 */
fun Container.getPlayerSlotRanges(player: EntityPlayer) : List<IntRange> {
    return this.getSlotRanges { it.inventory is InventoryPlayer && it.inventory == player.inventory }
}

/**
 * Get all slots range that is not a part of player inventory.
 */
fun Container.getNonPlayerSlotRanges() : List<IntRange> {
    return this.getSlotRanges { it.inventory !is InventoryPlayer }
}

/**
 * Get all slots filtering with [predicate]
 */
fun Container.getSlotRanges(predicate: (Slot) -> Boolean): List<IntRange> {
    val ranges = mutableListOf<IntRange>()
    val size = this.inventorySlots.size

    var start: Int? = null

    this.inventorySlots.forEachIndexed { i, slot ->
        if(predicate(slot)) {
            if(start == null)
                start = i
        } else if(start != null) {
            ranges += start!!..i
            start = null
        }
    }

    if(start != null)
        ranges += start!!..size-1

    return ranges
}