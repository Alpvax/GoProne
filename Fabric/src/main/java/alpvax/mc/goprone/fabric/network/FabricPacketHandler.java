package alpvax.mc.goprone.fabric.network;

import alpvax.mc.goprone.network.IClientHandler;
import alpvax.mc.goprone.network.IMessageType;
import alpvax.mc.goprone.network.IServerHandler;
import alpvax.mc.goprone.network.PacketHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.NotImplementedException;

import static alpvax.mc.goprone.GPConstants.MODID;

/**
 * Handler for packets.
 *
 * @author Alpvax
 * @author VidTu
 * @implNote Not compatible with Forge impl. (e.g. Fabric client is not guaranteed to work on Forge server)
 */
public final class FabricPacketHandler extends PacketHandler {
    public static final ResourceLocation ID_ISINSTALLED = new ResourceLocation(MODID, "is_installed");

    private final FabricServerHandler serverHandler = new FabricServerHandler();

    @Override
    protected IServerHandler<?> getServerHandler() {
        return serverHandler;
    }
    @Override
    protected IClientHandler<?> getClientHandler() {
        return null;
    }
    @Override
    protected void registerNonPlayEvents() {
        ServerPlayConnectionEvents.JOIN.register(
            (h, sender, srv) -> sender.sendPacket(ID_ISINSTALLED, new FriendlyByteBuf(Unpooled.buffer())));
    }

    /**
     * Send a packet to the server.<br>
     * Must be called Client side.
     *
     * @param msg Packet to send
     */
    @Override
    public <MSG extends IMessageType.IServerBoundMessage> void send(MSG msg) {
        serverHandler.send(msg);
    }

    @Override
    public <MSG extends IMessageType.IClientBoundMessage> void send(MSG msg, ServerPlayer player) {
        throw new NotImplementedException("Client bound packets are not yet implemented");
    }

}
