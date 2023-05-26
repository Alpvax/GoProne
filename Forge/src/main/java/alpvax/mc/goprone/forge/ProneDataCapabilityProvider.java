package alpvax.mc.goprone.forge;

import alpvax.mc.goprone.PlayerProneData;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProneDataCapabilityProvider implements ICapabilityProvider {
    public static Capability<PlayerProneData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    private final PlayerProneData data;
    private final LazyOptional<PlayerProneData> holder; //TODO: Invalidate when player dies?
    public ProneDataCapabilityProvider(Player player) {
        data = new PlayerProneData(player);
        holder = LazyOptional.of(() -> data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(
        @NotNull Capability<T> cap, @Nullable Direction side) {
        return CAPABILITY.orEmpty(cap, holder);
    }
}
