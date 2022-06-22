package alpvax.mc.goprone.validation;

import net.minecraft.world.entity.player.Player;

public interface ProneCheck<T> {
    enum Result {
        UNCHANGED,
        PASSED,
        FAILED;

        public static Result pass(boolean previouslyPassed) {
            return previouslyPassed ? UNCHANGED : PASSED;
        }
        public static Result fail(boolean previouslyPassed) {
            return previouslyPassed ? UNCHANGED : FAILED;
        }
        public static Result of(boolean pass, boolean previouslyPassed) {
            return pass == previouslyPassed ? UNCHANGED : (pass ? PASSED : FAILED);
        }
    }

    Result test(Player player, boolean previouslyPassed, T oldValue);

//    default void updateProneData(PlayerEntity player, PlayerProneData proneData) {
//        proneData.updateCheckState(player, this, getCheckData(proneData));
//    }
//    default T getCheckData(PlayerProneData data) {
//        return null;
//    }
}
