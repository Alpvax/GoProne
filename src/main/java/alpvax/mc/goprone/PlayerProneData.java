package alpvax.mc.goprone;

import alpvax.mc.goprone.validation.ProneCheck;
import alpvax.mc.goprone.validation.RidingCheck;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class PlayerProneData implements IProneData {

//    @NotNull PlayerEntity player;
    private boolean shouldBeProne;
    private final Set<ProneCheck<?>> failedChecks = new HashSet<>();
    private boolean dirty = false;
    @Nullable private EntityType<?> prevRiding;

    //    public PlayerProneData(@NotNull PlayerEntity player) {
////        this.player = player;
//        var riding = player.getVehicle();
//        prevRiding = riding == null ? null : riding.getType();
//    }
    public PlayerProneData(Player player) {
        var riding = player.getVehicle();
        prevRiding = riding == null ? null : riding.getType();
    }

    void markDirty() {
        dirty = true;
    }

    public void setProne(boolean prone) {
        shouldBeProne = prone;
        markDirty();
        //TODO: updateProneState();
    }

    @Override
    public boolean shouldBeProne() {
        return shouldBeProne;
    }
    private void updateRiding(@Nullable EntityType<?> riding) {
        prevRiding = riding;
        if (riding == null || RidingCheck.checkEntityType(riding)
            ? failedChecks.remove(RidingCheck.INSTANCE)
            : failedChecks.add(RidingCheck.INSTANCE)) {
            markDirty();
        }
    }

    @Override
    public void setRiding(@Nullable EntityType<?> riding) {
        if (riding != prevRiding) {
            updateRiding(riding);
        }
    }
    public void setRiding(@Nullable Entity riding) {
        if (riding != null) {
            var t = riding.getType();
            if (t != prevRiding) {
                updateRiding(t);
            }
        } else if (prevRiding != null) {
            updateRiding(null);
        }
    }

    public <T> boolean updateCheckState(Player player, ProneCheck<T> check, T data) {
        return switch (check.test(player, !failedChecks.contains(check), data)) {
            case PASSED -> failedChecks.remove(check);
            case FAILED -> failedChecks.add(check);
            case UNCHANGED -> false;
        };
    }

    @Override
    public void playerTick(Player player) {
        if (TICK_CHECKS.parallelStream()
            .map(c -> updateCheckState(player, c, null))
            .reduce((a, b) -> a || b)
            .orElse(false)) {
            markDirty();
        }
        setRiding(player.getVehicle());
        if (dirty) {
            updateProneState(player);
        }
    }

    protected void updateProneState(Player player) {
        if (shouldBeProne && failedChecks.isEmpty()) {
            ((IForcePose) player).setForcedPose(Pose.SWIMMING);
        } else {
            ((IForcePose) player).setForcedPose(null);
        }
        dirty = false;
    }

    @Override
    public @Nullable EntityType<?> getPrevRiding() {
        return prevRiding;
    }
}
