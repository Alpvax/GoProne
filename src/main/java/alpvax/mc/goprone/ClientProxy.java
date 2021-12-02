package alpvax.mc.goprone;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

/**
 * Client mod initializer.
 * @author Alpvax
 * @author VidTu
 */
public class ClientProxy implements ClientModInitializer {
	public static final KeyBinding prone = new KeyBinding("key.prone", Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.movement");
	public static final KeyBinding toggleProne = new KeyBinding("key.prone.toggle", InputUtil.UNKNOWN_KEY.getCode(), "key.categories.movement");

	private static boolean previousPressed = false;
	private static boolean proneToggle = false;
	private static boolean working = false;
	
	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(prone);
		KeyBindingHelper.registerKeyBinding(toggleProne);
		ClientTickEvents.END_CLIENT_TICK.register(ClientProxy::updateKeys);
		ClientPlayConnectionEvents.DISCONNECT.register((h, mc) -> working = false);
		ClientPlayNetworking.registerGlobalReceiver(PacketHandler.ID_ISINSTALLED, (mc, h, buf, sender) -> working = true);
	}

	public static void updateKeys(MinecraftClient mc) {
		boolean pressed = toggleProne.wasPressed();
		if (pressed && !previousPressed) {
			proneToggle = !proneToggle;
		}
		previousPressed = pressed;
		updateClientProneState(mc);
	}

	@SuppressWarnings("resource")
	public static void updateProneState(PlayerEntity player) {
		// Poses are automatically synced from server->client, so we don't have to worry
		// about other players on the client
		if (player == MinecraftClient.getInstance().player) {
			updateClientProneState(MinecraftClient.getInstance());
		}
	}

	private static void updateClientProneState(MinecraftClient mc) {
		PlayerEntity player = mc.player;
		if (player != null && working) {
			UUID uuid = player.getUuid();
			boolean shouldBeProne = prone.isPressed() != proneToggle;
			if (shouldBeProne != GoProne.entityProneStates.getOrDefault(uuid, false)) {
				PacketHandler.sendToServer(new SetPronePacket(shouldBeProne));
			}
			GoProne.entityProneStates.put(uuid, shouldBeProne);
		}
	}
}
