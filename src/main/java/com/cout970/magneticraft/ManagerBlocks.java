package com.cout970.magneticraft;

import com.cout970.magneticraft.block.*;
import com.cout970.magneticraft.block.multiblock.BlockChassis;
import com.cout970.magneticraft.client.tilerender.TileRenderCrushingTable;
import com.cout970.magneticraft.client.tilerender.TileRenderHandCrank;
import com.cout970.magneticraft.client.tilerender.TileRenderWindTurbine;
import com.cout970.magneticraft.client.tilerender.TileRenderWoodenShaft;
import com.cout970.magneticraft.tileentity.TileCrushingTable;
import com.cout970.magneticraft.tileentity.TileTableSieve;
import com.cout970.magneticraft.tileentity.kinetic.TileKineticGrinder;
import com.cout970.magneticraft.tileentity.kinetic.TileWoodenShaft;
import com.cout970.magneticraft.tileentity.kinetic.generators.TileHandCrank;
import com.cout970.magneticraft.tileentity.kinetic.generators.TileWindTurbine;
import com.cout970.magneticraft.tileentity.multiblock.TileMultiBlockChassis;
import net.darkaqua.blacksmith.api.block.IBlock;
import net.darkaqua.blacksmith.api.creativetab.CreativeTabFactory;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.inventory.ItemStackFactory;
import net.darkaqua.blacksmith.api.registry.StaticAccess;
import net.darkaqua.blacksmith.api.render.tileentity.ITileEntityRenderer;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;

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
    KineticGrinder(new BlockKineticGrinder(), "Kinetic Grinder", TileKineticGrinder.class);

    private BlockBase definition;
    private IBlock block;
    private String identifier;
    private Class<? extends ITileEntityDefinition> tileEntityClass;
    private ITileEntityRenderer<? extends ITileEntityDefinition> tileRenderer;

    ManagerBlocks(BlockBase def, String englishName) {
        definition = def;
        identifier = def.getUnlocalizedName();
        def.registerName(englishName);
    }

    <T extends ITileEntityDefinition> ManagerBlocks(BlockBase def, String englishName, Class<T> tile) {
        this(def, englishName);
        tileEntityClass = tile;
    }

    <T extends ITileEntityDefinition> ManagerBlocks(BlockBase def, String englishName, Class<T> tile, ITileEntityRenderer<T> renderer) {
        this(def, englishName, tile);
        tileRenderer = renderer;
    }

    public static void initBlocks() {
        for (ManagerBlocks b : ManagerBlocks.values()) {
            b.block = StaticAccess.GAME.getBlockRegistry().registerBlockDefinition(b.definition, b.identifier);
            if (b.tileEntityClass != null) {
                StaticAccess.GAME.getTileEntityRegistry().registerTileEntityDefinition(b.tileEntityClass, b.identifier);
            }
        }
        BlockBase.MAIN_CREATIVE_TAB = CreativeTabFactory.createCreativeTab(Magneticraft.ID+"_main", ManagerBlocks.CrushingTable.toItemStack());
    }

    public static void initBlockRenders() {
        for (ManagerBlocks b : ManagerBlocks.values()) {
            StaticAccess.GAME.getRenderRegistry().registerBlockModelProvider(b.definition, b.definition.getModelProvider());
            if (b.tileRenderer != null) {
                StaticAccess.GAME.getRenderRegistry().registerTileEntityRenderer(b.tileEntityClass, b.tileRenderer);
            }
        }
    }

    public BlockBase getDefinition() {
        return definition;
    }

    public String getIdentifier() {
        return identifier;
    }

    public IBlock getBlock() {
        return block;
    }

    public IItemStack toItemStack() {
        return ItemStackFactory.createItemStack(getBlock());
    }
}
