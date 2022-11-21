package alpvax.mc.goprone.config;

import alpvax.mc.goprone.GoProne;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConfigOptions {
    private static final ForgeConfigSpec SPEC;
    private static final ConfigOptions INSTANCE;

    static final List<BooleanConfigSetting> ALL_SETTINGS = new ArrayList<>();

    public final BooleanConfigSetting jumpingAllowed = new BooleanConfigSetting(
            "isJumpingAllowed", true, "Can players jump while prone");
    public final BooleanConfigSetting allowedWhileFlying = new BooleanConfigSetting(
            "flying", true, "Allow while flying (applies any time the player is off the ground)");
    public final BooleanConfigSetting allowedWhileRiding = new BooleanConfigSetting("riding", false,
            "Allow while riding another entity",
            "If this is true, then you cannot go prone while riding any entities in the tag \"" +
                    GoProne.MODID +
                    ":blacklisted_entities\" but you can when riding any others",
            "If this is false, then you can go prone while riding any entities in the tag \"" +
                    GoProne.MODID +
                    ":whitelisted_entities\" but you cannot when riding any others"
    );
    public BooleanConfigSetting allowedWhileClimbing = new BooleanConfigSetting(
            "climbing", false, "Allow while climbing (applies any time the player is on a climbable block)");


    public final BooleanConfigSetting sprintingAllowed = new BooleanConfigSetting(
            "isSprintingAllowed", true, "Can players sprint while prone. Also controls whether sprinting is cancelled when going prone");

    private ConfigOptions(ForgeConfigSpec.Builder builder) {
        builder.comment("Toggles to allow/disable going prone in various circumstances").push("allowProne");
        ALL_SETTINGS.stream()
                .filter(setting -> setting != jumpingAllowed && setting != sprintingAllowed)
                .forEach(setting -> setting.createConfigValue(builder));
        builder.pop();
        builder.comment("Other options not related to when you can go prone").push("other");
        jumpingAllowed.createConfigValue(builder);
        sprintingAllowed.createConfigValue(builder);
        builder.pop();
    }

    public static ConfigOptions instance() {
        return INSTANCE;
    }

    private void bakeConfig() {
        for (BooleanConfigSetting s : ALL_SETTINGS) {
            s.bakeConfigValue();
        }
    }

    static {
        Pair<ConfigOptions, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ConfigOptions::new);
        SPEC = pair.getRight();
        INSTANCE = pair.getLeft();
    }

    public static void registerConfig(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, SPEC);
    }

    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == SPEC) {
            INSTANCE.bakeConfig();
            if (configEvent instanceof ModConfigEvent.Reloading) {
                GoProne.onConfigChange();
            }
        }
    }
}
