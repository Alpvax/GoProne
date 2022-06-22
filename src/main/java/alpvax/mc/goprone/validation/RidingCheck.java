package alpvax.mc.goprone.validation;

import alpvax.mc.goprone.GoProne;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import ru.vidtu.Config;

public class RidingCheck implements ProneCheck<EntityType<?>> {
    public static final TagKey<EntityType<?>> ENTITY_BLACKLIST = TagKey.create(
        Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(GoProne.MODID, "blacklisted_entities"));
    public static final TagKey<EntityType<?>> ENTITY_WHITELIST = TagKey.create(
        Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(GoProne.MODID, "whitelisted_entities"));

    public static final RidingCheck INSTANCE = new RidingCheck();

    private RidingCheck() {
    }

    public static boolean checkEntityType(EntityType<?> type) {
        if (getConfigValue()) {
            return !type.is(ENTITY_BLACKLIST);
        }
        return type.is(ENTITY_WHITELIST);
    }

    public static boolean getConfigValue() {
        return Config.riding;
    }

    @Override
    public Result test(Player player, boolean previouslyPassed, EntityType<?> oldValue) {
        var riding = player.getVehicle();
        if (riding != null) {
            var t = riding.getType();
            return t == oldValue ? Result.UNCHANGED : Result.of(checkEntityType(t), previouslyPassed);
        } else {
            return Result.pass(previouslyPassed);
        }
    }
}
