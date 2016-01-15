package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.TileWindTurbine;
import net.darkaqua.blacksmith.api.block.*;
import net.darkaqua.blacksmith.api.block.methods.BlockMethod;
import net.darkaqua.blacksmith.api.block.variants.BlockVariantCreatorFactory;
import net.darkaqua.blacksmith.api.block.variants.IBlockVariant;
import net.darkaqua.blacksmith.api.block.variants.IBlockVariantCreator;
import net.darkaqua.blacksmith.api.block.variants.IIProperty;
import net.darkaqua.blacksmith.api.entity.ILivingEntity;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.render.model.*;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleItemBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleItemModelProvider;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3d;
import net.darkaqua.blacksmith.api.util.WorldRef;
import net.darkaqua.blacksmith.api.world.IWorld;

/**
 * Created by cout970 on 03/01/2016.
 */
public class BlockWindTurbine extends BlockModeled implements BlockMethod.OnPlacedBy, IBlockContainerDefinition {

    public static IIProperty<Direction> DIRECTION = BlockVariantCreatorFactory.createPropertyEnum("direction", Direction.class);

    @Override
    public IBlockVariantCreator getBlockVariantCreator() {
        return BlockVariantCreatorFactory.createBlockVariantCreator(parent, DIRECTION);
    }

    @Override
    public String getBlockName() {
        return "wind_turbine";
    }

    @Override
    public void onPlacedBy(WorldRef ref, IBlockVariant state, ILivingEntity placer, IItemStack stack) {
        ref.setBlockVariant(state.withProperty(DIRECTION, Direction.SOUTH));
    }

    public IBlockModelProvider getModelProvider(){
        return new SimpleItemBlockModelProvider(ModelConstants.ofTechne(ModelConstants.WIND_TURBINE)){
            public IRenderModel fromModelPart(IModelPartIdentifier id){
                return new SimpleItemModelProvider.ItemModel(id){
                    @Override
                    public RenderTransformation getTransformation(RenderPlace place) {
                        if (place == RenderPlace.THIRD_PERSON) {
                            return new RenderTransformation(new Vect3d(0, 1, -3).multiply(1 / 16d), new Vect3d(-90, 0, 0), new Vect3d(0.55, 0.55, 0.55).multiply(0.5f));
                        } else if (place == RenderPlace.FIRST_PERSON) {
                            return new RenderTransformation(new Vect3d(0, 4, 2).multiply(1 / 16d), new Vect3d(0, -135, 25), new Vect3d(1.7, 1.7, 1.7).multiply(0.5f));
                        } else if(place == RenderPlace.GUI){
                            return new RenderTransformation(Vect3d.nullVector(), new Vect3d(90,0,0), new Vect3d(1,1,1).multiply(0.3f));
                        }
                        return new RenderTransformation(Vect3d.nullVector(), Vect3d.nullVector(), new Vect3d(1,1,1).multiply(0.5f));
                    }

                    @Override
                    public boolean needsInventoryRotation() {
                        return false;
                    }
                };
            }
        };
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld world, IBlockVariant state) {
        return new TileWindTurbine();
    }
}
