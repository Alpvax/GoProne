package alpvax.mc.goprone.network;

import alpvax.mc.goprone.GoProne;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler {
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(GoProne.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void register() {
        int disc = 0;

        HANDLER.registerMessage(
                disc++, SetPronePacket.class, SetPronePacket::encode, SetPronePacket::decode, SetPronePacket::handle//,
                //Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        HANDLER.registerMessage(
                disc++, SetTogglePacket.class, SetTogglePacket::encode, SetTogglePacket::decode, SetTogglePacket::handle//,
                //Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }

    /**
     * Sends a packet to the server.<br>
     * Must be called Client side.
     */
    public static void sendToServer(Object msg) {
        HANDLER.sendToServer(msg);
    }

    /**
     * Send a packet to a specific player.<br>
     * Must be called Server side.
     */
    public static void sendTo(Object msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer)) {
            HANDLER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}