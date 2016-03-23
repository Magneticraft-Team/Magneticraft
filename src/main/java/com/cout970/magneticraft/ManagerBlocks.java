package com.cout970.magneticraft;

import com.cout970.magneticraft.block.*;
import com.cout970.magneticraft.block.multiblock.BlockChassis;
import com.cout970.magneticraft.client.tilerender.*;
import com.cout970.magneticraft.tileentity.TileCrushingTable;
import com.cout970.magneticraft.tileentity.TileTableSieve;
import com.cout970.magneticraft.tileentity.kinetic.TileKineticGrinder;
import com.cout970.magneticraft.tileentity.kinetic.TileOreWasher;
import com.cout970.magneticraft.tileentity.kinetic.TileWoodenShaft;
import com.cout970.magneticraft.tileentity.kinetic.generators.TileHandCrank;
import com.cout970.magneticraft.tileentity.kinetic.generators.TileWindTurbine;
import com.cout970.magneticraft.tileentity.multiblock.TileMultiBlockChassis;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by cout970 on 16/12/2015.
 */
public enum ManagerBlocks {

    CopperOre(new BlockOreBase("oreCopper"), "Copper Ore"),
    TungstenOre(new BlockOreBase("oreTungsten"), "Tungsten Ore"),
    CrushingTable(new BlockCrushingTable(), "Crushing Table", TileCrushingTable.class, new TileRenderCrushingTable()),
    TableSieve(new BlockTableSieve(), "Table Sieve", TileTableSieve.class),
    WoodenShaft(new BlockWoodenShaft(), "Wooden Shaft", TileWoodenShaft.class, new TileRenderWoodenShaft()),
    HandCrank(new BlockHandCrank(), "Hand Crank", TileHandCrank.class, new TileRenderHandCrank()),
    WindTurbine(new BlockWindTurbine(), "Wind Turbine", TileWindTurbine.class, new TileRenderWindTurbine()),
    Chassis(new BlockChassis(), "MultiBlock Chassis", TileMultiBlockChassis.class ),
    Limestone(new BlockLimestone(), "Limestone"),
    KineticGrinder(new BlockKineticGrinder(), "Kinetic Grinder", TileKineticGrinder.class, new TileRenderKineticGrinder()),
    OreWasher(new BlockOreWasher(), "Ore Washer", TileOreWasher.class, new TileRenderOreWasher());

    private BlockBase block;
    private String identifier;
    private Class<? extends TileEntity> tileEntityClass;
    private TileRenderer<? extends TileEntity> tileRenderer;

    ManagerBlocks(BlockBase def, String englishName) {
        block = def;
        identifier = def.getUnlocalizedName();
        def.registerName(englishName);
    }

    <T extends TileEntity> ManagerBlocks(BlockBase def, String englishName, Class<T> tile) {
        this(def, englishName);
        tileEntityClass = tile;
    }

    <T extends TileEntity> ManagerBlocks(BlockBase def, String englishName, Class<T> tile, TileRenderer<T> renderer) {
        this(def, englishName, tile);
        tileRenderer = renderer;
    }

    public static void initBlocks() {
        for (ManagerBlocks b : ManagerBlocks.values()) {
            GameRegistry.registerBlock(b.block, b.identifier);
            if (b.tileEntityClass != null) {
                GameRegistry.registerTileEntity(b.tileEntityClass, b.identifier);
            }
        }
    }

    public static void initBlockRenders() {
        for (ManagerBlocks b : ManagerBlocks.values()) {
            ManagerRender.INSTANCE.registerBlockModelProvider(b.block, b.block.getModelProvider());
            if (b.tileRenderer != null) {
                ClientRegistry.bindTileEntitySpecialRenderer(b.tileEntityClass, (TileRenderer<? super TileEntity>) b.tileRenderer);
                b.tileRenderer.initModels();
            }
        }
    }

    public static void reloadModels(){
        for (ManagerBlocks b : ManagerBlocks.values()) {
            if (b.tileRenderer != null) {
                b.tileRenderer.initModels();
            }
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public BlockBase getBlock() {
        return block;
    }

    public ItemStack toItemStack() {
        return new ItemStack(getBlock());
    }
}
