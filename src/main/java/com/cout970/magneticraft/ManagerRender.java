package com.cout970.magneticraft;

import net.darkaqua.blacksmith.render.internal.BSModelFactory;
import net.darkaqua.blacksmith.render.providers.IBlockModelProvider;
import net.darkaqua.blacksmith.render.providers.IItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by cout970 on 19/03/2016.
 */
public class ManagerRender {

    public static final ManagerRender INSTANCE = new ManagerRender();

    private Map<Block, IBlockModelProvider> blockModelProviders = new HashMap<>();
    private Map<Item, IItemModelProvider> itemModelProviders = new HashMap<>();
    private BakedModelWrapper wrapper = new BakedModelWrapper();

    private StateMapperBase mapper = new StateMapperBase() {
        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return new ModelResourceLocation(Magneticraft.ID + ":block", "normal");
        }
    };
    private ItemMeshDefinition itemMeshDefinition = new ItemMeshDefinition() {
        @Override
        public ModelResourceLocation getModelLocation(ItemStack stack) {
            return new ModelResourceLocation(Magneticraft.ID + ":item", "inventory");
        }
    };

    private ManagerRender() {}

    public static IStateMapper getStateMapper() {
        return INSTANCE.mapper;
    }

    public static ItemMeshDefinition getItemMeshDefinition() {
        return INSTANCE.itemMeshDefinition;
    }

    public void registerBlockModelProvider(Block b, IBlockModelProvider p) {
        ModelLoader.setCustomStateMapper(b, ManagerRender.getStateMapper());
        blockModelProviders.put(b, p);
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(b), ManagerRender.getItemMeshDefinition());
        itemModelProviders.put(Item.getItemFromBlock(b), p);
    }

    public void registerItemModelProvider(Item b, IItemModelProvider p) {
        ModelLoader.setCustomMeshDefinition(b, ManagerRender.getItemMeshDefinition());
        itemModelProviders.put(b, p);
    }

    @SubscribeEvent
    public void onBakedEvent(ModelBakeEvent event) {
        blockModelProviders.values().forEach(IBlockModelProvider::reloadModels);
        itemModelProviders.values().forEach(IItemModelProvider::reloadModels);
        event.modelRegistry.putObject(new ModelResourceLocation(Magneticraft.ID + ":block", "normal"), wrapper);
        event.modelRegistry.putObject(new ModelResourceLocation(Magneticraft.ID + ":item", "inventory"), wrapper);
        BSModelFactory.onModelBakeEvent(event);
    }

    private static class BakedModelWrapper implements IBakedModel, ISmartBlockModel, ISmartItemModel {

        @Override
        public IBakedModel handleBlockState(IBlockState state) {
            IBlockModelProvider provider = INSTANCE.blockModelProviders.get(state.getBlock());
            if (provider == null){
                throw new NullPointerException("Error trying to find a Model provider for Block: "+state.getBlock()+", and state: "+state);
            }
            return provider.getModelForBlockData(state);
        }

        @Override
        public IBakedModel handleItemState(ItemStack stack) {
            IItemModelProvider provider = INSTANCE.itemModelProviders.get(stack.getItem());
            if (provider == null){
                throw new NullPointerException("Error trying to find a Model provider for Item: "+stack.getItem()+", and stack: "+stack);
            }
            return provider.getModelForItemStack(stack);
        }

        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
            return new LinkedList<>();
        }

        @Override
        public List<BakedQuad> getGeneralQuads() {
            return new LinkedList<>();
        }

        @Override
        public boolean isAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return null;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return null;
        }
    }
}
