package alpvax.mc.goprone;

import alpvax.mc.goprone.network.PacketHandler;
import alpvax.mc.goprone.network.SetPronePacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkHooks;

@EventBusSubscriber(value = Dist.CLIENT, modid = GoProne.MODID)
public class ClientProxy {
    public static final KeyMapping prone = new KeyMapping(
        "key.prone", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_C,
        "key.categories.movement"
    );
    public static final KeyMapping toggleProne = new KeyMapping(
        "key.prone.toggle", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, "key.categories.movement");

    private static boolean proneToggle = false;

    private static boolean serverSideExists = false;

    public static void init() {
        ClientRegistry.registerKeyBinding(prone);
        ClientRegistry.registerKeyBinding(toggleProne);
    }

    public static void setToggleState(boolean toggleState) {
        proneToggle = toggleState;
    }
    @SubscribeEvent
    public static void updateKeys(InputEvent.KeyInputEvent event) {
        if (isDisabled()) {
            return;
        }
        var flag = event.getKey() == prone.getKey().getValue();
        if (toggleProne.consumeClick()) {
            proneToggle = !proneToggle;
            flag = true;
        }
        if (flag) {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                PacketHandler.sendToServer(new SetPronePacket(prone.isDown() != proneToggle));
                player.getCapability(PlayerProneData.CAPABILITY)
                    .ifPresent(data -> data.setProne(prone.isDown() != proneToggle));
            }
        }
    }

    @SubscribeEvent
    public static void onServerConnect(ClientPlayerNetworkEvent.LoggedInEvent event) {
        var connection = NetworkHooks.getConnectionData(event.getConnection());
        serverSideExists = connection != null && connection.getModList().contains(GoProne.MODID);
        if (!serverSideExists) {
            event.getPlayer()
                .displayClientMessage(Component.translatable(GoProne.MODID + ".notinstalled")
                                          .withStyle(ChatFormatting.BOLD, ChatFormatting.RED), false);
        }
    }

    public static boolean isDisabled() {
        return !serverSideExists;
    }
    public static void onConfigChange() {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(PlayerProneData.CAPABILITY).ifPresent(PlayerProneData::markDirty);
        }
    }
}
