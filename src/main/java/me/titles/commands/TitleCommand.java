package me.titles.commands;

import me.titles.Titles;
import me.titles.essentials.ChatUtils;
import me.titles.essentials.Debug;
import me.titles.owner.Owner;
import me.titles.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TitleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof ConsoleCommandSender){
            Debug.log("&aSender is console!");
        }

        if(args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Titles.getTitleController().openMainGui(player);
                return true;
            } else {
                ChatUtils.sendMessage(sender, "&cTa komenda jest tylko dla graczy!");
                return true;
            }
        }
        if (args.length == 1) {
            if (sender.hasPermission("titles.admin")) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        ChatUtils.sendMessage(player, "&cBrak uprawnień!");
                        return true;
                    }
                    ChatUtils.sendMessage(sender, "&aReloaded!");
                    Titles.getInstance().loadStuff();
                    return true;
                }
                if (args[0].equalsIgnoreCase("help")) {
                    if (sender instanceof Player) {
                        ChatUtils.sendMessage(sender, "&e/tytuly &7- Menu z tytułami.");
                        ChatUtils.sendMessage(sender, "&e/tytuly reload &7- Ponowne wczytanie pluginu.");
                        ChatUtils.sendMessage(sender, "&e/tytuly help &7- Menu pomocy.");
                        ChatUtils.sendMessage(sender, "&e/tytuly give <nick> <title> &7- Nadawanie");
                        ChatUtils.sendMessage(sender, "&e/tytuly take <nick> <title> &7- Odbieranie");
                        ChatUtils.sendMessage(sender, "&e/tytuly show <nick> &7- Lista tytułów gracza.");
                    }
                }
            }
        }

        if(args.length == 2){
            if (sender.hasPermission("titles.admin")) {
                if (args[0].equalsIgnoreCase("show")) {

                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (!player.hasPermission("titles.admin")) {
                            ChatUtils.sendMessage(player, "&cBrak uprawnień!");
                            return true;
                        }
                    }

                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null || !target.isOnline()) {
                        Debug.log("&cPlayer is offline!");
                        return true;
                    }

                    StringBuilder stringBuilder = new StringBuilder("&eTytuły gracza: &7");

                    Owner owner = Titles.getOwnerController().getOwner(target.getName());
                    if (owner.getUnlockedTitles() == null || owner.getUnlockedTitles().size() == 0) {
                        stringBuilder.append("&cBrak");
                    } else {
                        for (Title title : owner.getUnlockedTitles()) {
                            stringBuilder.append(title.getName()).append(", ");
                        }
                    }
                    ChatUtils.sendMessage(sender, stringBuilder.toString());
                    return true;
                }
            }
        }

        if(args.length == 3){
            if (sender.hasPermission("titles.admin")) {
                if (args[0].equalsIgnoreCase("give")) {

                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (!player.hasPermission("titles.admin")) {
                            ChatUtils.sendMessage(player, "&cBrak uprawnień!");
                            return true;
                        }
                    }

                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null || !target.isOnline()) {
                        ChatUtils.sendMessage(sender, "&cPlayer is offline!");
                        return true;
                    }

                    Titles.getTitleController().addPlayerTitle(target.getName(), args[2]);
                    ChatUtils.sendMessage(sender, "&aWykonano akcję!");
                    return true;
                }

                if (args[0].equalsIgnoreCase("take")) {

                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (!player.hasPermission("titles.admin")) {
                            ChatUtils.sendMessage(player, "&cBrak uprawnień!");
                            return true;
                        }
                    }

                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null || !target.isOnline()) {
                        ChatUtils.sendMessage(sender, "&cPlayer is offline!");
                        return true;
                    }

                    Titles.getTitleController().removePlayerTitle(target.getName(), args[2]);
                    ChatUtils.sendMessage(sender, "&aWykonano akcję!");
                    return true;
                }
            }
        }
        return true;
    }
}
