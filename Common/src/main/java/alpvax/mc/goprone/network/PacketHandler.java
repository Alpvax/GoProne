package alpvax.mc.goprone.network;

import alpvax.mc.goprone.network.packets.ClientBoundPackets;
import alpvax.mc.goprone.network.packets.ServerBoundPackets;
import alpvax.mc.goprone.platform.service.Services;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class PacketHandler {
    private static PacketHandler instance;
    protected PacketHandler() {
        if (instance != null) {
            throw new IllegalStateException("Cannot create multiple instances of PacketHandler!");
        }
        instance = this;
    }

    public static PacketHandler getInstance() {
        return instance == null ? Services.PRONE.createPacketHandler() : instance;
    }

    /**
     * Call to register the default server and client packets, followed by calling {@link #registerNonPlayEvents}
     * to register any additional events (e.g. handshake)
     *
     * @return {@linkplain this} for initialising and registering at once
     */
    @SuppressWarnings("UnusedReturnValue")
    public static PacketHandler initAndRegister() {
        return getInstance().register();
    }
    public final PacketHandler register() {
        Optional.ofNullable(getServerHandler()).ifPresent(server -> {
            for (ServerBoundPackets pkt : ServerBoundPackets.values()) {
                server.register(pkt);
            }
        });
        Optional.ofNullable(getClientHandler()).ifPresent(client -> {
            for (ClientBoundPackets pkt : ClientBoundPackets.values()) {
                client.register(pkt);
            }
        });
        registerNonPlayEvents();//server, client);
        return this;
    }
    @Nullable
    protected abstract IServerHandler<?> getServerHandler();
    @Nullable
    protected abstract IClientHandler<?> getClientHandler();
    protected void registerNonPlayEvents() {//IServerHandler<?> server, IClientHandler<?> client);
    }

    /**
     * Send a packet to the server.<br>
     * Must be called Client side.
     *
     * @param msg Packet to send
     */
    public static <MSG extends IMessageType.IServerBoundMessage> void sendToServer(MSG msg) {
        instance.send(msg);
    }
    protected abstract <MSG extends IMessageType.IServerBoundMessage> void send(MSG msg);


    /**
     * Send a packet to a specific player.<br>
     * Must be called Server side.
     *
     * @param msg    Packet to send
     * @param player Player to send the packet to
     */
    public static <MSG extends IMessageType.IClientBoundMessage> void sendTo(MSG msg, ServerPlayer player) {
        instance.send(msg, player);
    }
    protected abstract <MSG extends IMessageType.IClientBoundMessage> void send(MSG msg, ServerPlayer player);
}
