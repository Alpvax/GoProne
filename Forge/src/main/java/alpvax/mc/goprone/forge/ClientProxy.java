package alpvax.mc.goprone.forge;


import alpvax.mc.goprone.GPConstants;
import alpvax.mc.goprone.PlayerProneData;
import alpvax.mc.goprone.network.packets.ServerBoundPackets;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = GPConstants.MODID)
public class ClientProxy {
    public static final KeyMapping prone = new KeyMapping(
        "key.prone", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_C,
        "key.categories.movement"
    ) {
        @Override
        public void setDown(boolean down) {
            var wasDown = isDown();
            super.setDown(down);
            if (wasDown != down) {
                sendUpdate();
            }
        }
    };
    public static final AlwaysToggleMapping toggleProne = new AlwaysToggleMapping(
        "key.prone.toggle", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, "key.categories.movement");

    private static class AlwaysToggleMapping extends KeyMapping {
        public AlwaysToggleMapping(
            String description, IKeyConflictContext keyConflictContext,
            InputConstants.Key keyCode, String category) {
            super(description, keyConflictContext, keyCode, category);
        }

        @Override
        public void setDown(boolean down) {
            if (down && isConflictContextAndModifierActive()) {
                super.setDown(!this.isDown());
                sendUpdate();
            }
        }
        private void setToggle(boolean down) {
            super.setDown(down);
        }
    }

//    private static boolean proneToggle = false;

    private static boolean serverSideExists = false;

    public static void init(RegisterKeyMappingsEvent event) {
        event.register(prone);
        event.register(toggleProne);
    }

    public static void setToggleState(boolean toggleState) {
        toggleProne.setToggle(toggleState);
    }
//    @SubscribeEvent
//    public static void updateKeys(InputEvent.KeyInputEvent event) {
//        if (isDisabled()) {
//            return;
//        }
//        var k = event.getKey();
//        var s = event.getScanCode();
//        var flag = prone.matches(k, s) || toggleProne.matches(k, s);
//        if (flag) {
//            sendUpdate();
//        }
//    }

    private static void sendUpdate() {
        if (isDisabled()) {
            return;
        }
        var player = Minecraft.getInstance().player;
        if (player != null) {
            var shouldBeProne = prone.isDown() != toggleProne.isDown();
            new ServerBoundPackets.SetPronePacket(shouldBeProne).sendToServer();
            player.getCapability(ProneDataCapabilityProvider.CAPABILITY)
                .ifPresent(data -> data.setProne(shouldBeProne));
        }
    }

    @SubscribeEvent
    public static void onServerConnect(ClientPlayerNetworkEvent.LoggingIn event) {
        var connection = NetworkHooks.getConnectionData(event.getConnection());
        serverSideExists = connection != null && connection.getModList().contains(GPConstants.MODID);
        if (!serverSideExists) {
            event.getPlayer()
                .displayClientMessage(Component.translatable(GPConstants.MODID + ".notinstalled")
                                          .withStyle(ChatFormatting.BOLD, ChatFormatting.RED), false);
        }
    }

    @SubscribeEvent
    public static void attachCapabilityClient(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LocalPlayer player) {
            event.addCapability(new ResourceLocation(GPConstants.MODID, "player_cap"), new ProneDataCapabilityProvider(player));
        }
    }

    public static boolean isDisabled() {
        return !serverSideExists;
    }
    public static void onConfigChange() {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(ProneDataCapabilityProvider.CAPABILITY).ifPresent(PlayerProneData::markDirty);
        }
    }
}
