package alpvax.mc.goprone.fabric.platform;

import alpvax.mc.goprone.PlayerProneData;
import alpvax.mc.goprone.fabric.IForcePose;
import alpvax.mc.goprone.fabric.IProneDataProvider;
import alpvax.mc.goprone.fabric.network.FabricPacketHandler;
import alpvax.mc.goprone.network.PacketHandler;
import alpvax.mc.goprone.platform.service.IPronePlatform;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class FabricPronePlatform implements IPronePlatform {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public PacketHandler createPacketHandler() {
        return new FabricPacketHandler();
    }

    @Override
    public Optional<PlayerProneData> getProneData(Player player) {
        if (player instanceof IProneDataProvider p) {
            return Optional.of(p.getProneData());
        }
        return Optional.empty();
    }

    @Override
    public void setForcedPose(Player player, Pose pose) {
        ((IForcePose) player).setForcedPose(pose);
    }
}
