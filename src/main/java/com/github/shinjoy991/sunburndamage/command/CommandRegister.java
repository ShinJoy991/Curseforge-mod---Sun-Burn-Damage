package com.github.shinjoy991.sunburndamage.command;

import com.github.shinjoy991.sunburndamage.SunBurnDamage;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = SunBurnDamage.MODID)
public class CommandRegister {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {

        new ReloadCommand(event.getDispatcher());
        new AddMobCommand(event.getDispatcher());
        new RemoveMobCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}