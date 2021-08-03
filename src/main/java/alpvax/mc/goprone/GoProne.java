package alpvax.mc.goprone;

import alpvax.mc.goprone.config.ConfigOptions;
import com.google.common.collect.Maps;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;

@Mod(GoProne.MODID)
public class GoProne {
  public static final String MODID = "goprone";

  // Directly reference a log4j logger.
  private static final Logger LOGGER = LogManager.getLogger();

  public GoProne() {
    MinecraftForge.EVENT_BUS.register(this);
    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientProxy::init);
    PacketHandler.register();
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigOptions.SPEC);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfigOptions::onModConfigEvent);
  }

  static final Map<UUID, Boolean> entityProneStates = Maps.newConcurrentMap();

  public static void setProne(UUID playerID, boolean prone) {
    entityProneStates.put(playerID, prone);
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      if (event.player.level.isClientSide) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientProxy.updateProneState(event.player));
      }//*/
      if (entityProneStates.getOrDefault(event.player.getUUID(), false) && ConfigOptions.test(event.player)) {
        event.player.setPose(Pose.SWIMMING);
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
    if (event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntityLiving();
      if (player.isOnGround() && player.getPose() == Pose.SWIMMING && !ConfigOptions.isJumpingAllowed()) {
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x, 0, motion.z); //set y motion to 0
      }
    }
  }
}