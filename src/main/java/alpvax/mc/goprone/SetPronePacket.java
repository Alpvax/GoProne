package alpvax.mc.goprone;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SetPronePacket {
  private final boolean pressed;

  SetPronePacket(boolean isPressed) {
    this.pressed = isPressed;
  }

  public static SetPronePacket decode(PacketBuffer buffer) {
    return new SetPronePacket(buffer.readBoolean());
  }

  public static void encode(SetPronePacket msg, PacketBuffer buffer) {
    buffer.writeBoolean(msg.pressed);
  }

  public static void handle(SetPronePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
    contextSupplier.get().enqueueWork(() -> {
      ServerPlayerEntity player = contextSupplier.get().getSender();
      GoProne.entityProneStates.put(player.getUniqueID(), msg.pressed);
    });
  }
}
