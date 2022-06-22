package alpvax.mc.goprone.validation;


import net.minecraft.world.entity.player.Player;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class SimpleCheck implements ProneCheck<Void> {
    private final BooleanSupplier configGetter;
    private final Predicate<Player> applicable;
    public SimpleCheck(BooleanSupplier configValueGetter, Predicate<Player> applicablePredicate) {
        configGetter = configValueGetter;
        applicable = applicablePredicate;
    }

    @Override
    public Result test(Player player, boolean previouslyPassed, Void oldValue) {
        return (configGetter.getAsBoolean() || !applicable.test(player))
               ? Result.pass(previouslyPassed)
               : Result.fail(previouslyPassed);
    }
}
