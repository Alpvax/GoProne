package alpvax.mc.goprone.network.packets;

import alpvax.mc.goprone.network.IMessageType;
import alpvax.mc.goprone.network.IServerHandler;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.Function;

public enum ServerBoundPackets {
    SET_PRONE(
        SetPronePacket.class,
        (msg, buffer) -> buffer.writeBoolean(msg.isPressed()),
        buffer -> new SetPronePacket(buffer.readBoolean()),
        IServerHandler::handleSetPronePacket
    );

    final Class<? extends IMessageType.IServerBoundMessage> messageClass;
    final IMessageType<? extends IMessageType.IServerBoundMessage> type;
    <MSG extends IMessageType.IServerBoundMessage> ServerBoundPackets(
        Class<MSG> messageClass,
        BiConsumer<MSG, FriendlyByteBuf> encode,
        Function<FriendlyByteBuf, MSG> decode,
        TriConsumer<IServerHandler<?>, MSG, IServerHandler.IContext> handler
    ) {
        this.messageClass = messageClass;
        this.type = IMessageType.serverBound(encode, decode, handler);
    }
    public Class<? extends IMessageType.IServerBoundMessage> getMessageClass() {
        return messageClass;
    }
    public IMessageType<? extends IMessageType.IServerBoundMessage> getType() {
        return type;
    }

    /**
     * Set prone packet.
     * Used to tell the server when the client input has changed.
     */
    public static record SetPronePacket(boolean isPressed) implements IMessageType.IServerBoundMessage {
    }
}
