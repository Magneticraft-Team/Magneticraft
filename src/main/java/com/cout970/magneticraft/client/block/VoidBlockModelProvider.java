package com.cout970.magneticraft.client.block;

import com.google.common.collect.Lists;
import net.darkaqua.blacksmith.api.block.IBlockVariant;
import net.darkaqua.blacksmith.api.render.model.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.IModelIdentifier;
import net.darkaqua.blacksmith.api.render.model.IRenderModel;

import java.util.List;

/**
 * Created by cout970 on 16/12/2015.
 */
public class VoidBlockModelProvider implements IBlockModelProvider{

    @Override
    public IModelIdentifier getModelForVariant(IBlockVariant variant) {
        return null;
    }

    @Override
    public void bindModelIdentifier(IRenderModel model, IModelIdentifier identifier) { }

    @Override
    public List<IRenderModel> getAllModels() {
        return Lists.newArrayList();
    }
}
