package alpvax.mc.goprone.validation;

import net.minecraft.world.entity.player.Player;
import ru.vidtu.Config;

public enum SimpleChecks implements ProneCheck<Void> {
    FLYING,
    CLIMBING;

    public boolean configValue() {
        return switch (this) {
            case FLYING -> Config.flying;
            case CLIMBING -> Config.climbing;
        };
    }

    private boolean isApplicable(Player player) {
        return switch (this) {
            case FLYING -> player.isOnGround();
            case CLIMBING -> player.onClimbable();
        };
    }

    @Override
    public Result test(Player player, boolean previouslyPassed, Void oldValue) {
        return (configValue() || !isApplicable(player)) ? Result.pass(previouslyPassed) : Result.fail(previouslyPassed);
    }
}
