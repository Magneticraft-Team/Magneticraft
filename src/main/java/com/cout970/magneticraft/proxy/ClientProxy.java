package com.cout970.magneticraft.proxy;

import com.cout970.magneticraft.ManagerBlocks;
import com.cout970.magneticraft.ManagerItems;
import com.cout970.magneticraft.ManagerRender;
import com.cout970.magneticraft.client.model.ModelConstants;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

/**
 * Created by cout970 on 06/12/2015.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(ManagerRender.INSTANCE);
        ModelConstants.loadModels();
        ManagerBlocks.initBlockRenders();
        ManagerItems.initItemRenders();
    }

    @Mod.EventHandler
    public void onModelReload(ModelBakeEvent event){
        ModelConstants.loadModels();
        ManagerBlocks.reloadModels();
    }
}
