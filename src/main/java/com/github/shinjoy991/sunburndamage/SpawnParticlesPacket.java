package com.github.shinjoy991.sunburndamage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpawnParticlesPacket {

    private final int entityId;

    public SpawnParticlesPacket(int entityId) {
        this.entityId = entityId;
    }

    public SpawnParticlesPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        ctx.get().enqueueWork(() -> {
            if (FMLEnvironment.dist.isClient()) {
                ClientParticlesHandler.sunBurnPar(entityId);
            }
        });
        context.setPacketHandled(true);
    }
}