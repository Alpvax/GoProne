package alpvax.mc.goprone;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Map;

public final class PacketHandler
{
  private static final String PROTOCOL_VERSION = Integer.toString(1);
  private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
      .named(new ResourceLocation(GoProne.MODID, "main_channel"))
      .clientAcceptedVersions(PROTOCOL_VERSION::equals)
      .serverAcceptedVersions((ver) -> true)
      .networkProtocolVersion(() -> PROTOCOL_VERSION)
      .simpleChannel();

  public static void register()
  {
    int disc = 0;

    HANDLER.registerMessage(disc++, SetPronePacket.class, SetPronePacket::encode, SetPronePacket::decode, SetPronePacket::handle);
  }

  /**
   * Sends a packet to the server.<br>
   * Must be called Client side.
   */
  public static void sendToServer(Object msg)
  {
    HANDLER.sendToServer(msg);
  }

  /**
   * Send a packet to a specific player.<br>
   * Must be called Server side.
   */
  public static void sendTo(Object msg, ServerPlayerEntity player)
  {
    if (!(player instanceof FakePlayer))
    {
      HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
  }
}
