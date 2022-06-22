package alpvax.mc.goprone;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface RedirectedProneData extends IProneData {
    IProneData getRedirectedProneData();

    @Override
    default boolean shouldBeProne() {
        return getRedirectedProneData().shouldBeProne();
    }
    @Override
    default void setProne(boolean shouldBeProne) {
        getRedirectedProneData().setProne(shouldBeProne);
    }
    @Override
    @Nullable
    default EntityType<?> getPrevRiding() {
        return getRedirectedProneData().getPrevRiding();
    }
    @Override
    default void setRiding(@Nullable EntityType<?> riding) {
        getRedirectedProneData().setRiding(riding);
    }
    @Override
    default void playerTick(Player player) {
        getRedirectedProneData().playerTick(player);
    }
}
