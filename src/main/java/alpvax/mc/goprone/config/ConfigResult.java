package alpvax.mc.goprone.config;

import java.util.stream.Stream;

public enum ConfigResult {
    PASS,
    ALLOW,
    DENY;

    public static ConfigResult of(ConfigResult results) {
        return Stream.of(results).distinct().reduce(ConfigResult::with).orElse(PASS);
    }
    public ConfigResult with(ConfigResult other) {
        if (this == other) {
            return this;
        } else if (this == DENY || other == DENY) {
            return DENY;
        } else if (this == ALLOW || other == ALLOW) {
            return ALLOW;
        } else {
            return PASS;
        }
    }
}
