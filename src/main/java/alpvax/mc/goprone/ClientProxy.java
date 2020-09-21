package alpvax.mc.goprone;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@EventBusSubscriber(value = Dist.CLIENT, modid = GoProne.MODID)
public class ClientProxy {
  public static final KeyBinding prone = new KeyBinding("key.prone", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.movement");
  public static final KeyBinding toggleProne = new KeyBinding("key.prone.toggle", KeyConflictContext.IN_GAME, InputMappings.INPUT_INVALID, "key.categories.movement");

  //private static boolean previousPressed = false;
  private static boolean toggleState = false;

  public static void init() {
    ClientRegistry.registerKeyBinding(prone);
    ClientRegistry.registerKeyBinding(toggleProne);
  }

  @SubscribeEvent
  public static void onKeyPress(InputEvent.KeyInputEvent event) {
    if (toggleProne.isPressed()) {
      toggleState = !toggleState;
      updateClientProneState();
    }
  }

  /*@SubscribeEvent
  public static void updateKeys(ClientTickEvent event) {
    if (event.phase == Phase.END) {
      boolean pressed = toggleProne.isKeyDown();
      if (pressed && !previousPressed) {
        toggleState = !toggleState;
      }
      previousPressed = pressed;
      updateClientProneState();
    }
  }*/

  public static void updateProneState(PlayerEntity player) {
    // Poses are automatically synced from server->client, so we don't have to worry about other players on the client
    if (player == Minecraft.getInstance().player) {
      updateClientProneState();
    }
  }

  private static void updateClientProneState() {
    PlayerEntity player = Minecraft.getInstance().player;
    if (player != null) {
      UUID uuid = player.getUniqueID();
      boolean shouldBeProne = prone.isKeyDown() != toggleState;
      if (shouldBeProne != GoProne.entityProneStates.getOrDefault(uuid, false)) {
        PacketHandler.sendToServer(new SetPronePacket(shouldBeProne));
      }
      GoProne.setProne(uuid, shouldBeProne);
    }
  }
}
