package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.block.core.OnActivatedArgs
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileConveyorBelt
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.getList
import com.cout970.magneticraft.util.list
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/06/17.
 */
class ConveyorBeltModule(
        val facingGetter: () -> EnumFacing,
        override val name: String = "module_conveyor_belt"
) : IModule {

    override lateinit var container: IModuleContainer
    val facing get() = facingGetter()
    val boxes = mutableListOf<Box>()
    val bitmap = BitMap()

    override fun update() {
        val frontTile = world.getTile<TileConveyorBelt>(pos.offset(facing))
        val map = getSurroundMap()

        val limit = if (frontTile == null) 13f else 15f

        if (world.totalWorldTime % 20 == 0L) {
            bitmap.clear()
            boxes.forEach { FullBitMap(bitmap, map).mark(it.getHitBox()) }
        }

        boxes.forEach {
            if (it.lastTick != world.totalWorldTime) {
                if (it.position <= limit) {
                    val bitmap = FullBitMap(bitmap, map)

                    it.locked = checkCollision(bitmap, it)
                    if (!it.locked) {
                        var hitbox = it.getHitBox()
                        bitmap.unmark(hitbox)

                        it.position += if (it.route.isShort) 2f else 1f
                        hitbox = it.getHitBox()
                        bitmap.mark(hitbox)
                    }
                } else {
                    it.locked = true
                }
                it.lastTick = world.totalWorldTime
            }
        }
        if (frontTile != null) {
            boxes.removeAll {
                if (it.position <= limit) return@removeAll false
                val bitMap = FullBitMap(bitmap, map)
                bitMap.unmark(it.getHitBox())

                val res = frontTile.conveyorModule.addItem(it.item, facing,
                        it.route)
                if (!res) {
                    bitMap.mark(it.getHitBox())
                }
                res
            }
        }
    }

    fun getSurroundMap(): Map<BitmapLocation, IBitMap> {
        val map = mutableMapOf<BitmapLocation, IBitMap>()
        world.getTile<TileConveyorBelt>(pos.offset(facing.opposite))?.let {
            map += BitmapLocation.IN_BACK to it.conveyorModule.bitmap
        }
        world.getTile<TileConveyorBelt>(pos.offset(facing.rotateY()))?.let {
            map += BitmapLocation.IN_RIGHT to it.conveyorModule.bitmap
        }
        world.getTile<TileConveyorBelt>(pos.offset(facing.rotateYCCW()))?.let {
            map += BitmapLocation.IN_LEFT to it.conveyorModule.bitmap
        }
        world.getTile<TileConveyorBelt>(pos.offset(facing))?.let {
            when (it.facing) {
                facing -> map += BitmapLocation.OUT_FRONT to it.conveyorModule.bitmap
                facing.opposite -> map += BitmapLocation.OUT_BACK to it.conveyorModule.bitmap
                facing.rotateY() -> map += BitmapLocation.OUT_RIGHT to it.conveyorModule.bitmap
                facing.rotateYCCW() -> map += BitmapLocation.OUT_LEFT to it.conveyorModule.bitmap
                else -> Unit
            }
        }
        return map
    }

    private fun checkCollision(thisBitMap: IBitMap, box: Box): Boolean {
        val thisTemp = thisBitMap.copy()
        val hitbox = box.getHitBox()

        thisTemp.unmark(hitbox)

        box.position += if (box.route.isShort) 2f else 1f
        val newHitbox = box.getHitBox()

        val tempResult = !thisTemp.test(newHitbox)
        box.position -= if (box.route.isShort) 2f else 1f
        return tempResult
    }

    var flip = true
    fun addItem(stack: ItemStack): Boolean {
        val route = if (flip) {
            Route.LEFT_FORWARD
        } else {
            Route.RIGHT_FORWARD
        }
        flip = !flip
        val box = Box(stack.copy(), 2f, route, world.totalWorldTime)
        if (bitmap.test(box.getHitBox())) {
            boxes += box
            return true
        } else {
            return false
        }
    }

    fun addItem(stack: ItemStack, side: EnumFacing, oldRoute: Route): Boolean {
        val newRoute = getRoute(facing, side, oldRoute)
        val box = Box(stack.copy(), 0f, newRoute, world.totalWorldTime)
        if (bitmap.test(box.getHitBox())) {
            FullBitMap(bitmap, getSurroundMap()).mark(box.getHitBox())
            boxes += box
            return true
        } else {
            return false
        }
    }

    fun getRoute(facing: EnumFacing, side: EnumFacing, oldRoute: Route): Route {

        if (facing == side) {
            return if (!oldRoute.leftSide) Route.RIGHT_FORWARD else Route.LEFT_FORWARD
        }
        if (facing.opposite == side) {
            return if (oldRoute.leftSide) Route.RIGHT_FORWARD else Route.LEFT_FORWARD
        }
        if (facing.rotateY() == side) {
            return if (oldRoute.leftSide) Route.LEFT_SHORT else Route.LEFT_LONG
        }
        if (facing.rotateYCCW() == side) {
            return if (oldRoute.leftSide) Route.RIGHT_LONG else Route.RIGHT_SHORT
        }
        throw IllegalStateException("Illegal side: $side for facing: $facing")
    }


    fun removeItem(): ItemStack {
        if (boxes.isNotEmpty()) {
            return boxes.removeAt(boxes.lastIndex).item
        }
        return ItemStack.EMPTY
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        boxes.clear()
        boxes += nbt.getList("items")
                .map { it as NBTTagCompound }
                .map { Box(it) }
    }

    override fun serializeNBT(): NBTTagCompound {
        return newNbt {
            list("items") {
                boxes.map { it.serializeNBT() }.forEach { appendTag(it) }
            }
        }
    }

    interface IBitMap {
        operator fun get(x: Int, y: Int): Boolean

        operator fun set(x: Int, y: Int, value: Boolean)

        fun mark(box: AABB)
        fun mark(start: IVector2, end: IVector2)

        fun unmark(box: AABB)
        fun unmark(start: IVector2, end: IVector2)

        fun test(box: AABB): Boolean

        fun test(start: IVector2, end: IVector2): Boolean

        fun clear()
        fun copy(): IBitMap
    }

    enum class BitmapLocation {
        OUT_FRONT,
        OUT_BACK,
        OUT_LEFT,
        OUT_RIGHT,
        IN_BACK,
        IN_LEFT,
        IN_RIGHT;

        fun isInside(x: Int, y: Int): Boolean {
            return when (this) {
                ConveyorBeltModule.BitmapLocation.OUT_FRONT -> x in 0..15 && y in -1 downTo -16
                ConveyorBeltModule.BitmapLocation.OUT_BACK -> x in 0..15 && y in -1 downTo -16
                ConveyorBeltModule.BitmapLocation.OUT_LEFT -> x in 0..15 && y in -1 downTo -16
                ConveyorBeltModule.BitmapLocation.OUT_RIGHT -> x in 0..15 && y in -1 downTo -16
                ConveyorBeltModule.BitmapLocation.IN_BACK -> x in 0..15 && y in 16..31
                ConveyorBeltModule.BitmapLocation.IN_LEFT -> x in -1 downTo -16 && y in 0..15
                ConveyorBeltModule.BitmapLocation.IN_RIGHT -> x in 16..31 && y in 0..15
            }
        }

        fun transform(x: Int, y: Int): IVector2 {
            return when (this) {
                ConveyorBeltModule.BitmapLocation.OUT_FRONT -> vec2Of(x, y + 16)
                ConveyorBeltModule.BitmapLocation.OUT_BACK -> vec2Of(16 - x, (16 - y) + 16)
                ConveyorBeltModule.BitmapLocation.OUT_LEFT -> vec2Of(-(y + 1), x)
                ConveyorBeltModule.BitmapLocation.OUT_RIGHT -> vec2Of(16 + y, 16 - (x + 1))
                ConveyorBeltModule.BitmapLocation.IN_BACK -> vec2Of(x, y - 16)
                ConveyorBeltModule.BitmapLocation.IN_LEFT -> vec2Of(y, -(x + 1))
                ConveyorBeltModule.BitmapLocation.IN_RIGHT -> vec2Of(16 - (y + 1), x - 16)
            }
        }
    }

    class FullBitMap(val current: IBitMap, val others: Map<BitmapLocation, IBitMap>) : IBitMap {

        override fun get(x: Int, y: Int): Boolean {
            if (x in 0..15 && y in 0..15) return current[x, y]
            for ((loc, map) in others) {
                if (loc.isInside(x, y)) {
                    val pos = loc.transform(x, y)
                    return map[pos.xi, pos.yi]
                }
            }
            return false
        }

        override fun set(x: Int, y: Int, value: Boolean) {
            if (x in 0..15 && y in 0..15) {
                current[x, y] = value
                return
            }
            for ((loc, map) in others) {
                if (loc.isInside(x, y)) {
                    val pos = loc.transform(x, y)
                    map[pos.xi, pos.yi] = value
                    return
                }
            }
        }

        override fun mark(box: AABB) {
            mark(vec2Of(box.minX, box.minZ) * 16, vec2Of(box.maxX, box.maxZ) * 16)
        }

        override fun mark(start: IVector2, end: IVector2) {
            for (i in Math.floor(start.x).toInt() until Math.floor(end.x).toInt()) {
                for (j in Math.floor(start.y).toInt() until Math.floor(end.y).toInt()) {
                    this[i, j] = true
                }
            }
        }

        override fun unmark(box: AABB) {
            unmark(vec2Of(box.minX, box.minZ) * 16, vec2Of(box.maxX, box.maxZ) * 16)
        }

        override fun unmark(start: IVector2, end: IVector2) {
            for (i in Math.floor(start.x).toInt() until Math.floor(end.x).toInt()) {
                for (j in Math.floor(start.y).toInt() until Math.floor(end.y).toInt()) {
                    this[i, j] = false
                }
            }
        }

        override fun test(box: AABB): Boolean {
            return test(vec2Of(box.minX, box.minZ) * 16, vec2Of(box.maxX, box.maxZ) * 16)
        }

        override fun test(start: IVector2, end: IVector2): Boolean {
            for (i in Math.floor(start.x).toInt() until Math.floor(end.x).toInt()) {
                for (j in Math.floor(start.y).toInt() until Math.floor(end.y).toInt()) {
                    if (this[i, j]) return false
                }
            }
            return true
        }

        override fun clear() {
            current.clear()
            others.values.forEach { it.clear() }
        }

        override fun copy(): IBitMap {
            return FullBitMap(current.copy(), others.mapValues { it.value.copy() })
        }

        override fun toString(): String {
            return "FullBitMap(current=$current, others=$others)"
        }
    }

    open class BitMap(val map: BooleanArray = BooleanArray(16 * 16)) : IBitMap {

        override operator fun get(x: Int, y: Int): Boolean {
            val index = x + y * 16
            if (index < 0 || index >= 16 * 16) return false
            return map[index]
        }

        override operator fun set(x: Int, y: Int, value: Boolean) {
            val index = x + y * 16
            if (index < 0 || index >= 16 * 16) return
            map[index] = value
        }

        override fun mark(box: AABB) {
            mark(vec2Of(box.minX, box.minZ) * 16, vec2Of(box.maxX, box.maxZ) * 16)
        }

        override fun mark(start: IVector2, end: IVector2) {
            for (i in Math.floor(start.x).toInt() until Math.floor(end.x).toInt()) {
                for (j in Math.floor(start.y).toInt() until Math.floor(end.y).toInt()) {
                    this[i, j] = true
                }
            }
        }

        override fun unmark(box: AABB) {
            unmark(vec2Of(box.minX, box.minZ) * 16, vec2Of(box.maxX, box.maxZ) * 16)
        }

        override fun unmark(start: IVector2, end: IVector2) {
            for (i in Math.floor(start.x).toInt() until Math.floor(end.x).toInt()) {
                for (j in Math.floor(start.y).toInt() until Math.floor(end.y).toInt()) {
                    this[i, j] = false
                }
            }
        }

        override fun test(box: AABB): Boolean {
            return test(vec2Of(box.minX, box.minZ) * 16, vec2Of(box.maxX, box.maxZ) * 16)
        }

        override fun test(start: IVector2, end: IVector2): Boolean {
            for (i in Math.floor(start.x).toInt() until Math.floor(end.x).toInt()) {
                for (j in Math.floor(start.y).toInt() until Math.floor(end.y).toInt()) {
                    if (this[i, j]) return false
                }
            }
            return true
        }

        override fun clear() {
            for (i in 0 until 16 * 16) {
                map[i] = false
            }
        }

        override fun copy(): BitMap {
            return BitMap(map.clone())
        }

        override fun toString(): String {
            return buildString {
                append("BitMap(\n")
                for (i in 0 until 16) {
                    for (j in 0 until 16) {
                        if (this@BitMap[i, j]) {
                            append("#")
                        } else {
                            append("_")
                        }
                    }
                    append('\n')
                }
                append(")")
            }
        }
    }

    class Box(
            val item: ItemStack,
            var position: Float,
            val route: Route,
            var lastTick: Long,
            var locked: Boolean = false
    ) {

        constructor(nbt: NBTTagCompound) : this(
                ItemStack(nbt.getCompoundTag("items")),
                nbt.getFloat("position"),
                Route.values()[nbt.getInteger("route")],
                -1L
        )

        fun serializeNBT() = newNbt {
            add("item", item.serializeNBT())
            add("position", position)
            add("route", route.ordinal)
        }

        fun getPos(partialTicks: Float): IVector3 {
            val pos = position + if (locked) 0f else 0f//partialTicks
            if (route.isRect) {
                val z = Utilities.interpolate(16f, 0f, pos / 16.0f) * Utilities.PIXEL
                val x = when (route) {
                    ConveyorBeltModule.Route.LEFT_FORWARD -> 5 * Utilities.PIXEL
                    ConveyorBeltModule.Route.RIGHT_FORWARD -> 11 * Utilities.PIXEL
                    else -> 0.0
                }
                return vec3Of(x, 0, z)
            } else if (route.leftSide) {
                val x: Double
                val z: Double

                if (route.isShort) {
                    if (position < 8f) {
                        x = Utilities.interpolate(0f, 5f, pos / 8.0f) * Utilities.PIXEL
                        z = 5 * Utilities.PIXEL
                    } else {
                        x = 5 * Utilities.PIXEL
                        z = Utilities.interpolate(5f, 0f, (pos - 8f) / 8f) * Utilities.PIXEL
                    }
                } else {
                    if (position < 5f) {
                        x = Utilities.interpolate(0f, 5f, pos / 5.0f) * Utilities.PIXEL
                        z = 11 * Utilities.PIXEL
                    } else {
                        x = 5 * Utilities.PIXEL
                        z = Utilities.interpolate(11f, 0f, (pos - 5) / 11.0f) * Utilities.PIXEL
                    }
                }
                return vec3Of(x, 0, z)
            } else if (!route.leftSide) {
                val x: Double
                val z: Double

                if (route.isShort) {
                    if (position < 8f) {
                        x = Utilities.interpolate(16f, 11f, pos / 8f) * Utilities.PIXEL
                        z = 5 * Utilities.PIXEL
                    } else {
                        x = 11 * Utilities.PIXEL
                        z = Utilities.interpolate(5f, 0f, (pos - 8f) / 8f) * Utilities.PIXEL
                    }
                } else {
                    if (position < 5f) {
                        x = Utilities.interpolate(16f, 11f, pos / 5f) * Utilities.PIXEL
                        z = 11 * Utilities.PIXEL
                    } else {
                        x = 11 * Utilities.PIXEL
                        z = Utilities.interpolate(11f, 0f, (pos - 5f) / 11f) * Utilities.PIXEL
                    }
                }
                return vec3Of(x, 0, z)
            }
            return vec3Of(0, 0, 0)
        }

        fun getHitBox(): AABB {
            val pos = getPos(0f)
            return (pos - vec3Of(2, 0, 2) * Utilities.PIXEL) toAABBWith (pos + vec3Of(2, 4, 2) * Utilities.PIXEL)
        }
    }

    enum class Route(val leftSide: Boolean, val isRect: Boolean, val isShort: Boolean) {
        LEFT_FORWARD(true, true, false),
        RIGHT_FORWARD(false, true, false),
        LEFT_SHORT(true, false, true),
        LEFT_LONG(true, false, false),
        RIGHT_SHORT(false, false, true),
        RIGHT_LONG(false, false, false);

    }

    fun onClick(it: OnActivatedArgs): Boolean {
        if (it.heldItem.isNotEmpty) {
            addItem(it.heldItem)
        }
        return false
    }
}