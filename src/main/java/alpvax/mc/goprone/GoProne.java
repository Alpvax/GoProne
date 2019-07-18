package alpvax.mc.goprone;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(GoProne.MODID)
public class GoProne {
  public static final String MODID = "goprone";

  // Directly reference a log4j logger.
  private static final Logger LOGGER = LogManager.getLogger();

  public static IProxy proxy = DistExecutor.runForDist(()-> ()-> new ClientProxy(), () -> () -> new IProxy(){
    @Override
    public boolean onProneTick(PlayerEntity player, boolean previous) {
      return entityProneStates.getOrDefault(player.getUniqueID(), false);
    }
  });

  public GoProne() {
    MinecraftForge.EVENT_BUS.register(this);
    PacketHandler.register();
  }

  private static final Method setPose = ObfuscationReflectionHelper.findMethod(Entity.class, "func_213301_b", Pose.class);

  static Map<UUID, Boolean> entityProneStates = Maps.newConcurrentMap();

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      boolean isProne = entityProneStates.getOrDefault(event.player.getUniqueID(), false);
      entityProneStates.put(event.player.getUniqueID(), proxy.onProneTick(event.player, isProne));
      if (isProne) {
        try {
          setPose.invoke(event.player, Pose.SWIMMING);
        } catch (IllegalAccessException | InvocationTargetException e) {
          LOGGER.error("Error setting player prone: " + event.player.getDisplayNameAndUUID(), e);
        }
      }
    }
  }
}