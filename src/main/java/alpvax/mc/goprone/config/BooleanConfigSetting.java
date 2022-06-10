package alpvax.mc.goprone.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BooleanConfigSetting {
    private final String key;
    private final boolean defaultValue;
    private final String[] configComment;
    private ForgeConfigSpec.ConfigValue<Boolean> configValue;

    private boolean value;

    BooleanConfigSetting(String key, boolean defaultVal, String... comment) {
        this.key = key;
        defaultValue = defaultVal;
        value = defaultVal;
        configComment = comment;
        ConfigOptions.ALL_SETTINGS.add(this);
    }

    void createConfigValue(ForgeConfigSpec.Builder builder) {
        configValue = builder.comment(configComment).define(key, defaultValue);
    }

    protected void bakeConfigValue() {
        value = configValue.get();
    }

    public boolean get() {
        return value;
    }
}
