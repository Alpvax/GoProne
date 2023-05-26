package alpvax.mc.goprone.forge.datagen;

import alpvax.mc.goprone.GPConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static alpvax.mc.goprone.validation.RidingCheck.ENTITY_BLACKLIST;
import static alpvax.mc.goprone.validation.RidingCheck.ENTITY_WHITELIST;

public class TagsProvider  extends EntityTypeTagsProvider {
    public TagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, ExistingFileHelper helper) {
        super(pOutput, pProvider, GPConstants.MODID, helper);
    }

    @Override
    public void addTags(HolderLookup.Provider pProvider) {
        tag(ENTITY_BLACKLIST)
                .add(EntityType.MINECART) // Add to prevent the issue of ending up stuck beneath the minecart
                .addOptional(new ResourceLocation("sit:entity_sit"));
        tag(ENTITY_WHITELIST);
    }

    @Override
    public @NotNull String getName() {
        return "GoProne Entity Tags";
    }
}
