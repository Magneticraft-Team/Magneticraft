package com.cout970.magneticraft.util.multiblock;

import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.api.block.IBlock;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.ObjectScanner;
import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.util.WorldRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleComponent implements MB_Component {

    public List<IBlock> blocks = new ArrayList<>();

    public SimpleComponent(IBlock b) {
        blocks.add(b);
    }

    public SimpleComponent(IBlock a, IBlock b) {
        blocks.add(a);
        blocks.add(b);
    }

    public SimpleComponent(IBlock... a) {
        Collections.addAll(blocks, a);
    }

    @Override
    public boolean matches(WorldRef ref, Vect3i rot, IMultiBlockData data) {
        Vect3i pos = data.getMultiBlock().translate(rot, data).add(ref.getPosition());
        IBlock block = ref.getWorld().getBlockData(pos).getBlock();
        MB_Block mb = ObjectScanner.findInBlock(block, MB_Block.class);
        if (mb == null)return false;
        if (!mb.matchesAny(new WorldRef(ref.getWorld(), pos), blocks)) {
            if (Magneticraft.DEBUG) {
                ref.getWorld().setBlockData(blocks.get(0).getDefaultBlockData(), pos);
            }
            return false;
        }
        return true;
    }

    @Override
    public void setup(WorldRef ref, Vect3i rot, IMultiBlockData data) {
        Vect3i pos = data.getMultiBlock().translate(rot, data).add(ref.getPosition());

        IBlock block = ref.getWorld().getBlockData(pos).getBlock();
        MB_Block mb_block = ObjectScanner.findInBlock(block, MB_Block.class);
        if (mb_block == null)return;
        mb_block.mutates(new WorldRef(ref.getWorld(), pos), data);

        ITileEntity tile = ref.getWorld().getTileEntity(pos);
        MB_Tile mb_tile = ObjectScanner.findInTileEntity(tile, MB_Tile.class);
        if (mb_tile == null)return;
        mb_tile.onActivate(new WorldRef(ref.getWorld(), pos), data);
    }

    @Override
    public void destroy(WorldRef ref, Vect3i rot, IMultiBlockData data) {
        Vect3i pos = data.getMultiBlock().translate(rot, data).add(ref.getPosition());

        IBlock block = ref.getWorld().getBlockData(pos).getBlock();
        MB_Block mb_block = ObjectScanner.findInBlock(block, MB_Block.class);
        if (mb_block == null)return;
        mb_block.destroy(new WorldRef(ref.getWorld(), pos), data);

        ITileEntity tile = ref.getWorld().getTileEntity(pos);
        MB_Tile mb_tile = ObjectScanner.findInTileEntity(tile, MB_Tile.class);
        if (mb_tile == null)return;
        mb_tile.onDestroy(new WorldRef(ref.getWorld(), pos), data);
    }

    @Override
    public String getErrorMessage(WorldRef ref, Vect3i rot, IMultiBlockData data) {
        Vect3i pos = data.getMultiBlock().translate(rot, data).add(ref.getPosition());
        return "Error in " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " with the block: " + blocks.get(0).getLocalizedName();
    }
}
