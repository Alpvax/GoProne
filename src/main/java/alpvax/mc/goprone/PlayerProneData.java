package alpvax.mc.goprone;

import alpvax.mc.goprone.config.ConfigOptions;
import alpvax.mc.goprone.validation.Checks;
import alpvax.mc.goprone.validation.RidingCheck;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class PlayerProneData {
    public static Capability<PlayerProneData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    @NotNull Player player;
    private boolean shouldBeProne;
    private final EnumSet<Checks> failedChecks = EnumSet.noneOf(Checks.class);
    private boolean dirty = false;
    @Nullable private EntityType<?> prevRiding;

    public PlayerProneData(@NotNull Player player) {
        this.player = player;
        var riding = player.getVehicle();
        prevRiding = riding == null ? null : riding.getType();
    }

    void markDirty() {
        dirty = true;
    }

    public void setProne(boolean prone) {
        shouldBeProne = prone;
        updateProneState();
    }

    private void setRiding(@Nullable EntityType<?> riding) {
        prevRiding = riding;
        if (riding == null || RidingCheck.checkEntityType(riding)
            ? failedChecks.remove(Checks.RIDING)
            : failedChecks.add(Checks.RIDING)) {
            markDirty();
        }
    }

    protected void playerTick() {
        if (Checks.CLIMBING.updateFailed(player.onClimbable(), failedChecks)) {
            markDirty();
        }
        if (Checks.FLYING.updateFailed(!player.isOnGround(), failedChecks)) {
            markDirty();
        }
        {
            var riding = player.getVehicle();
            if (riding != null) {
                var t = player.getVehicle().getType();
                if (t != prevRiding) {
                    setRiding(t);
                }
            } else if (prevRiding != null) {
                setRiding(null);
            }
        }
        if (dirty) {
            updateProneState();
        }
    }

    private void updateProneState() {
        if (shouldBeProne && failedChecks.isEmpty()) {
            player.setForcedPose(Pose.SWIMMING);
            if (!ConfigOptions.instance().sprintingAllowed.get()) {
                player.setSprinting(false);
            }
        } else {
            player.setForcedPose(null);
        }
        dirty = false;
    }

    static class Provider implements ICapabilityProvider {
        private final PlayerProneData data;
        private final LazyOptional<PlayerProneData> holder; //TODO: Invalidate when player dies?
        public Provider(Player player) {
            data = new PlayerProneData(player);
            holder = LazyOptional.of(() -> data);
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(
            @NotNull Capability<T> cap, @Nullable Direction side) {
            return CAPABILITY.orEmpty(cap, holder);
        }
    }
}
