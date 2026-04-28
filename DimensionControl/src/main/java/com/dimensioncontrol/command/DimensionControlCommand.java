package com.dimensioncontrol.command;

import com.dimensioncontrol.config.DimensionConfig;
import com.dimensioncontrol.permission.PermissionManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DimensionControlCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("dimensioncontrol")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("reload")
                    .executes(DimensionControlCommand::reload))
                .then(CommandManager.literal("gui")
                    .executes(DimensionControlCommand::openGui))
                .then(CommandManager.literal("dimension")
                    .then(CommandManager.argument("dimension", StringArgumentType.string())
                        .then(CommandManager.literal("enable")
                            .executes(DimensionControlCommand::enableDimension))
                        .then(CommandManager.literal("disable")
                            .executes(DimensionControlCommand::disableDimension))))
                .then(CommandManager.literal("permission")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.literal("set")
                            .then(CommandManager.argument("level", StringArgumentType.string())
                                .executes(DimensionControlCommand::setPermission)))
                        .then(CommandManager.literal("get")
                            .executes(DimensionControlCommand::getPermission))
                        .then(CommandManager.literal("remove")
                            .executes(DimensionControlCommand::removePermission)))));
        });
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        DimensionConfig.load();
        context.getSource().sendFeedback(() -> 
            Text.literal("§aConfiguration reloaded successfully!"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int openGui(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, p) -> GenericContainerScreenHandler.createGeneric9x6(syncId, inventory),
                Text.literal("§6Dimension Control§r")
            ));
            
            player.sendMessage(Text.literal("§ePlace items you want to block in this dimension, then close the menu to save."), false);
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("§cOnly players can open the GUI!"));
            return 0;
        }
    }

    private static int enableDimension(CommandContext<ServerCommandSource> context) {
        String dimensionStr = StringArgumentType.getString(context, "dimension");
        Identifier dimensionId = Identifier.of(dimensionStr);
        
        DimensionConfig.DimensionRestrictions restrictions = 
            DimensionConfig.getInstance().getRestrictions(dimensionId);
        restrictions.enabled = true;
        DimensionConfig.save();

        context.getSource().sendFeedback(() -> 
            Text.literal("§aEnabled dimension: " + dimensionStr), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int disableDimension(CommandContext<ServerCommandSource> context) {
        String dimensionStr = StringArgumentType.getString(context, "dimension");
        Identifier dimensionId = Identifier.of(dimensionStr);
        
        DimensionConfig.DimensionRestrictions restrictions = 
            DimensionConfig.getInstance().getRestrictions(dimensionId);
        restrictions.enabled = false;
        DimensionConfig.save();

        context.getSource().sendFeedback(() -> 
            Text.literal("§cDisabled dimension: " + dimensionStr), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int setPermission(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
            String level = StringArgumentType.getString(context, "level");
            
            PermissionManager.setPermission(target.getUuid(), level);
            
            context.getSource().sendFeedback(() -> 
                Text.literal("§aSet permission level for " + target.getName().getString() + " to: " + level), true);
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("§cFailed to set permission!"));
            return 0;
        }
    }

    private static int getPermission(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
            String level = PermissionManager.getPermission(target.getUuid());
            
            context.getSource().sendFeedback(() -> 
                Text.literal("§ePermission level for " + target.getName().getString() + ": " + level), false);
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("§cFailed to get permission!"));
            return 0;
        }
    }

    private static int removePermission(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
            PermissionManager.removePermission(target.getUuid());
            
            context.getSource().sendFeedback(() -> 
                Text.literal("§aRemoved permission level for " + target.getName().getString()), true);
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("§cFailed to remove permission!"));
            return 0;
        }
    }
}