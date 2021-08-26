package ru.vidtu.goprone.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import alpvax.mc.goprone.GoProne;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	@Inject(method = "jump", at = @At("TAIL"))
	public void jump(CallbackInfo ci) {
		GoProne.onPlayerJump((PlayerEntity) (Object) this);
	}
	
	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		GoProne.onPlayerTick((PlayerEntity) (Object) this);
	}
}
