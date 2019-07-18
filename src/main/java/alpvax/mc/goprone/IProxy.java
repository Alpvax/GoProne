package alpvax.mc.goprone;

import net.minecraft.entity.player.PlayerEntity;

public interface IProxy {
  boolean onProneTick(PlayerEntity player, boolean previous);
}
