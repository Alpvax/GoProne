package alpvax.mc.goprone.network;

import alpvax.mc.goprone.PlayerProneData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetPronePacket {
    private final boolean pressed;

    public SetPronePacket(boolean isPressed) {
        this.pressed = isPressed;
    }

    public static SetPronePacket decode(FriendlyByteBuf buffer) {
        return new SetPronePacket(buffer.readBoolean());
    }

    public static void encode(SetPronePacket msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.pressed);
    }

    public static void handle(SetPronePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer player = contextSupplier.get().getSender();
            if (player != null) {
                player.getCapability(PlayerProneData.CAPABILITY)
                        .orElseThrow(() -> new NullPointerException("PlayerProneData capability not attached!"))
                        .setProne(msg.pressed);
//        GoProne.setProne(player.getUUID(), msg.pressed);
            }
        });
    }
}
