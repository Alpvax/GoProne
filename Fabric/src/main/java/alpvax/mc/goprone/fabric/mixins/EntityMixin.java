package alpvax.mc.goprone.fabric.mixins;

import alpvax.mc.goprone.fabric.IProneDataProvider;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantConditions")
@Mixin({Entity.class})
public abstract class EntityMixin {

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("RETURN"))
    public void startRiding(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && this instanceof IProneDataProvider p) {
            var riding = ((Entity)(Object)this).getVehicle();
            p.getProneData().setRiding(riding == null ? null : riding.getType());
        }
    }
}
