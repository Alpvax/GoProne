package alpvax.mc.goprone.forge;

import alpvax.mc.goprone.PlayerProneData;
import alpvax.mc.goprone.config.ConfigOptions;
import alpvax.mc.goprone.forge.datagen.TagsProvider;
import alpvax.mc.goprone.forge.network.ForgePacketHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.concurrent.CompletableFuture;

import static alpvax.mc.goprone.GPConstants.MODID;


@Mod(MODID)
public class GoProne {
    public GoProne() {
        MinecraftForge.EVENT_BUS.register(this);
        ForgePacketHandler.initAndRegister();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigOptions.SPEC);
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modBus.addListener(ClientProxy::init));
        modBus.addListener(this::onModConfigEvent);
        modBus.addListener(this::registerCapability);
        modBus.addListener(this::gatherData);
        ModLoadingContext.get()
            .registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(
                () -> "ANY", //FMLNetworkConstants.IGNORESERVERONLY
                (remote, isServer) -> true
            ));
    }
    private void onModConfigEvent(final ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == ConfigOptions.SPEC) {
            ConfigOptions.instance().bakeConfig();
            if (configEvent instanceof ModConfigEvent.Reloading) {
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientProxy::onConfigChange);
                var server = ServerLifecycleHooks.getCurrentServer();
                if (server != null) {
                    server.getPlayerList()
                            .getPlayers()
                            .forEach(p -> p.getCapability(ProneDataCapabilityProvider.CAPABILITY).ifPresent(PlayerProneData::markDirty));
                }
            }
        }
    }

    private void registerCapability(RegisterCapabilitiesEvent event) {
        event.register(PlayerProneData.class);
    }

    @SubscribeEvent
    public void attachPlayerCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer player) { // Only attach capability on server side
            event.addCapability(new ResourceLocation(MODID, "player_cap"), new ProneDataCapabilityProvider(player));
        }
    }

//    @SubscribeEvent
//    public void clonePlayer(PlayerEvent.Clone event) {
//        LOGGER.debug(event.getOriginal().getCapability(ProneDataCapability.CAPABILITY));//XXX
//    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            event.player.getCapability(ProneDataCapabilityProvider.CAPABILITY).ifPresent(PlayerProneData::playerTick);
            onPlayerSprint(event.player);
        }
    }

    //TODO: Add Sprint Event
    public void onPlayerSprint(Player player) {
        if (player.getForcedPose() == Pose.SWIMMING && !ConfigOptions.instance().sprintingAllowed.get()) {
            player.setSprinting(false);
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
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
//        gen.addProvider(event.includeClient(), new AALangProvider(gen));
        gen.addProvider(event.includeServer(), new TagsProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
    }
}