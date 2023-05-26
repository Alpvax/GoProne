package alpvax.mc.goprone.forge.network;

import alpvax.mc.goprone.GPConstants;
import alpvax.mc.goprone.network.IClientHandler;
import alpvax.mc.goprone.network.IMessageType;
import alpvax.mc.goprone.network.IServerHandler;
import alpvax.mc.goprone.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.Nullable;

public final class ForgePacketHandler extends PacketHandler {
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(GPConstants.MODID, "main_channel"))
        .clientAcceptedVersions(PROTOCOL_VERSION::equals)
        .serverAcceptedVersions(NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION))
        .networkProtocolVersion(() -> PROTOCOL_VERSION)
        .simpleChannel();

    private final ForgeServerHandler serverHandler = new ForgeServerHandler(HANDLER);

    @Override
    protected @Nullable IServerHandler<?> getServerHandler() {
        return serverHandler;
    }

    @Override
    protected @Nullable IClientHandler<?> getClientHandler() {
        return null;
    }

    @Override
    protected <MSG extends IMessageType.IServerBoundMessage> void send(MSG msg) {
        HANDLER.sendToServer(msg);
    }

    @Override
    protected <MSG extends IMessageType.IClientBoundMessage> void send(MSG msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer)) {
            HANDLER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
