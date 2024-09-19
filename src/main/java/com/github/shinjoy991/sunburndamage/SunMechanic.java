package com.github.shinjoy991.sunburndamage;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

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
            return Collections.emptyList();
        }
    }

    private static void dosomething(LivingEntity entity) {
        UUID entityUUID = entity.getUUID();
        if (damagedEntities.getOrDefault(entityUUID, false))
            return;
        if (entity.level.isDay() && !entity.level.isClientSide) {
            float f = entity.getBrightness();
            BlockPos blockpos = entity.getVehicle() instanceof BoatEntity ? (new BlockPos(entity.getX(), (double)Math.round(entity.getY()), entity.getZ())).above() : new BlockPos(entity.getX(), (double)Math.round(entity.getY()), entity.getZ());
            if (f > 0.5F && entity.level.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && entity.level.canSeeSky(blockpos)) {
                damagedEntities.put(entityUUID, true);
                delayedTask(40, () -> {
                    damagedEntities.remove(entityUUID);
                });

                entity.setSecondsOnFire(3);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingUpdateEvent event) {
        if (!getConfig("mechanic", "enable", 0).toString().equalsIgnoreCase("true"))
            return;
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof PlayerEntity)
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

        if (entity.getMobType() == CreatureAttribute.UNDEAD
                && getConfig("mechanic", "UNDEAD", 0).toString().equalsIgnoreCase("true"))
            dosomething(entity);
        else
            if (entity.getMobType() == CreatureAttribute.ARTHROPOD
                    && getConfig("mechanic", "ARTHROPOD", 0).toString().equalsIgnoreCase("true"))
                dosomething(entity);
            else
                if (entity.getMobType() == CreatureAttribute.ILLAGER
                        && getConfig("mechanic", "ILLAGER", 0).toString().equalsIgnoreCase("true"))
                    dosomething(entity);
                else
                    if (entity.getMobType() == CreatureAttribute.WATER
                            && getConfig("mechanic", "WATER", 0).toString().equalsIgnoreCase("true"))
                        dosomething(entity);
                    else
                        if (entity.getMobType() == CreatureAttribute.UNDEFINED
                                && getConfig("mechanic", "UNDEFINED", 0).toString().equalsIgnoreCase("true"))
                            dosomething(entity);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
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