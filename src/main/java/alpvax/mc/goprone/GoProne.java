package alpvax.mc.goprone;

import com.google.common.collect.Maps;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import ru.vidtu.goprone.Config;

import java.util.Map;
import java.util.UUID;

/**
 * Main mod initializer.
 * @author Alpvax
 * @author VidTu
 */
public class GoProne implements ModInitializer {
	public static final String MODID = "goprone";
	public static Map<UUID, Boolean> entityProneStates = Maps.newConcurrentMap();

	@Override
	public void onInitialize() {
		PacketHandler.register();
		ServerLifecycleEvents.SERVER_STARTED.register(Config::load);
	}

	public static void setProne(UUID playerID, boolean prone) {
		entityProneStates.put(playerID, prone);
	}

	public static void onPlayerTick(PlayerEntity pe) {
		if (pe.world.isClient) {
			ClientProxy.updateProneState(pe);
		}
		if (entityProneStates.getOrDefault(pe.getUuid(), false) && Config.test(pe)) {
			pe.setPose(EntityPose.SWIMMING);
		}
	}

	public static void onPlayerJump(PlayerEntity player) {
		if (player.isOnGround() && player.getPose() == EntityPose.SWIMMING && !Config.isJumpingAllowed) {
			Vec3d motion = player.getVelocity();
			player.setVelocity(motion.add(0, -motion.y, 0)); // set y motion to 0
		}
	}
}