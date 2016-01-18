package com.cout970.magneticraft.block;

import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.api.render.model.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleModelPartBlock;
import net.darkaqua.blacksmith.api.util.ResourceReference;

/**
 * Created by cout970 on 18/12/2015.
 */
public class BlockOreBase extends BlockBase {

    private String oreDictName;

    public BlockOreBase(String oreDictName) {
        this.oreDictName = oreDictName;
    }

    @Override
    public String getBlockName() {
        return oreDictName;
    }

    public IBlockModelProvider getModelProvider() {
        return new SimpleBlockModelProvider(new SimpleModelPartBlock(new ResourceReference(Magneticraft.ID, "blocks/" + getBlockName().toLowerCase())));
    }
}
