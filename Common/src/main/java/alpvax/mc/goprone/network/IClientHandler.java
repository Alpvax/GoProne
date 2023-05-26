package alpvax.mc.goprone.network;

import alpvax.mc.goprone.network.packets.ClientBoundPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;

public interface IClientHandler<T> {
    interface IContext {
        void enqueue(Runnable toRun);
        Connection getNetworkManager();
    }

    <MSG> void register(ClientBoundPackets packet);

    IContext buildContext(Minecraft client, T fromCtx);

//TODO:    void handleXXXPacket(XXXPacket msg, IContext context);
}
