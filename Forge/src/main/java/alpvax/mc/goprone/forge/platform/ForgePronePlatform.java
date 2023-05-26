package alpvax.mc.goprone.forge.platform;

import alpvax.mc.goprone.PlayerProneData;
import alpvax.mc.goprone.forge.ProneDataCapabilityProvider;
import alpvax.mc.goprone.forge.network.ForgePacketHandler;
import alpvax.mc.goprone.network.PacketHandler;
import alpvax.mc.goprone.platform.service.IPronePlatform;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;


public class ForgePronePlatform  implements IPronePlatform {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public PacketHandler createPacketHandler() {
        return new ForgePacketHandler();
    }

    @Override
    public Optional<PlayerProneData> getProneData(Player player) {
        return player.getCapability(ProneDataCapabilityProvider.CAPABILITY).resolve();
    }

    @Override
    public void setForcedPose(Player player, Pose pose) {
        player.setForcedPose(pose);
    }
}
