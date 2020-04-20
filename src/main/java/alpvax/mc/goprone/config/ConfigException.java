package alpvax.mc.goprone.config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Predicate;


public abstract class ConfigException<T, C> implements Predicate<PlayerEntity> {
    private ForgeConfigSpec.ConfigValue<C> configValue;
    private T bakedValue;
    //private String parent;
    protected abstract ForgeConfigSpec.ConfigValue<C> makeConfigValue(ForgeConfigSpec.Builder builder);
    protected abstract T map(C configVal);
    protected abstract boolean test(T value, PlayerEntity player);

    ConfigException<T, C> setParent(String parent) {
        //this.parent = parent;
        return this;
    }
    /*public String getParentSetting() {
        return parent;
    }*/
    public final void createConfigValue(ForgeConfigSpec.Builder builder) {
        configValue = makeConfigValue(builder);
    }
    protected void bakeValue() {
        bakedValue = map(configValue.get());
    }

    @Override
    public boolean test(PlayerEntity player) {
        return test(bakedValue, player);
    }

    @FunctionalInterface
    interface ConfigPredicate<U> {
        boolean test(U value, PlayerEntity player);
    }
}
