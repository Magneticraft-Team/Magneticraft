package com.cout970.magneticraft;

import com.cout970.magneticraft.network.MessageServerUpdate;
import net.darkaqua.blacksmith.api.common.network.INetworkChannel;
import net.darkaqua.blacksmith.api.common.network.NetworkChannelFactory;
import net.darkaqua.blacksmith.api.common.util.GameSide;

/**
 * Created by cout970 on 17/01/2016.
 */
public class ManagerNetwork {

    public static INetworkChannel CHANNEL;

    public static void init(){
        CHANNEL = NetworkChannelFactory.createNetworkChannel(Magneticraft.ID);
        CHANNEL.registerMessage(new MessageServerUpdate(), MessageServerUpdate.class, 0, GameSide.CLIENT);
    }
}
