package alpvax.mc.goprone;

import alpvax.mc.goprone.network.PacketHandler;
import alpvax.mc.goprone.network.SetPronePacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.NetworkHooks;

@EventBusSubscriber(value = Dist.CLIENT, modid = GoProne.MODID)
public class ClientProxy {
    public static final KeyMapping prone = new KeyMapping(
            "key.prone", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_C,
            "key.categories.movement"
    ) {
        @Override
        public void setDown(boolean down) {
            super.setDown(down);
            sendUpdate();
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


    private static boolean serverSideExists = false;

    public static void init(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(prone);
        ClientRegistry.registerKeyBinding(toggleProne);
    }

    public static void setToggleState(boolean toggleState) {
        toggleProne.setToggle(toggleState);
    }

    private static void sendUpdate() {
        if (isDisabled()) {
            return;
        }
        var player = Minecraft.getInstance().player;
        if (player != null) {
            var shouldBeProne = prone.isDown() != toggleProne.isDown();
            PacketHandler.sendToServer(new SetPronePacket(shouldBeProne));
            player.getCapability(PlayerProneData.CAPABILITY)
                    .ifPresent(data -> data.setProne(shouldBeProne));
        }
    }

    @SubscribeEvent
    public static void onServerConnect(ClientPlayerNetworkEvent.LoggedInEvent event) {
        var connection = NetworkHooks.getConnectionData(event.getConnection());
        serverSideExists = connection != null && connection.getModList().contains(GoProne.MODID);
        if (!serverSideExists) {
            event.getPlayer()
                    .displayClientMessage(new TranslatableComponent(GoProne.MODID + ".notinstalled")
                            .withStyle(ChatFormatting.BOLD, ChatFormatting.RED), false);
        }
    }

    @SubscribeEvent
    public static void attachCapabilityClient(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LocalPlayer player) {
            event.addCapability(new ResourceLocation(GoProne.MODID, "player_cap"), new PlayerProneData.Provider(player));
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
