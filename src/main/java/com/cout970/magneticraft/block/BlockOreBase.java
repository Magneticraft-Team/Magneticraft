package com.cout970.magneticraft.block;

import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.api.client.render.block.IBlockModelProvider;
import net.darkaqua.blacksmith.api.client.render.block.defaults.SimpleBlockModelProvider;
import net.darkaqua.blacksmith.api.client.render.model.defaults.SimpleModelPartBlock;
import net.darkaqua.blacksmith.api.common.util.ResourceReference;

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
        return new SimpleBlockModelProvider(iModelRegistry -> new SimpleBlockModelProvider.BlockModel(
                iModelRegistry.registerModelPart(Magneticraft.IDENTIFIER,
                        new SimpleModelPartBlock(
                                new ResourceReference(Magneticraft.ID, "blocks/" + getBlockName().toLowerCase())))));
    }
}
