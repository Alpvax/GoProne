package alpvax.mc.goprone;

import alpvax.mc.goprone.config.ConfigOptions;
import alpvax.mc.goprone.platform.service.Services;
import alpvax.mc.goprone.validation.Checks;
import alpvax.mc.goprone.validation.RidingCheck;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class PlayerProneData {

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

    public void markDirty() {
        dirty = true;
    }

    public boolean shouldBeProne() {
        return shouldBeProne;
    }
    public void setProne(boolean prone) {
        shouldBeProne = prone;
        updateProneState();
    }

    public void setRiding(@Nullable EntityType<?> riding) {
        prevRiding = riding;
        if (riding == null || RidingCheck.checkEntityType(riding)
            ? failedChecks.remove(Checks.RIDING)
            : failedChecks.add(Checks.RIDING)) {
            markDirty();
        }
    }

    public void playerTick() {
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
            Services.PRONE.setForcedPose(player, Pose.SWIMMING);
            if (!ConfigOptions.instance().sprintingAllowed.get()) {
                player.setSprinting(false);
            }
        } else {
            Services.PRONE.setForcedPose(player,null);
        }
        dirty = false;
    }
}
