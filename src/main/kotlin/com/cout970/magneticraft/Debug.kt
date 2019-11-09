package com.cout970.magneticraft

import com.cout970.magneticraft.misc.Asm
import com.cout970.magneticraft.misc.fluid.isNotEmpty
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.toTextComponent
import com.cout970.magneticraft.misc.vector.toBlockPos
import com.cout970.magneticraft.systems.gui.render.isKeyPressed
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipeType
import net.minecraft.util.Hand
import net.minecraft.util.Timer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.server.ServerChunkProvider
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fml.client.ClientHooks
import org.lwjgl.glfw.GLFW
import java.io.File

@Suppress("unused")
/**
 * Created by cout970 on 11/06/2016.
 *
 * Stuff that only works in the dev-environment
 */
object Debug {

    var DEBUG = false
    var srcDir: File? = null

    fun init() {
        val configDir = TODO()
        srcDir = searchSourceDir(configDir)
        DEBUG = srcDir != null
    }

    fun searchSourceDir(configDir: File): File? {
        var temp: File? = configDir
        while (temp != null && temp.isDirectory) {
            if (File(temp, "build.gradle").exists()) {
                return temp
            }
            temp = temp.parentFile
        }
        return temp
    }

//    var cache = emptyList<IRenderCache>()
//    var last = -1L

//    fun getOrLoad(loc: ResourceLocation = resource("models/block/mcx/test.mcx")): List<ModelCache> {
//        val locLasModified = lastModified(loc)
//        if (last != locLasModified) {
//            try {
//                cache = loadModel(loc)
//                last = locLasModified
//            } catch (e: Exception) {
//            }
//        }
//        return cache
//    }
//
//    fun lastModified(loc: ResourceLocation): Long {
//        val path = "assets/${loc.resourceDomain}/${loc.resourcePath}"
//
//        val url = Thread.currentThread().contextClassLoader.getResource(path)
//        return File(url.file).lastModified()
//    }
//
//    fun loadModel(loc: ResourceLocation): List<ModelCache> {
//        val resourceManager = Minecraft.getMinecraft().resourceManager
//        val res = resourceManager.getResource(loc)
////        Thread.sleep(100)
//        val data = ModelSerializer.load(res.inputStream)
//        println("loading")
//
//        val textureGrouped = data.parts.groupBy { it.texture }
//        return textureGrouped.map {
//            ModelCache {
//                ModelUtilties.renderModelParts(data, it.value)
//            }.apply { texture = it.key.addPrefix("textures/").addPostfix(".png") }
//        }
//    }

    fun printBlocksWithoutRecipe() {

        val allBlocks = RegistryEvents.blocks.toMutableSet()
        val allItems = RegistryEvents.items.toMutableSet()

        val server = Minecraft.getInstance().integratedServer ?: return
        val craftingRecipes = server.recipeManager.recipes.filter { it.type == IRecipeType.CRAFTING }
        craftingRecipes.forEach { value ->
            val stack = value.recipeOutput
            if (stack.isEmpty) return@forEach

            val item = stack.item

            // check that the recipe doesn't use ore dictionary entries that are empty
            // TODO
//            val valid = it.ingredients.all { ing ->
//                (ing as? OreIngredient)?.matchingStacks?.isNotEmpty() ?: true
//            }
            val valid = true

            if (valid) {
                allItems.remove(item)
            } else if (item in allItems) {
                println("Invalid recipe for: ${item.registryName}")
            }

            if (item is BlockItem) {
                if (valid) {
                    allBlocks.remove(item.block)
                } else if (item.block in allBlocks) {
                    println("Invalid recipe for: ${item.registryName}")
                }
            }
        }

        if (allItems.isNotEmpty()) {
            println("==========================================")
            println("Items without crafting recipe: ")
            allItems.forEach { println("- $it") }
            println("==========================================")
        }

        if (allBlocks.isNotEmpty()) {
            println("==========================================")
            println("Blocks without crafting recipe: ")
            allBlocks.forEach { println("- $it") }
            println("==========================================")
        }
    }

    // To use this, add breakpoint in
    // net.minecraft.inventory.ContainerWorkbench#onCraftMatrixChanged
    // and set the breakpoint condition to:
    // "Debug.INSTANCE.createRecipe(craftMatrix, player)"
    fun createRecipe(inv: CraftingInventory, player: EntityPlayer): Boolean {

        try {
            val chars = listOf(
                "A", "B", "C",
                "D", "E", "F",
                "G", "H", "I"
            )

            val map = mutableMapOf<JsonObject, String>()
            val slots = Array(9) { " " }

            for (y in 0 until inv.height) {
                for (x in 0 until inv.width) {

                    val stack = inv.getStackInSlot(x + y * 3)

                    if (stack.isNotEmpty) {
                        val itemJson = deserializeToJson(stack)
                        val char = map.getOrPut(itemJson) { chars[map.size] }
                        slots[x + y * 3] = char
                    }
                }
            }

            val pattern = JsonArray().apply {
                var start = 0 to 0
                var end = 2 to 2
                if (slots[0].isBlank() && slots[3].isBlank() && slots[6].isBlank()) {
                    //ignore first column
                    start = start.first + 1 to start.second
                }
                if (slots[2].isBlank() && slots[5].isBlank() && slots[8].isBlank()) {
                    //ignore last column
                    end = end.first - 1 to end.second
                }
                if (slots[0].isBlank() && slots[1].isBlank() && slots[2].isBlank()) {
                    //ignore first row
                    start = start.first to start.second + 1
                }
                if (slots[6].isBlank() && slots[7].isBlank() && slots[8].isBlank()) {
                    //ignore last row
                    end = end.first to end.second - 1
                }

                for (y in start.second..end.second) {
                    var thisLine = ""
                    for (x in start.first..end.first) {
                        thisLine += slots[x + y * 3]
                    }
                    add(thisLine)
                }
            }

            val key = JsonObject().also { keyObj ->
                map.forEach { stack, itemKey ->
                    keyObj.add(itemKey, stack)
                }
            }

            val handItem = player.getHeldItem(Hand.MAIN_HAND)

            val obj = JsonObject()
            obj.addProperty("type", "forge:ore_shaped")
            obj.add("pattern", pattern)
            obj.add("key", key)
            obj.add("result", deserializeToJson2(handItem))

            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonStr = gson.toJson(obj)

            if (isKeyPressed(GLFW.GLFW_KEY_C)) {
                val folder = File(srcDir, "src/main/resources/assets/magneticraft/recipes")
                val fileName = handItem.translationKey.replace(".name", "").replaceBeforeLast(".", "").substring(1)
                val file = File(folder, "$fileName.json")

                file.writeText(jsonStr)
                println("saved: ${file.exists()}, path: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun deserializeToJson2(stack: ItemStack) = JsonObject().apply {
        if (stack.isEmpty) return@apply

        addProperty("item", stack.item.registryName.toString())
        addProperty("count", stack.count)

//        if (stack.hasSubtypes) {
//            addProperty("data", stack.itemDamage)
//        }
        if (stack.tagCompound != null) {
            addProperty("nbt", stack.tagCompound.toString())
        }
    }

    fun deserializeToJson(stack: ItemStack) = JsonObject().run {
        if (stack.isEmpty) return this
//        val ids = OreDictionary.getOreIDs(stack)
//
//        if (ids.isNotEmpty()) {
//            val name = OreDictionary.getOreName(ids.first())
//            addProperty("type", "forge:ore_dict")
//            addProperty("ore", name)
//            addProperty("count", stack.count)
//            this
//        } else {
        deserializeToJson2(stack)
//        }
    }

    //useful function to change the amount of tick per second used in minecraft
    fun setTicksPerSecond(tps: Int) {
        val timer = Asm.getPrivateField(Minecraft.getInstance(), "timer") as Timer
        Asm.setPrivateField(timer, "tickLength", 1000.0f / tps.toFloat())
    }

    object MgCommand {

        fun register(dispatcher: CommandDispatcher<CommandSource>) {
            dispatcher.register(
                literal<CommandSource>("mg")
                    .executes {
                        it.source.sendFeedback("Magneticraft debug command, is you see this please report to the mod author".toTextComponent(), true)
                        Command.SINGLE_SUCCESS
                    }
                    .then(literal("gen")).executes {
                        regenTerrain(it.source, it.source.world, it.source.pos.toBlockPos())
                        Command.SINGLE_SUCCESS
                    }
                    .then(
                        literal<CommandSource>("ticks")
                            .then(Commands.argument("ticks", IntegerArgumentType.integer(20)))
                            .executes {
                                setTicksPerSecond(IntegerArgumentType.getInteger(it, "ticks"))
                                Command.SINGLE_SUCCESS
                            }
                    )
                    .then(literal("info")).executes {
                        setTicksPerSecond(IntegerArgumentType.getInteger(it, "ticks"))
                        Command.SINGLE_SUCCESS
                    }
                    .then(literal("reload")).executes {
                        reload()
                        Command.SINGLE_SUCCESS
                    }
            )
        }

        private fun reload() {
            Minecraft.getInstance().deferTask {
                ug()
                try {
                    @Suppress("DEPRECATION")
                    Minecraft.getInstance().reloadResources()
                    val list = Minecraft.getInstance().saveLoader.saveList
                    list.sort()
                    ClientHooks.tryLoadExistingWorld(null, list[0])
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun info(sender: CommandSource) {
            val stack = sender.asPlayer().inventory.getCurrentItem()
            val fluid = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY)

            sender.sendFeedback("Item: $stack <${stack.item.registryName}:${stack.damage}>".toTextComponent(), true)
            sender.sendFeedback("NBT: ${stack.tag}".toTextComponent(), true)
            if (fluid.isNotEmpty) {
                sender.sendFeedback("FluidStack: ${fluid.fluid.registryName}, ${fluid.amount}, ${fluid.tag}".toTextComponent(), true)
            }
        }

        private fun regenTerrain(sender: CommandSource, world: World, pos: BlockPos) {
            val prov = world.chunkProvider as ServerChunkProvider

            for (i in -5..5) {
                for (j in -5..5) {
                    val chunkX = i + (pos.x shr 4)
                    val chunkZ = j + (pos.z shr 4)

                    prov.getChunk(chunkX, chunkZ, false)?.let { chunk ->
                        sender.sendFeedback("Regenerating chunk: ($chunkX, $chunkZ)".toTextComponent(), true)
                        // TODO deal with new chunk generation
                    }
                }
            }
        }
    }
}

val ug get() = ug()

fun ug(): Boolean {
    Minecraft.getInstance().mouseHelper.ungrabMouse()
    return true
}