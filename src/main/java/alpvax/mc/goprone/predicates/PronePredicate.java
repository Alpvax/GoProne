package alpvax.mc.goprone.predicates;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
public class PronePredicate<T> {
  private final String configKey;
  private final String[] configComment;
  private final boolean defaultValue;
  @Nullable private final Function<ForgeConfigSpec.Builder, ForgeConfigSpec.ConfigValue<List<? extends T>>> exceptionValueFactory;

  private PronePredicate(
      String configKey, String[] configComment, boolean defaultValue,
      @Nullable Function<ForgeConfigSpec.Builder, ForgeConfigSpec.ConfigValue<List<? extends T>>> exceptionValueFactory
  ) {
    this.configKey = configKey;
    this.configComment = configComment;
    this.defaultValue = defaultValue;
    this.exceptionValueFactory = exceptionValueFactory;
  }
  public ConfigValues addToConfig(ForgeConfigSpec.Builder builder) {
    builder.comment(configComment);
    ConfigValues res;
    if (exceptionValueFactory != null) {
      builder.push(configKey);
      res = new ConfigValues(
        builder.define("allowed", defaultValue),
        exceptionValueFactory.apply(builder)
      );
      /*List<String> list = new ArrayList<>();
      list.add("Exceptions to the previous rule (if allowed is false, this counts as a whitelist; if true, counts as a blacklist).");
      if (exceptionCommentMapper != null) {
        list = exceptionCommentMapper.apply(list);
      }
      builder
          .comment(list.toArray(new String[0]))
          .defineList("exceptions", );*/
      builder.pop();
    } else {
      res = new ConfigValues(builder.define(configKey, defaultValue), null);
    }
    return res;
  }

  public class ConfigValues {
    private final ForgeConfigSpec.BooleanValue allowed;
    @Nullable private final ForgeConfigSpec.ConfigValue<List<? extends T>> exceptions;

    public ConfigValues(ForgeConfigSpec.BooleanValue allowed, @Nullable ForgeConfigSpec.ConfigValue<List<? extends T>> exceptions) {
      this.allowed = allowed;
      this.exceptions = exceptions;
    }
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }
  public static class Builder<T> {
    private String configKey;
    private String[] configComment = new String[] { "true if going prone should be allowed in this scenario" };
    @Nullable private Function<ForgeConfigSpec.Builder, ForgeConfigSpec.ConfigValue<List<? extends T>>> exceptionFactory;
    private List<T> defaultExceptions;

    public Builder<T> withComment(String... comment) {
      configComment = comment;
      return this;
    }
    public Builder<T> withExceptions(Function<ForgeConfigSpec.Builder, ForgeConfigSpec.ConfigValue<List<? extends T>>> exceptionValueFactory) {
      exceptionFactory = exceptionValueFactory;
      return this;
    }
    public Builder<T> addDefaultException(T exception) {
      defaultExceptions.add(exception);
      return this;
    }

    public PronePredicate<T> build(String configKey, boolean defaultValue) {
      return new PronePredicate<>(configKey, configComment, defaultValue, exceptionFactory); //TODO: defaultExceptions
    }
  }

}
