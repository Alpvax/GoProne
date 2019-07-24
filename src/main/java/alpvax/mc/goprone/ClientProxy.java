package alpvax.mc.goprone;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class ClientProxy implements IProxy {
  public static final KeyBinding prone = new KeyBinding("key.prone", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.movement");

  public ClientProxy() {
    ClientRegistry.registerKeyBinding(prone);
  }

  @Override
  public boolean onProneTick(PlayerEntity player, boolean previous) {
    if (player != Minecraft.getInstance().player) {
      return previous;
    }
    boolean shouldBeProne = prone.isKeyDown();
    if (previous != shouldBeProne) {
      PacketHandler.sendToServer(new SetPronePacket(shouldBeProne));
    }
    return shouldBeProne;
  }
}
