package com.github.shinjoy991.sunburndamage.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

import static com.github.shinjoy991.sunburndamage.SunMechanic.getStringListConfig;
import static com.github.shinjoy991.sunburndamage.config.ReadConfig.updateConfig;

public class RemoveMobCommand {
    public RemoveMobCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("SunBurnDamageRemoveMob")
                .requires(commandSource -> commandSource.hasPermission(4)).executes((command) -> removeMobCommand(command.getSource())));
    }

    private int removeMobCommand(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.getItem() instanceof SpawnEggItem) {
            EntityType<?> entityType = ((SpawnEggItem) heldItem.getItem()).getType(heldItem.getTag());
            String entityName = EntityType.getKey(entityType).toString();

            List<String> affectMobNames = getStringListConfig("affectmobname");
            if (affectMobNames.contains(entityName)) {
                updateConfig("affectmobname", entityName, 0);
                player.sendMessage(new StringTextComponent("[SunBurnDamage] ").setStyle(Style.EMPTY.withColor(TextFormatting.GOLD))
                        .append(new StringTextComponent("Mob removed from affect list: " + entityName).setStyle(Style.EMPTY.withColor(TextFormatting.GREEN))), player.getUUID());
            } else {
                player.sendMessage(new StringTextComponent("[SunBurnDamage] ").setStyle(Style.EMPTY.withColor(TextFormatting.GOLD))
                        .append(new StringTextComponent("Mob not found in affect list: " + entityName).setStyle(Style.EMPTY.withColor(TextFormatting.RED))), player.getUUID());
            }
        } else {
            player.sendMessage(new StringTextComponent("[SunBurnDamage] ").setStyle(Style.EMPTY.withColor(TextFormatting.GOLD))
                    .append(new StringTextComponent("You must be holding a spawn egg to use this command").setStyle(Style.EMPTY.withColor(TextFormatting.RED))), player.getUUID());
        }
        return 1;
    }
}
