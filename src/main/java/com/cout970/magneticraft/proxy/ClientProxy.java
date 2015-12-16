package com.cout970.magneticraft.proxy;

import com.cout970.magneticraft.ManagerBlocks;
import com.cout970.magneticraft.client.tilerender.TileRenderCrushingTable;
import com.cout970.magneticraft.tileentity.TileCrushingTable;
import net.darkaqua.blacksmith.api.registry.StaticAccess;
import net.darkaqua.blacksmith.api.render.tileentity.ITileEntityRenderer;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;

/**
 * Created by cout970 on 06/12/2015.
 */
public class ClientProxy extends CommonProxy {

    public void init(){
        super.init();
        ManagerBlocks.initBlockRenders();
        register(TileCrushingTable.class, new TileRenderCrushingTable());
    }

    private void register(Class<? extends ITileEntityDefinition> tile, ITileEntityRenderer render) {
        StaticAccess.GAME.getRenderRegistry().registerTileEntityRenderer(tile, render);
    }
}
