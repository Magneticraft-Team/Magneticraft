package com.cout970.magneticraft;

import com.cout970.magneticraft.network.MessageServerUpdate;
import net.darkaqua.blacksmith.network.INetworkChannel;
import net.darkaqua.blacksmith.network.NetworkChannelFactory;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by cout970 on 17/01/2016.
 */
public class ManagerNetwork {

    public static INetworkChannel CHANNEL;

    public static void init(){
        CHANNEL = NetworkChannelFactory.createNetworkChannel(Magneticraft.ID);
        CHANNEL.registerMessage(new MessageServerUpdate(), MessageServerUpdate.class, 0, Side.CLIENT);
    }
}
