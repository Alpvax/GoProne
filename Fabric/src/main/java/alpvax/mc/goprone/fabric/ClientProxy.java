package alpvax.mc.goprone.fabric;

import alpvax.mc.goprone.fabric.network.FabricPacketHandler;
import alpvax.mc.goprone.network.packets.ServerBoundPackets;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

/**
 * Client mod initializer.
 *
 * @author Alpvax
 * @author VidTu
 */
public class ClientProxy implements ClientModInitializer {
    public static final KeyMapping prone = new KeyMapping(
        "key.prone", InputConstants.Type.KEYSYM, InputConstants.KEY_C, "key.categories.movement");
    public static final KeyMapping toggleProne = new KeyMapping("key.prone.toggle", -1, "key.categories.movement");

    private static boolean previousPressed = false;
    private static boolean proneToggle = false;
    private static boolean working = false;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(prone);
        KeyBindingHelper.registerKeyBinding(toggleProne);
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ClientPlayConnectionEvents.DISCONNECT.register((h, mc) -> working = false);
        ClientPlayNetworking.registerGlobalReceiver(
            FabricPacketHandler.ID_ISINSTALLED, (mc, h, buf, sender) -> working = true);
    }

    public void onTick(Minecraft mc) {
        boolean pressed = toggleProne.consumeClick();
        if (pressed && !previousPressed) {
            proneToggle = !proneToggle;
        }
        previousPressed = pressed;
        updateClientProneState(mc);
    }

    private static void updateClientProneState(Minecraft mc) {
        var player = mc.player;
        if (player != null && working) {
            boolean shouldBeProne = prone.isDown() != proneToggle;
            var data = ((IProneDataProvider) player).getProneData();
            if (shouldBeProne != data.shouldBeProne()) {
                new ServerBoundPackets.SetPronePacket(shouldBeProne).sendToServer();
            }
            data.setProne(shouldBeProne);
        }
    }
}
