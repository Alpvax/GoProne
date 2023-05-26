package alpvax.mc.goprone.fabric;

import alpvax.mc.goprone.config.ConfigOptions;
import alpvax.mc.goprone.fabric.network.FabricPacketHandler;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraftforge.fml.config.ModConfig;
import ru.vidtu.JsonConfig;

import static alpvax.mc.goprone.GPConstants.MODID;

/**
 * Main mod initializer.
 *
 * @author Alpvax
 * @author VidTu
 */
public class GoProne implements ModInitializer {
    @Override
    public void onInitialize() {
        new FabricPacketHandler().register();
        ServerLifecycleEvents.SERVER_STARTED.register(JsonConfig::load);
        ForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.SERVER, ConfigOptions.SPEC);
        ModConfigEvents.reloading(MODID).register(config -> {
            if (config.getSpec() == ConfigOptions.SPEC) {
                ConfigOptions.instance().bakeConfig();
                //TODO: get server and mark all players dirty
//                var server = ServerCommandSource.getM;
//                if (server != null) {
//                    server.getPlayerList()
//                            .getPlayers()
//                            .forEach(p -> p.getCapability(ProneDataCapabilityProvider.CAPABILITY).ifPresent(PlayerProneData::markDirty));
//                }
            }
        });
        //TODO: copy and delete old config, listen to events
    }
}