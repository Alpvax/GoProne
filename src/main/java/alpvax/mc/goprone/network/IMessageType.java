package alpvax.mc.goprone.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.Function;

public interface IMessageType<T> {
    void encode(T msg, FriendlyByteBuf buf);

    T decode(FriendlyByteBuf buf);

    void handleServerSide(IServerHandler<?> serverHandler, T msg, IServerHandler.IContext ctx);

    void handleClientSide(IClientHandler<?> clientHandler, T msg, IClientHandler.IContext ctx);

    static <MSG extends IServerBoundMessage> IMessageType<MSG> serverBound(
        BiConsumer<MSG, FriendlyByteBuf> encode,
        Function<FriendlyByteBuf, MSG> decode,
        TriConsumer<IServerHandler<?>, MSG, IServerHandler.IContext> handler
    ) {
        return new IMessageType<>() {
            @Override
            public void encode(MSG msg, FriendlyByteBuf buf) {
                encode.accept(msg, buf);
            }
            @Override
            public MSG decode(FriendlyByteBuf buf) {
                return decode.apply(buf);
            }
            @Override
            public void handleServerSide(IServerHandler<?> serverHandler, MSG msg, IServerHandler.IContext ctx) {
                handler.accept(serverHandler, msg, ctx);
            }
            @Override
            public void handleClientSide(IClientHandler<?> clientHandler, MSG msg, IClientHandler.IContext ctx) {
                throw new IllegalStateException(
                    "Attempting to handle a server bound packet on the client (%s)".formatted(
                        msg.getClass().getSimpleName()));
            }
        };
    }
    interface IServerBoundMessage {
        default void sendToServer() {
            PacketHandler.sendToServer(this);
        }
    }

    static <MSG extends IClientBoundMessage> IMessageType<MSG> clientBound(
        BiConsumer<MSG, FriendlyByteBuf> encode,
        Function<FriendlyByteBuf, MSG> decode,
        TriConsumer<IClientHandler<?>, MSG, IClientHandler.IContext> handler
    ) {
        return new IMessageType<>() {
            @Override
            public void encode(MSG msg, FriendlyByteBuf buf) {
                encode.accept(msg, buf);
            }
            @Override
            public MSG decode(FriendlyByteBuf buf) {
                return decode.apply(buf);
            }
            @Override
            public void handleServerSide(IServerHandler<?> serverHandler, MSG msg, IServerHandler.IContext ctx) {
                throw new IllegalStateException(
                    "Attempting to handle a client bound packet on the server (%s)".formatted(
                        msg.getClass().getSimpleName()));
            }
            @Override
            public void handleClientSide(IClientHandler<?> clientHandler, MSG msg, IClientHandler.IContext ctx) {
                handler.accept(clientHandler, msg, ctx);
            }
        };
    }
    interface IClientBoundMessage {
        default void sendToClient(ServerPlayer target) {
            PacketHandler.sendTo(this, target);
        }
    }
}
