package alpvax.mc.goprone;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SetPronePacket {
  private final boolean prone;

  SetPronePacket(boolean isPressed) {
    this.prone = isPressed;
  }

  public static SetPronePacket decode(PacketBuffer buffer) {
    return new SetPronePacket(buffer.readBoolean());
  }

  public static void encode(SetPronePacket msg, PacketBuffer buffer) {
    buffer.writeBoolean(msg.prone);
  }

  public static void handle(SetPronePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
    contextSupplier.get().enqueueWork(() -> {
      ServerPlayerEntity player = contextSupplier.get().getSender();
      if (player != null) {
        //player.setForcedPose(msg.prone ? Pose.SWIMMING : null);
        GoProne.setProne(player.getUniqueID(), msg.prone);
      }
    });
  }
}
