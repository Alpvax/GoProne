package alpvax.mc.goprone.fabric.network;

import alpvax.mc.goprone.network.IMessageType;
import alpvax.mc.goprone.network.IServerHandler;
import alpvax.mc.goprone.network.packets.ServerBoundPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static alpvax.mc.goprone.GPConstants.MODID;

public class FabricServerHandler implements IServerHandler<ServerGamePacketListenerImpl> {
    private final Map<Class<?>, ServerBoundPacket<?>> registeredPackets = new HashMap<>();

    static record ServerBoundPacket<MSG>(ResourceLocation id, IMessageType<MSG> type) {
        public void sendToServer(MSG msg) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            type.encode(msg, buf);
            ClientPlayNetworking.send(id, buf);
        }
    }

    @Override
    public IContext buildContext(MinecraftServer server, ServerGamePacketListenerImpl fromCtx) {
        return new IContext() {
            @Override
            public void enqueue(Runnable toRun) {
                server.execute(toRun);
            }
            @Override
            public ServerPlayer getSender() {
                return fromCtx.getPlayer();
            }
//            @Override
//            public Connection getNetworkManager() {
//                return fromCtx.;
//            }
        };
    }

    @SuppressWarnings("unchecked")
    public <MSG> void register(ServerBoundPackets packet) {
        register(
            (Class<MSG>) packet.getMessageClass(),
            new ResourceLocation(MODID, packet.name().toLowerCase(Locale.ROOT)),
            (IMessageType<MSG>) packet.getType()
        );
    }
    <MSG> void register(Class<MSG> msgClass, ResourceLocation id, IMessageType<MSG> messageType) {
        ServerPlayNetworking.registerGlobalReceiver(id,
                                                    (server, player, ctx, buf, sender) -> messageType.handleServerSide(
                                                        this, messageType.decode(buf), buildContext(server, ctx))
        );
        registeredPackets.put(msgClass, new ServerBoundPacket<>(id, messageType));
    }

    @SuppressWarnings("unchecked")
    <MSG> void send(MSG msg) {
        FabricServerHandler.ServerBoundPacket<MSG> msgWrapper = (FabricServerHandler.ServerBoundPacket<MSG>) registeredPackets.get(
            msg.getClass());
        if (msgWrapper == null) {
            throw new NullPointerException(
                "Message class %s has not been registered correctly as serverbound".formatted(
                    msg.getClass().getSimpleName()));
        }
        msgWrapper.sendToServer(msg);
    }
}
