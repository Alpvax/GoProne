package alpvax.mc.goprone.mixins;

import alpvax.mc.goprone.IForcePose;
import alpvax.mc.goprone.IProneData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.vidtu.Config;

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

    @Inject(method = "jumpFromGround", at = @At("TAIL"), cancellable = true)
    public void jump(CallbackInfo ci) {
        var player = getAsPlayer();
        if (player.isOnGround() && player.getPose() == Pose.SWIMMING && !Config.isJumpingAllowed) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        var player = getAsPlayer();
        if (player instanceof IProneData data) {
            data.playerTick(player);
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
