package com.github.shinjoy991.sunburndamage.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

import static com.github.shinjoy991.sunburndamage.config.ReadConfig.reloadConfig;

public class ReloadCommand {

    public ReloadCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("SunBurnDamageReload")
                .requires(commandSource -> commandSource.hasPermission(4)).executes((command) -> CustomCommand1a(command.getSource())));
    }

    private int CustomCommand1a(CommandSourceStack source) throws CommandSyntaxException {

        ServerPlayer player = source.getPlayerOrException();
        if (!player.level().isClientSide && player.level().getServer() != null) {
            if (reloadConfig() == 0) {
                MutableComponent message = Component.literal("[SunBurnDamage]")
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                        .append(Component.literal(" Reloaded").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                player.sendSystemMessage(message);

            } else {
                MutableComponent message = Component.literal("[SunBurnDamage]")
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                        .append(Component.literal(" Reload Error!!").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                player.sendSystemMessage(message);
            }
        }
        return 0;
    }
}