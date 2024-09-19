package com.github.shinjoy991.sunburndamage.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import java.util.List;

import static com.github.shinjoy991.sunburndamage.SunMechanic.getStringListConfig;
import static com.github.shinjoy991.sunburndamage.config.ReadConfig.updateConfig;

public class RemoveMobCommand {
    public RemoveMobCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("SunBurnDamageRemoveMob")
                .requires(commandSource -> commandSource.hasPermission(4)).executes((command) -> removeMobCommand(command.getSource())));
    }

    private int removeMobCommand(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.getItem() instanceof SpawnEggItem) {
            EntityType<?> entityType = ((SpawnEggItem) heldItem.getItem()).getType(heldItem.getTag());
            String entityName = EntityType.getKey(entityType).toString();

            List<String> affectMobNames = getStringListConfig("affectmobname");
            if (affectMobNames.contains(entityName)) {
                updateConfig("affectmobname", entityName, 0);
                player.sendSystemMessage(Component.literal("[SunBurnDamage] ").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                        .append(Component.literal("Mob removed from affect list: " + entityName).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN))));
            } else {
                player.sendSystemMessage(Component.literal("[SunBurnDamage] ").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                        .append(Component.literal("Mob not found in affect list: " + entityName).setStyle(Style.EMPTY.withColor(ChatFormatting.RED))));
            }
        } else {
            player.sendSystemMessage(Component.literal("[SunBurnDamage] ").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                    .append(Component.literal("You must be holding a spawn egg to use this command").setStyle(Style.EMPTY.withColor(ChatFormatting.RED))));
        }
        return 1;
    }
}
