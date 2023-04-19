package alpvax.mc.goprone.validation;

import alpvax.mc.goprone.config.ConfigOptions;

import java.util.EnumSet;

public enum Checks {
    RIDING,
    FLYING,
    CLIMBING;

    public boolean configValue() {
        ConfigOptions config = ConfigOptions.instance();
        return switch (this) {
            case FLYING -> config.allowedWhileFlying.get();
            case RIDING -> config.allowedWhileRiding.get();
            case CLIMBING -> config.allowedWhileClimbing.get();
        };
    }

    /**
     * Called when the criteria matches (e.g. called on FLYING when the player is in the air)
     *
     * @param applicable   true if the player is in the scenario described by this check.
     * @param failedChecks the enumset to update.
     * @return true if the checks have changed from last time (i.e. the result of the check has changed)
     */
    public boolean updateFailed(boolean applicable, EnumSet<Checks> failedChecks) {
        return configValue() || !applicable ? failedChecks.remove(this) : failedChecks.add(this);
    }
}