package alpvax.mc.goprone;

import alpvax.mc.goprone.config.ConfigOptions;
import alpvax.mc.goprone.network.PacketHandler;
import alpvax.mc.goprone.validation.RidingCheck;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;


@Mod(GoProne.MODID)
public class GoProne {
    public static final String MODID = "goprone";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public GoProne() {
        MinecraftForge.EVENT_BUS.register(this);
        PacketHandler.register();
        ConfigOptions.registerConfig(ModLoadingContext.get());
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modBus.addListener(ClientProxy::init));
        modBus.addListener(ConfigOptions::onModConfigEvent);
        modBus.addListener(this::registerCapability);
        modBus.addListener(this::gatherData);
        ModLoadingContext.get()
            .registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(
                () -> "ANY", //FMLNetworkConstants.IGNORESERVERONLY
                (remote, isServer) -> true
            ));
    }

//    private void setupClient(FMLClientSetupEvent event) {
//        event.enqueueWork(ClientProxy::init);
//    }

    public static void onConfigChange() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientProxy::onConfigChange);
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.getPlayerList()
                .getPlayers()
                .forEach(p -> p.getCapability(PlayerProneData.CAPABILITY).ifPresent(PlayerProneData::markDirty));
        }
    }

    private void registerCapability(RegisterCapabilitiesEvent event) {
        event.register(PlayerProneData.class);
    }

    @SubscribeEvent
    public void attachPlayerCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer player) { // Only attach capability on server side
            event.addCapability(new ResourceLocation(MODID, "player_cap"), new PlayerProneData.Provider(player));
        }
    }

    @SubscribeEvent
    public void clonePlayer(PlayerEvent.Clone event) {
        LOGGER.debug(event.getOriginal().getCapability(PlayerProneData.CAPABILITY));//XXX
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            event.player.getCapability(PlayerProneData.CAPABILITY).ifPresent(PlayerProneData::playerTick);
        }
    }

    /**
     * Must be equal to LivingEntity#SPEED_MODIFIER_SPRINTING_UUID, duplicated here to avoid use of AccessTransformer
     */
    private static final UUID LE_SPRINT_MOD_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    //TODO: Add Sprint Event
    public void onPlayerSprint(Player player) {
        if (player.getForcedPose() == Pose.SWIMMING && !ConfigOptions.instance().sprintingAllowed.get()) {
            player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(LE_SPRINT_MOD_UUID);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity().level.isClientSide) {
            var disabled = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ClientProxy::isDisabled);
            if (disabled != null && disabled) {
                return;
            }
        }
        if (event.getEntity() instanceof Player player && player.isOnGround() && player.getPose() == Pose.SWIMMING &&
            !ConfigOptions.instance().jumpingAllowed.get()) {
            var motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, 0, motion.z); //set y motion to 0
        }
    }

    private void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
//        gen.addProvider(event.includeClient(), new AALangProvider(gen));
        gen.addProvider(event.includeServer(), new RidingCheck.TagsProvider(gen, event.getExistingFileHelper()));
    }
}