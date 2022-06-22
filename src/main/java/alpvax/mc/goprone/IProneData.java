package alpvax.mc.goprone;

import alpvax.mc.goprone.validation.ProneCheck;
import alpvax.mc.goprone.validation.SimpleChecks;
import net.minecraft.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface IProneData {
    Set<ProneCheck<Void>> TICK_CHECKS = Util.make(new HashSet<>(), s -> s.addAll(Arrays.asList(SimpleChecks.values())));

    /**
     * @return true if the client is telling this data to be prone
     */
    boolean shouldBeProne();
    /**
     * Called by the packet handler on the server side, and also called on the local client player (client side)
     *
     * @param shouldBeProne whether the client is telling this data to be prone
     */
    void setProne(boolean shouldBeProne);

    @Nullable EntityType<?> getPrevRiding();
    void setRiding(@Nullable EntityType<?> riding);

    void playerTick(Player player);
}
