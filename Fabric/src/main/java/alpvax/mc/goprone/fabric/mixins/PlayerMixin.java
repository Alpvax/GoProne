package alpvax.mc.goprone.fabric.mixins;

import alpvax.mc.goprone.config.ConfigOptions;
import alpvax.mc.goprone.fabric.IForcePose;
import alpvax.mc.goprone.fabric.IProneDataProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IForcePose {
    private Pose forcedPose;

    @SuppressWarnings("ConstantConditions")
    protected PlayerMixin() {
        super(null, null);
        throw new AssertionError("Should not instantiate Mixin");
    }

    @Override
    public void setForcedPose(Pose forcedPose) {
        this.forcedPose = forcedPose;
        if (forcedPose != null) {
            setPose(forcedPose);
        }
    }

    @SuppressWarnings("ConstantConditions")
    protected Player getAsPlayer() {
        return (Player) (Object) this;
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    public void jump(CallbackInfo ci) {
        var player = getAsPlayer();
        if (player.onGround() && player.getPose() == Pose.SWIMMING && !ConfigOptions.instance().jumpingAllowed.get()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        var player = getAsPlayer();
        if (player instanceof IProneDataProvider data) {
            data.getProneData().playerTick();
        }
    }

    @Inject(method = "updatePlayerPose", at = @At("HEAD"), cancellable = true)
    public void updatePlayerPose(CallbackInfo ci) {
        if (forcedPose != null) {
            var player = getAsPlayer();
            if (player.isAlive()) {
                player.setPose(forcedPose);
                ci.cancel();
            }
        }
    }
}
