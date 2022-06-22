package alpvax.mc.goprone;

import alpvax.mc.goprone.network.fabric.FabricPacketHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import ru.vidtu.Config;

/**
 * Main mod initializer.
 *
 * @author Alpvax
 * @author VidTu
 */
public class GoProne implements ModInitializer {
    public static final String MODID = "goprone";

    @Override
    public void onInitialize() {
        new FabricPacketHandler().register();
        ServerLifecycleEvents.SERVER_STARTED.register(Config::load);
    }
}