package alpvax.mc.goprone.config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConfigExceptionList<T> extends ConfigException<List<T>, List<? extends String>> {
    private final String[] configComment;
    private final Supplier<List<String>> defaults;
    private final Function<T, String> toStringMapper;
    private final Function<String, T> toValueMapper;
    private final Predicate<Object> validator;
    private final ConfigPredicate<T> predicate;

    private ConfigExceptionList(Supplier<List<String>> defaults,
                                Function<T, String> toStringMapper,
                                Function<String, T> toValueMapper,
                                Predicate<Object> configValidator,
                                ConfigPredicate<T> predicate,
                                String... comment) {
        this.defaults = defaults;
        this.toStringMapper = toStringMapper;
        this.toValueMapper = toValueMapper;
        validator = configValidator;
        this.predicate = predicate;
        configComment = comment;
    }

    @Override
    protected ForgeConfigSpec.ConfigValue<List<? extends String>> makeConfigValue(ForgeConfigSpec.Builder builder) {
        return builder.comment(configComment).defineList("exceptions", defaults::get, validator); // WTF!?
    }

    @Override
    protected List<T> map(List<? extends String> configVal) {
        return configVal.stream().map(toValueMapper).collect(Collectors.toList());
    }

    @Override
    protected boolean test(List<T> value, PlayerEntity player) {
        return value.stream().anyMatch(v -> predicate.test(v, player));
    }

    public static class Builder<T> {
        private final Function<T, String> toStringMapper;
        private final Function<String, T> toValueMapper;
        private Predicate<Object> validator;
        private String[] comment;
        private final List<Supplier<String>> defaults = new ArrayList<>();

        public Builder(Function<T, String> toStringMapper, Function<String, T> toValueMapper) {
            this.toStringMapper = toStringMapper;
            this.toValueMapper = toValueMapper;
            validator = s -> s instanceof String && toValueMapper.apply((String)s) != null;
        }
        public Builder<T> setValidator(Predicate<Object> configValueValidator) {
            validator = configValueValidator;
            return this;
        }
        public Builder<T> setComment(String... configComment) {
            comment = configComment;
            return this;
        }
        public Builder<T> withDefaults(Supplier<String>... defaults) {
            this.defaults.addAll(Arrays.asList(defaults));
            return this;
        }
        public Builder<T> withDefaults(String... defaults) {
            for (String s : defaults) {
                this.defaults.add(() -> s);
            }
            return this;
        }
        public ConfigExceptionList<T> build(ConfigPredicate<T> predicate) {
            return new ConfigExceptionList<>(
                    () -> defaults.stream().map(Supplier::get).collect(Collectors.toList()),
                    toStringMapper, toValueMapper, validator, predicate, comment
            );
        }
    }
}
