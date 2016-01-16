package com.cout970.magneticraft.proxy;

import com.cout970.magneticraft.ManagerBlocks;
import com.cout970.magneticraft.ManagerItems;

/**
 * Created by cout970 on 06/12/2015.
 */
public class ClientProxy extends CommonProxy {

    public void init() {
        super.init();
        ManagerBlocks.initBlockRenders();
        ManagerItems.initItemRenders();
    }
}
