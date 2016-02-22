package com.cout970.magneticraft.proxy;

import com.cout970.magneticraft.ManagerBlocks;
import com.cout970.magneticraft.ManagerItems;
import com.cout970.magneticraft.client.model.ModelConstants;
import net.darkaqua.blacksmith.api.common.event.EventSubscribe;
import net.darkaqua.blacksmith.api.common.event.render.IModelsReloadEvent;

/**
 * Created by cout970 on 06/12/2015.
 */
public class ClientProxy extends CommonProxy {

    public void init() {
        super.init();
        ModelConstants.loadModels();
        ManagerBlocks.initBlockRenders();
        ManagerItems.initItemRenders();
    }

    @EventSubscribe
    public void onModelReload(IModelsReloadEvent event){
        ModelConstants.loadModels();
        ManagerBlocks.reloadModels();
    }
}
