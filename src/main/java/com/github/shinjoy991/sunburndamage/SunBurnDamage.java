package com.github.shinjoy991.sunburndamage;

import com.github.shinjoy991.sunburndamage.config.CreateJson;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.github.shinjoy991.sunburndamage.config.ReadConfig.readJsonValue;

@Mod(SunBurnDamage.MODID)
public class SunBurnDamage {

    public static final String MODID = "sunburndamage";
    public static final Logger LOGGER = LogManager.getLogger();

    public SunBurnDamage() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        CreateJson.CreateJsonConfigFile();
        readJsonValue(CreateJson.configFile);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetworking.registerPackets();
        LOGGER.info("HELLO SunBurnDamage mod from common setup");
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("HELLO from SunBurnDamage mod ... server starting");
    }

    }
