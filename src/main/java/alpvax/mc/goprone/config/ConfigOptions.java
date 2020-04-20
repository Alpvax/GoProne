package alpvax.mc.goprone.config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

public class ConfigOptions {
    public static final ForgeConfigSpec SPEC;
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

    static {
        SPEC = new ForgeConfigSpec.Builder().configure(ConfigOptions::makeConfig).getRight();
    }
    public static Void makeConfig(ForgeConfigSpec.Builder builder) {
        for (ConfigSetting s : ALLOW_SETTINGS) {
            s.createConfigValue(builder);
        }
        return null;
    }
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == SPEC) {
            for (ConfigSetting s: ALLOW_SETTINGS) {
                s.bakeConfigValue();
            }
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
