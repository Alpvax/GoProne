package alpvax.mc.goprone.fabric.mixins;

import alpvax.mc.goprone.PlayerProneData;
import alpvax.mc.goprone.fabric.IProneDataProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerPlayer.class})
public abstract class ServerPlayerMixin extends PlayerMixin implements IProneDataProvider {
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
}
