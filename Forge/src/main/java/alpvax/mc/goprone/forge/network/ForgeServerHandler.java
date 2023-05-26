package alpvax.mc.goprone.forge.network;

import alpvax.mc.goprone.network.IMessageType;
import alpvax.mc.goprone.network.IServerHandler;
import alpvax.mc.goprone.network.packets.ServerBoundPackets;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class ForgeServerHandler implements IServerHandler<NetworkEvent.Context> {
    private final SimpleChannel channel;

    public ForgeServerHandler(SimpleChannel channel) {
        this.channel = channel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <MSG> void register(ServerBoundPackets packet) {
        var mType = (IMessageType<MSG>) packet.getType();
        channel.registerMessage(packet.ordinal(), (Class<MSG>) packet.getMessageClass(), mType::encode, mType::decode, (msg, ctx) -> mType.handleServerSide(this, msg, buildContext(null, ctx.get())));
    }

    @Override
    public IContext buildContext(MinecraftServer server, NetworkEvent.Context fromCtx) {
        return new IContext() {
            @Override
            public void enqueue(Runnable toRun) {
                fromCtx.enqueueWork(toRun);
            }
            @Override
            public ServerPlayer getSender() {
                return fromCtx.getSender();
            }
//            @Override
//            public Connection getNetworkManager() {
//                return fromCtx.;
//            }
        };
    }
}
