package me.titles.commands;

import me.titles.Titles;
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

import static me.titles.essentials.ChatUtils.*;

public class TitleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof ConsoleCommandSender){
            Debug.log("&cCommand only executable by player!");
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length == 0) {
                Titles.getTitleController().openShopMenu(player);
            }

            if (args.length == 1) {
                if (sender.hasPermission("titlesreborn.admin")) {
                    if (args[0].equalsIgnoreCase("reload")) {

                        player.sendMessage(color("&aReloaded!"));
                        Titles.getInstance().loadStuff();
                        return true;

                    }
                    if (args[0].equalsIgnoreCase("help")) {

                        player.sendMessage(color("&e▪ &6/tytuly &7- &fMenu z tytułami."));
                        player.sendMessage(color("&e▪ &6/tytuly reload &7- &fReload."));
                        player.sendMessage(color("&e▪ &6/tytuly help &7- &fMenu pomocy."));
                        player.sendMessage(color("&e▪ &6/tytuly give <nick> <title> &7- &fNadawanie"));
                        player.sendMessage(color("&e▪ &6/tytuly take <nick> <title> &7- &fOdbieranie"));
                        player.sendMessage(color("&e▪ &6/tytuly show <nick> &7- &fTytuły gracza."));
                        return true;

                    }
                } else {
                    prefix(player, "&cBrak uprawnień!");
                    return true;
                }
            }
            if(args.length == 2){
                if (sender.hasPermission("titlesreborn.admin")) {
                    if (args[0].equalsIgnoreCase("show")) {

                        Player target = Bukkit.getPlayer(args[1]);

                        if (target == null || !target.isOnline()) {
                            prefix(player, "&cGracz jest nieaktywny!");
                            return true;
                        }

                        Owner owner = Titles.getOwnerController().getOwner(target.getName());
                        StringBuilder stringBuilder = new StringBuilder(" &e&lTytuły gracza " + target.getName() + "&e&l:\n&7");

                        if (owner.getUnlockedTitles() == null || owner.getUnlockedTitles().size() == 0) {
                            prefix(player, "&cGracz " + target.getName() + " &cnie posiada żadnego tytułu!");
                            return true;
                        } else {
                            for (Title title : owner.getUnlockedTitles()) {
                                stringBuilder.append("&7▪ " + title.getPrefix() + "&7(" + title.getName() + "&7)\n");
                            }
                        }
                        player.sendMessage(color(stringBuilder.toString()));
                        return true;
                    }
                }
            }
            if(args.length == 3){
                if (sender.hasPermission("titlesreborn.admin")) {
                    if (args[0].equalsIgnoreCase("give")) {

                        Player target = Bukkit.getPlayer(args[1]);

                        if (target == null || !target.isOnline()) {
                            prefix(player, "&cGracz jest nieaktywny!");
                            return true;
                        }

                        Titles.getTitleController().addPlayerTitle(target.getName(), args[2]);
                        prefix(player, "&7Pomyślnie przyznano tytuł &e&l" + args[2] + " &7dla gracza &e" + target.getName() + "&7!");
                        prefix(target, "&7Otrzymałeś/aś tytuł &e&l" + args[2] + "&7!");
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("take")) {

                        Player target = Bukkit.getPlayer(args[1]);

                        if (target == null || !target.isOnline()) {
                            prefix(player, "&cGracz jest nieaktywny!");
                            return true;
                        }

                        Titles.getTitleController().removePlayerTitle(target.getName(), args[2]);
                        prefix(player, "&7Pomyślnie odebrano tytuł &e" + args[2] + " &7graczu &e" + target.getName() + "&7!");
                        prefix(target, "&7Odebrano ci tytuł &e&l" + args[2] + "&7!");
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("set")) {

                        Player target = Bukkit.getPlayer(args[1]);

                        if (target == null || !target.isOnline()) {
                            prefix(player, "&cGracz jest nieaktywny!");
                            return true;
                        }

                        Titles.getTitleController().removePlayerTitle(target.getName(), args[2]);
                        prefix(player, "&7Pomyślnie odebrano tytuł &e" + args[2] + " &7graczu &e" + target.getName() + "&7!");
                        prefix(target, "&7Odebrano ci tytuł &e&l" + args[2] + "&7!");
                        return true;
                    }
                }
            }
        }
        return true;
    }
}