package com.cout970.magneticraft.util;

import net.darkaqua.blacksmith.api.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.registry.IModelRegistry;
import net.darkaqua.blacksmith.api.render.model.IModelPart;
import net.darkaqua.blacksmith.api.render.model.IStaticModel;
import net.darkaqua.blacksmith.api.render.model.defaults.EmptyModel;
import net.darkaqua.blacksmith.api.render.model.providers.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.providers.defaults.SimpleBlockModelProvider;

/**
 * Created by cout970 on 30/01/2016.
 */
public class ItemOnlyModelProvider implements IBlockModelProvider {

    protected IModelPart component;
    protected IStaticModel model;
    protected IStaticModel emptyModel;

    public ItemOnlyModelProvider(IModelPart component) {
        this.component = component;
    }

    @Override
    public IStaticModel getModelForBlockData(IBlockData variant) {
        return emptyModel;
    }

    @Override
    public IStaticModel getModelForItemBlock(IItemStack stack) {
        return model;
    }

    @Override
    public void registerModels(IModelRegistry registry) {
        model = new SimpleBlockModelProvider.BlockModel(registry.registerModelPart(component));
        emptyModel = new EmptyModel();
    }
}
