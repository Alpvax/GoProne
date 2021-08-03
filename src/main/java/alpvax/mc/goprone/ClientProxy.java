package alpvax.mc.goprone;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@EventBusSubscriber(value = Dist.CLIENT, modid = GoProne.MODID)
public class ClientProxy {
  public static final KeyMapping prone = new KeyMapping("key.prone", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.movement");
  public static final KeyMapping toggleProne = new KeyMapping("key.prone.toggle", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, "key.categories.movement");

  private static boolean previousPressed = false;
  private static boolean proneToggle = false;

  public static void init() {
    ClientRegistry.registerKeyBinding(prone);
    ClientRegistry.registerKeyBinding(toggleProne);
  }

  @SubscribeEvent
  public static void updateKeys(ClientTickEvent event) {
    if (event.phase == Phase.END) {
      boolean pressed = toggleProne.isDown();
      if (pressed && !previousPressed) {
        proneToggle = !proneToggle;
      }
      previousPressed = pressed;
      updateClientProneState();
    }
  }

  public static void updateProneState(Player player) {
    // Poses are automatically synced from server->client, so we don't have to worry about other players on the client
    if (player == Minecraft.getInstance().player) {
      updateClientProneState();
    }
  }

  private static void updateClientProneState() {
    Player player = Minecraft.getInstance().player;
    if (player != null) {
      UUID uuid = player.getUUID();
      boolean shouldBeProne = prone.isDown() != proneToggle;
      if (shouldBeProne != GoProne.entityProneStates.getOrDefault(uuid, false)) {
        PacketHandler.sendToServer(new SetPronePacket(shouldBeProne));
      }
      GoProne.setProne(uuid, shouldBeProne);
    }
  }
}
