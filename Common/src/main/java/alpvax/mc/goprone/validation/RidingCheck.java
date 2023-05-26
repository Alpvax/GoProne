package alpvax.mc.goprone.validation;

import alpvax.mc.goprone.GPConstants;
import alpvax.mc.goprone.config.ConfigOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class RidingCheck {
    public static final TagKey<EntityType<?>> ENTITY_BLACKLIST = TagKey.create(
        Registries.ENTITY_TYPE, new ResourceLocation(GPConstants.MODID, "blacklisted_entities"));
    public static final TagKey<EntityType<?>> ENTITY_WHITELIST = TagKey.create(
        Registries.ENTITY_TYPE, new ResourceLocation(GPConstants.MODID, "whitelisted_entities"));

    public static boolean checkEntityType(EntityType<?> type) {
        if (ConfigOptions.instance().allowedWhileRiding.get()) {
            return !type.is(ENTITY_BLACKLIST);
        }
        return type.is(ENTITY_WHITELIST);
    }
}
