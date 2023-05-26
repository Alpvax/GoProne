package alpvax.mc.goprone.platform.service;

import alpvax.mc.goprone.PlayerProneData;
import alpvax.mc.goprone.network.PacketHandler;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;


public interface IPronePlatform {
    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    PacketHandler createPacketHandler();

    Optional<PlayerProneData> getProneData(Player player);

    void setForcedPose(Player player, Pose pose);
}
