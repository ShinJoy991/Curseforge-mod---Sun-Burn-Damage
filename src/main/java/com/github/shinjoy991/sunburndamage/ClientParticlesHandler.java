package com.github.shinjoy991.sunburndamage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;

@SuppressWarnings("resources")
public class ClientParticlesHandler {

    public static void sunBurnPar(int entityId) {

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel world = minecraft.level;
        if (world != null) {
            Entity entity = world.getEntity(entityId);
            if (entity == null)
                return;
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            int count = 30;

            for (int i = 0; i < count; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 0.5;
                double offsetY = (world.random.nextDouble() - 0.5) * 0.5;
                double offsetZ = (world.random.nextDouble() - 0.5) * 0.5;
                world.addParticle(ParticleTypes.LAVA, x + offsetX, y + offsetY, z + offsetZ, 0, 0, 0);
            }
        }
    }
}
