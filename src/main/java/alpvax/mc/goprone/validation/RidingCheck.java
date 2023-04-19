package alpvax.mc.goprone.validation;

import alpvax.mc.goprone.GoProne;
import alpvax.mc.goprone.config.ConfigOptions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RidingCheck {
    public static final TagKey<EntityType<?>> ENTITY_BLACKLIST = TagKey.create(
        Registries.ENTITY_TYPE, new ResourceLocation(GoProne.MODID, "blacklisted_entities"));
    public static final TagKey<EntityType<?>> ENTITY_WHITELIST = TagKey.create(
        Registries.ENTITY_TYPE, new ResourceLocation(GoProne.MODID, "whitelisted_entities"));

    public static boolean checkEntityType(EntityType<?> type) {
        if (ConfigOptions.instance().allowedWhileRiding.get()) {
            return !type.is(ENTITY_BLACKLIST);
        }
        return type.is(ENTITY_WHITELIST);
    }
    public static class TagsProvider extends EntityTypeTagsProvider {
        public TagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, ExistingFileHelper helper) {
            super(pOutput, pProvider, GoProne.MODID, helper);
        }

        @Override
        public void addTags(HolderLookup.Provider pProvider) {
            tag(ENTITY_BLACKLIST)
                .add(EntityType.MINECART) // Add to prevent the issue of ending up stuck beneath the minecart
                .addOptional(new ResourceLocation("sit:entity_sit"));
        }

        @Override
        public @NotNull String getName() {
            return "GoProne Entity Tags";
        }
    }
}
