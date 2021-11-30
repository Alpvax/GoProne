package alpvax.mc.goprone;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetPronePacket {
  private final boolean pressed;

  SetPronePacket(boolean isPressed) {
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
        GoProne.setProne(player.getUUID(), msg.pressed);
      }
    });
  }
}
