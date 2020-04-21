package alpvax.mc.goprone;

import alpvax.mc.goprone.config.ConfigOptions;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigOptions.SPEC);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfigOptions::onModConfigEvent);
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
      if (entityProneStates.getOrDefault(event.player.getUniqueID(), false) && ConfigOptions.test(event.player)) {
        try {
          setPose.invoke(event.player, Pose.SWIMMING);
        } catch (IllegalAccessException | InvocationTargetException e) {
          LOGGER.error("Error setting player prone: " + event.player.getDisplayNameAndUUID(), e);
        }
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
    if (event.getEntity() instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) event.getEntityLiving();
      if (player.onGround && player.getPose() == Pose.SWIMMING && !ConfigOptions.isJumpingAllowed()) {
        Vec3d motion = player.getMotion();
        player.setMotion(motion.add(0, -motion.y, 0)); //set y motion to 0
      }
    }
  }
}