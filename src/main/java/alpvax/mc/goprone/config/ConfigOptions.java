package alpvax.mc.goprone.config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigOptions {
    public static final ForgeConfigSpec SPEC;
    private static final ConfigOptions INSTANCE;
    private static final ConfigSetting[] ALLOW_SETTINGS = new ConfigSetting[]{
            new ConfigSetting("flying", true, p -> !p.onGround, "Allow while flying"),
            new ConfigSetting("riding", false, Entity::isPassenger, "Allow while riding another entity")
                    .withException(
                    new ConfigExceptionList.Builder<EntityType<?>>(
                            e -> e.getRegistryName().toString(),
                            s -> ForgeRegistries.ENTITIES.getValue(new ResourceLocation(s))
                    ).setComment("A list of exceptions to the rule.",
                            "If allowed is true, this works as a blacklist",
                            "If allowed is false, this works as a whitelist"
                    ).build((value, player) -> player.isPassenger() && player.getRidingEntity().getType() == value)
            )
    };
    private final ForgeConfigSpec.BooleanValue isJumpingAllowedCV;
    private boolean jumpingAllowed = true;
    private ConfigOptions(ForgeConfigSpec.Builder builder) {
        builder.comment("Toggles to allow/disable going prone in various circumstances").push("allowProne");
        for (ConfigSetting s : ALLOW_SETTINGS) {
            s.createConfigValue(builder);
        }
        builder.pop();
        builder.comment("Other options not related to when you can go prone").push("other");
        isJumpingAllowedCV = builder.comment("Can players jump while prone").define("isJumpingAllowed", jumpingAllowed);
        builder.pop();
    }

    private void bakeConfig() {
        for (ConfigSetting s: ALLOW_SETTINGS) {
            s.bakeConfigValue();
        }
        jumpingAllowed = isJumpingAllowedCV.get();
    }

    public static boolean isJumpingAllowed() {
        return INSTANCE.jumpingAllowed;
    }

    static {
        Pair<ConfigOptions, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ConfigOptions::new);
        SPEC = pair.getRight();
        INSTANCE = pair.getLeft();
    }
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == SPEC) {
            INSTANCE.bakeConfig();
        }
    }
    public static boolean test(PlayerEntity player) {
        for (ConfigSetting s : ALLOW_SETTINGS) {
            if (s.test(player) == ConfigResult.DENY) {
                return false;
            }
        }
        return true;
    }
}
