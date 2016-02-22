package com.cout970.magneticraft.tileentity.multiblock;

import com.cout970.magneticraft.tileentity.TileBase;
import com.cout970.magneticraft.util.multiblock.IMultiBlockData;
import com.cout970.magneticraft.util.multiblock.MB_Tile;
import com.cout970.magneticraft.util.multiblock.MultiblockData;
import net.darkaqua.blacksmith.api.common.storage.IDataCompound;
import net.darkaqua.blacksmith.api.common.util.WorldRef;

/**
 * Created by cout970 on 15/01/2016.
 */
public class TileMultiblockBase extends TileBase implements MB_Tile {

    protected IMultiBlockData mbData;

    @Override
    public IMultiBlockData getMultiBlockData() {
        return mbData;
    }

    @Override
    public void onDestroy(WorldRef ref, IMultiBlockData data) {
        mbData = null;
    }

    @Override
    public void onActivate(WorldRef ref, IMultiBlockData data) {
        mbData = data;
    }

    @Override
    public void loadData(IDataCompound tag) {
        mbData = MultiblockData.load(tag);
    }

    @Override
    public void saveData(IDataCompound tag) {
        MultiblockData.save(tag, mbData);
    }
}
