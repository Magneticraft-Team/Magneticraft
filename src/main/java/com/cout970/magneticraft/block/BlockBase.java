package com.cout970.magneticraft.block;

import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.block.IBlockBoxes;
import net.darkaqua.blacksmith.block.IStatefulBlock;
import net.darkaqua.blacksmith.raytrace.Cube;
import net.darkaqua.blacksmith.raytrace.RayTraceResult;
import net.darkaqua.blacksmith.render.ITexture;
import net.darkaqua.blacksmith.render.TextureManager;
import net.darkaqua.blacksmith.render.providers.IBlockModelProvider;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.darkaqua.blacksmith.render.providers.factory.SimpleBlockModelFactory;
import net.darkaqua.blacksmith.render.providers.block.UniqueModelProvider;
import net.darkaqua.blacksmith.util.ResourceReference;
import net.darkaqua.blacksmith.util.WorldRef;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.darkaqua.blacksmith.vectors.Vect3i;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by cout970 on 16/12/2015.
 */
public abstract class BlockBase extends Block implements IBlockBoxes, BlockProperties {

    public static final CreativeTabs CREATIVE_TAB_MAIN = new CreativeTabs("magneticraft") {
        @Override
        public Item getTabIconItem() {
            return Items.apple;
        }
    };

    public BlockBase() {
        super(Material.iron);
        setCreativeTab(CREATIVE_TAB_MAIN);
        Cube c = getBounds(null, null, getDefaultState());
        setBlockBounds((float) c.minX(), (float) c.minY(), (float) c.minZ(), (float) c.maxX(), (float) c.maxY(), (float) c.maxZ());
    }

    protected abstract String getBlockName();

    @Override
    public String getUnlocalizedName() {
        return getBlockName();
    }

    public void registerName(String name) {
        Magneticraft.LANG.addName("tile." + getUnlocalizedName(), name);
    }

    //interface method for IBlockBoxes

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        AxisAlignedBB a = getSelectionCube(new WorldRef(worldIn, new Vect3i(pos))).translate(new Vect3d(pos)).toAxisAlignedBB();
        System.out.println(a);
        return a;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        List<Cube> boxes = getCollisionCubes(new WorldRef(worldIn, new Vect3i(pos)), collidingEntity);
        Vect3d vec = new Vect3d(pos);
        boxes.stream().map((c) -> c.translate(vec)).map(Cube::toAxisAlignedBB).filter(mask::intersectsWith).forEach(list::add);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
        RayTraceResult res = rayTraceBlock(new WorldRef(worldIn, pos), new Vect3d(start), new Vect3d(end));
        return res == null ? null : res.toMOP();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        Cube c = getBounds(worldIn, pos, worldIn.getBlockState(pos));
        setBlockBounds((float) c.minX(), (float) c.minY(), (float) c.minZ(), (float) c.maxX(), (float) c.maxY(), (float) c.maxZ());
    }


    //istatefulblock

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (this instanceof IStatefulBlock) {
            return ((IStatefulBlock) this).getStateFromMetadata(meta);
        }
        return super.getStateFromMeta(meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (this instanceof IStatefulBlock) {
            return ((IStatefulBlock) this).getMetaFromBlockState(state);
        }
        return super.getMetaFromState(state);
    }

    public IBlockModelProvider getModelProvider() {
        ITexture texture = TextureManager.registerTexture(
                new ResourceReference(Magneticraft.ID, "blocks/" + getBlockName().toLowerCase()));
        IModelFactory factory = new SimpleBlockModelFactory(texture);
        return new UniqueModelProvider(factory);
    }
}
