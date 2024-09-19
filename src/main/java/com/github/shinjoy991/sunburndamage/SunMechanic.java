package com.github.shinjoy991.sunburndamage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

import static com.github.shinjoy991.sunburndamage.config.ReadConfig.getConfig;
import static com.github.shinjoy991.sunburndamage.helpers.DelayFunc.delayedTask;

@Mod.EventBusSubscriber
public class SunMechanic {

    private static final Map<UUID, Boolean> damagedEntities = new HashMap<>();

    public static List<String> getStringListConfig(String key) {
        Object configValue = getConfig(key);
        if (configValue instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) configValue;
            return list;
        } else {
            return List.of();
        }
    }

    private static void dosomething(LivingEntity entity) {
        UUID entityUUID = entity.getUUID();
        if (damagedEntities.getOrDefault(entityUUID, false))
            return;
        if (entity.level().isDay() && !entity.level().isClientSide) {
            float f = entity.getLightLevelDependentMagicValue();
            BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
            boolean flag = entity.isInWaterRainOrBubble() || entity.isInPowderSnow || entity.wasInPowderSnow;
            if (f > 0.5F && entity.level().random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && entity.level().canSeeSky(blockpos)) {

                damagedEntities.put(entityUUID, true);
                delayedTask(40, () -> {
                    damagedEntities.remove(entityUUID);
                });

                entity.setSecondsOnFire(3);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!getConfig("mechanic", "enable", 0).toString().equalsIgnoreCase("true"))
            return;
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player)
            return;
        List<String> affectMobNames = getStringListConfig("affectmobname");
        String entityName = entity.getEncodeId();
        if (affectMobNames.contains(entityName)) {
            dosomething(entity);
            return;
        }

        List<String> nonAffectMobNames = getStringListConfig("non-affectmobname");
        if (nonAffectMobNames.contains(entityName))
            return;

        if (entity.getMobType() == MobType.UNDEAD
                && getConfig("mechanic", "UNDEAD", 0).toString().equalsIgnoreCase("true"))
            dosomething(entity);
        else
            if (entity.getMobType() == MobType.ARTHROPOD
                    && getConfig("mechanic", "ARTHROPOD", 0).toString().equalsIgnoreCase("true"))
                dosomething(entity);
            else
                if (entity.getMobType() == MobType.ILLAGER
                        && getConfig("mechanic", "ILLAGER", 0).toString().equalsIgnoreCase("true"))
                    dosomething(entity);
                else
                    if (entity.getMobType() == MobType.WATER
                            && getConfig("mechanic", "WATER", 0).toString().equalsIgnoreCase("true"))
                        dosomething(entity);
                    else
                        if (entity.getMobType() == MobType.UNDEFINED
                                && getConfig("mechanic", "UNDEFINED", 0).toString().equalsIgnoreCase("true"))
                            dosomething(entity);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        String sourceId = event.getSource().getMsgId();

        if (sourceId.equals("onFire") || sourceId.equals("inFire")) {
            UUID entityUUID = entity.getUUID();
            if (!damagedEntities.getOrDefault(entityUUID, false)) {
                return;
            }
            ModNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new SpawnParticlesPacket(entity.getId()));
            int adddmg =
                    Math.max((Integer) getConfig("mechanic",
                            "damageadded", 1), 0);
            event.setAmount(event.getAmount() + adddmg);
        }
    }
}