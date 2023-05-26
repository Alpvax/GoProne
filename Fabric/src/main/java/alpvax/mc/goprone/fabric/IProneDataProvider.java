package alpvax.mc.goprone.fabric;

import alpvax.mc.goprone.PlayerProneData;

public interface IProneDataProvider {
    PlayerProneData getProneData();

//    @Override
//    default boolean shouldBeProne() {
//        return getRedirectedProneData().shouldBeProne();
//    }
//    @Override
//    default void setProne(boolean shouldBeProne) {
//        getRedirectedProneData().setProne(shouldBeProne);
//    }
//    @Override
//    @Nullable
//    default EntityType<?> getPrevRiding() {
//        return getRedirectedProneData().getPrevRiding();
//    }
//    @Override
//    default void setRiding(@Nullable EntityType<?> riding) {
//        getRedirectedProneData().setRiding(riding);
//    }
//    @Override
//    default void playerTick(Player player) {
//        getRedirectedProneData().playerTick(player);
//    }
}
