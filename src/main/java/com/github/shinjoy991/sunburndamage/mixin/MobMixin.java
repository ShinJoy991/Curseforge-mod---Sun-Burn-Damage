package com.github.shinjoy991.sunburndamage.mixin;

import net.minecraft.entity.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.github.shinjoy991.sunburndamage.SunMechanic.getStringListConfig;

@Mixin(MobEntity.class)
public class MobMixin {
    @Unique
    MobEntity sunBurnDamage$mob = (MobEntity) (Object) this;
    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void protectMobsFromSun(CallbackInfoReturnable<Boolean> cir) {

        String entityName = sunBurnDamage$mob.getEncodeId();
        List<String> affectMobNames = getStringListConfig("non-affectmobname");
        if (affectMobNames.contains(entityName)) {
            cir.setReturnValue(false);
        }
    }
}
