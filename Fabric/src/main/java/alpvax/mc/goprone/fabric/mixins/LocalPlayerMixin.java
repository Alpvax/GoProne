package alpvax.mc.goprone.fabric.mixins;

import alpvax.mc.goprone.PlayerProneData;
import alpvax.mc.goprone.fabric.IProneDataProvider;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LocalPlayer.class})
public abstract class LocalPlayerMixin extends PlayerMixin implements IProneDataProvider {
    private PlayerProneData proneData;
    @Override
    public PlayerProneData getProneData() {
        return proneData;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initPlayerData(CallbackInfo ci) {
        var player = getAsPlayer();
        proneData = new PlayerProneData(player);
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("RETURN"))
    public void startRiding(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            var riding = getVehicle();
            proneData.setRiding(riding == null ? null : riding.getType());
        }
    }
}
