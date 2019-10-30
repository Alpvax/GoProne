package alpvax.mc.goprone;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
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

  public GoProne() {
    MinecraftForge.EVENT_BUS.register(this);
    DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientProxy.init());
    PacketHandler.register();
  }

  private static final Method setPose = ObfuscationReflectionHelper.findMethod(Entity.class, "func_213301_b", Pose.class);

  static Map<UUID, Boolean> entityProneStates = Maps.newConcurrentMap();

  public static void setProne(UUID playerID, boolean prone) {
    entityProneStates.put(playerID, prone);
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      if (event.player.world.isRemote) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientProxy.updateProneState(event.player));
      }//*/
      if (entityProneStates.getOrDefault(event.player.getUniqueID(), false)) {
        try {
          setPose.invoke(event.player, Pose.SWIMMING);
        } catch (IllegalAccessException | InvocationTargetException e) {
          LOGGER.error("Error setting player prone: " + event.player.getDisplayNameAndUUID(), e);
        }
      }
    }
  }
}