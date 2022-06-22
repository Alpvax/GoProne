package alpvax.mc.goprone.mixins;

import alpvax.mc.goprone.IProneData;
import alpvax.mc.goprone.PlayerProneData;
import alpvax.mc.goprone.RedirectedProneData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ServerPlayer.class, LocalPlayer.class})
public abstract class ProneablePlayerMixin extends PlayerMixin implements RedirectedProneData {
    private PlayerProneData proneData;
    @Override
    public IProneData getRedirectedProneData() {
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
            setRiding(riding == null ? null : riding.getType());
        }
    }
}
