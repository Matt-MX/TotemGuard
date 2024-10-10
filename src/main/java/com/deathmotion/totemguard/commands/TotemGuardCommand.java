/*
 * This file is part of TotemGuard - https://github.com/Bram1903/TotemGuard
 * Copyright (C) 2024 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.totemguard.commands;

import com.deathmotion.totemguard.TotemGuard;
import com.deathmotion.totemguard.commands.totemguard.*;
import com.deathmotion.totemguard.data.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TotemGuardCommand implements CommandExecutor, TabExecutor {
    private final Component versionComponent;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public TotemGuardCommand(TotemGuard plugin) {
        subCommands.put("info", new InfoCommand(plugin));
        subCommands.put("alerts", new AlertsCommand(plugin));
        subCommands.put("reload", new ReloadCommand(plugin));
        subCommands.put("profile", new ProfileCommand(plugin));
        subCommands.put("stats", new StatsCommand(plugin));
        subCommands.put("clearlogs", new ClearLogsCommand(plugin));
        subCommands.put("track", new TrackCommand(plugin));
        subCommands.put("untrack", new UntrackCommand(plugin));
        subCommands.put("database", new DatabaseCommand(plugin));

        versionComponent = Component.text()
                .append(LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getConfigManager().getSettings().getPrefix()).decorate(TextDecoration.BOLD))
                .append(Component.text("Running ", NamedTextColor.GRAY).decorate(TextDecoration.BOLD))
                .append(Component.text("TotemGuard", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text(" v" + plugin.getVersion().toString(), NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text(" by ", NamedTextColor.GRAY).decorate(TextDecoration.BOLD))
                .append(Component.text("Bram", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text(" and ", NamedTextColor.GRAY).decorate(TextDecoration.BOLD))
                .append(Component.text("OutDev", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .hoverEvent(HoverEvent.showText(Component.text("Open Github Page!", NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD)
                        .decorate(TextDecoration.UNDERLINED)))
                .clickEvent(ClickEvent.openUrl(Constants.GITHUB_URL))
                .build();

        plugin.getCommand("totemguard").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!hasAnyPermission(sender)) {
            sender.sendMessage(versionComponent);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(getAvailableCommandsComponent(sender));
            return false;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand != null && hasPermissionForSubCommand(sender, subCommandName)) {
            return subCommand.execute(sender, args);
        } else {
            sender.sendMessage(getAvailableCommandsComponent(sender));
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .filter(name -> hasPermissionForSubCommand(sender, name))
                    .collect(Collectors.toList());
        } else if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null && hasPermissionForSubCommand(sender, args[0].toLowerCase())) {
                return subCommand.onTabComplete(sender, args);
            }
        }
        return List.of();
    }

    private boolean hasAnyPermission(CommandSender sender) {
        return subCommands.keySet().stream().anyMatch(command -> !command.equals("info") && hasPermissionForSubCommand(sender, command));
    }

    private boolean hasPermissionForSubCommand(CommandSender sender, String subCommand) {
        return switch (subCommand) {
            case "info" -> true;
            case "alerts" -> sender.hasPermission("TotemGuard.Alerts");
            case "reload" -> sender.hasPermission("TotemGuard.Reload");
            case "profile" -> sender.hasPermission("TotemGuard.Profile");
            case "stats" -> sender.hasPermission("TotemGuard.Stats");
            case "clearlogs" -> sender.hasPermission("TotemGuard.ClearLogs");
            case "track", "untrack" -> sender.hasPermission("TotemGuard.Track");
            case "database" -> hasAnyDatabasePermissions(sender);
            default -> false;
        };
    }

    private Component getAvailableCommandsComponent(CommandSender sender) {
        // Start building the help message
        TextComponent.Builder componentBuilder = Component.text()
                .append(Component.text("TotemGuard Commands", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("Below are the available subcommands:", NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.newline());

        // Command descriptions
        Map<String, String> commandDescriptions = Map.of(
                "info", "Show plugin information.",
                "alerts", "Toggle alerts on/off.",
                "reload", "Reload the plugin configuration.",
                "profile", "View player profiles.",
                "stats", "View plugin statistics.",
                "clearlogs", "Clear player logs.",
                "track", "Tracks a player.",
                "database", "Database management commands."
        );

        // Add each command to the message if the sender has permission
        for (String command : subCommands.keySet()) {
            if (hasPermissionForSubCommand(sender, command)) {
                componentBuilder.append(Component.text("- ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("/totemguard " + command, NamedTextColor.GOLD, TextDecoration.BOLD))
                        .append(Component.text(" - ", NamedTextColor.GRAY))
                        .append(Component.text(commandDescriptions.get(command), NamedTextColor.GRAY))
                        .append(Component.newline());
            }
        }

        // If no commands are available, provide a different message
        if (componentBuilder.children().isEmpty()) {
            return Component.text("You do not have permission to use any commands.", NamedTextColor.RED);
        }

        return componentBuilder.build();
    }

    private boolean hasAnyDatabasePermissions(CommandSender sender) {
        if (sender.hasPermission("TotemGuard.Database")) {
            return true;
        } else if (sender.hasPermission("TotemGuard.Database.Trim")) {
            return true;
        } else return sender.hasPermission("TotemGuard.Database.Clear");
    }
}