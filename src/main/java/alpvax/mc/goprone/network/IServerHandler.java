package alpvax.mc.goprone.network;

import alpvax.mc.goprone.network.packets.ServerBoundPackets;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface IServerHandler<T> {
    interface IContext {
        void enqueue(Runnable toRun);
        ServerPlayer getSender();
        Connection getNetworkManager();
    }

    <MSG> void register(ServerBoundPackets packet);

    IContext buildContext(MinecraftServer server, T fromCtx);

    void handleSetPronePacket(ServerBoundPackets.SetPronePacket msg, IContext context);
}
