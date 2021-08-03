package alpvax.mc.goprone.config;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ConfigSetting {
    private final String key;
    private final boolean defaultValue;
    private final String[] configComment;
    private final Predicate<Player> predicate;
    private final Set<ConfigException<?, ?>> exceptions = new HashSet<>();
    private ForgeConfigSpec.ConfigValue<Boolean> configValue;

    private boolean value;

    public ConfigSetting(String key, boolean defaultVal, Predicate<Player> predicate, String... comment) {
        this.key = key;
        defaultValue = defaultVal;
        value = defaultVal;
        configComment = comment.length < 1 ? new String[] {
                "Set to true if going prone should be allowed in this scenario, false otherwise"
            } : comment;
        this.predicate = predicate;
    }

    public void createConfigValue(ForgeConfigSpec.Builder builder) {
        builder.comment(configComment);
        if (exceptions.size() < 1) {
            configValue = addConfigValue(key, builder);
        } else {
            builder.push(key);
            configValue = addConfigValue("allowed", builder);
            addExceptionConfigValues(builder);
            builder.pop();
        }
    }
    protected ForgeConfigSpec.ConfigValue<Boolean> addConfigValue(String key, ForgeConfigSpec.Builder builder) {
        return builder.define(key, defaultValue);
    }
    protected void addExceptionConfigValues(ForgeConfigSpec.Builder builder) {
        exceptions.forEach(e -> e.createConfigValue(builder));
    }

    public <T, C> ConfigSetting withException(ConfigException<T, C> exception) {
        if (configValue != null) {
            throw new IllegalArgumentException("Config exceptions must be added before the config is generated!");
        }
        exceptions.add(exception.setParent(key));
        return this;
    }

    protected void bakeConfigValue() {
        value = configValue.get();
        exceptions.forEach(ConfigException::bakeValue);
    }

    public ConfigResult test(Player player) {
        return predicate.test(player)
                ? value != exceptions.stream().anyMatch(e -> e.test(player)) ? ConfigResult.ALLOW : ConfigResult.DENY
                : ConfigResult.PASS;
    }
}
