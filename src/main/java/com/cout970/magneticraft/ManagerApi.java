package com.cout970.magneticraft;

import com.cout970.magneticraft.api.electricity.IElectricConductor;
import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.api.tool.IHammer;
import com.cout970.magneticraft.item.ItemStoneHammer;
import com.cout970.magneticraft.tileentity.electric.TileElectricBase;
import com.cout970.magneticraft.tileentity.kinetic.TileKineticBase;
import com.cout970.magneticraft.util.EmptyStorageHandler;
import net.darkaqua.blacksmith.block.IRotableBlock;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.IItemHandler;

/**
 * Created by cout970 on 19/03/2016.
 */
public class ManagerApi {

    @CapabilityInject(IItemHandler.class)
    public static final Capability<IItemHandler> ITEM_HANDLER = null;

    @CapabilityInject(IElectricConductor.class)
    public static final Capability<IElectricConductor> ELECTRIC_CONDUCTOR = null;

    @CapabilityInject(IHammer.class)
    public static final Capability<IHammer> HAMMER = null;

    @CapabilityInject(IKineticConductor.class)
    public static final Capability<IKineticConductor> KINETIC_CONDUCTOR = null;

    @CapabilityInject(IRotableBlock.class)
    public static final Capability<IRotableBlock> ROTABLE_BLOCK = null;

    public static void init(){
        CapabilityManager.INSTANCE.register(IHammer.class, new EmptyStorageHandler<>(), ItemStoneHammer::new);
        CapabilityManager.INSTANCE.register(IKineticConductor.class, new EmptyStorageHandler<>(), TileKineticBase::new);
        CapabilityManager.INSTANCE.register(IElectricConductor.class, new EmptyStorageHandler<>(), TileElectricBase::new);
    }
}
