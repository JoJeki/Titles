package me.titles.commands;

import me.titles.Titles;
import me.titles.essentials.ConfigUtils;
import me.titles.essentials.Debug;
import me.titles.owner.Owner;
import me.titles.title.Title;
import me.titles.title.TitleController;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TitleTabCompleter implements TabCompleter {
    @Nullable

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> titleTab = new ArrayList<>();
        if (sender.hasPermission("titlesreborn.admin")) {
            if (args.length == 1) {
                titleTab.add("give");
                titleTab.add("take");
                titleTab.add("show");
                titleTab.add("reload");
                return titleTab;
            }
            if (args.length == 2) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    titleTab.add(player.getName());
                }
                return titleTab;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("take")) {

                Owner owner = Titles.getOwnerController().getOwner(args[1]);
                for (Title title : owner.getUnlockedTitles()) {
                    titleTab.add(title.getName());
                }
                return titleTab;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
                YamlConfiguration titles = ConfigUtils.load("titles.yml", Titles.getInstance());
                titleTab.addAll(titles.getConfigurationSection("titles").getKeys(false));
                Owner owner = Titles.getOwnerController().getOwner(args[1]);

                for (String key : titleTab) {
                    Title title = Titles.getTitleController().getTitle(key);
                    if(!owner.getUnlockedTitles().contains(title)){
                        owner.getUnlockedTitles().add(title);
                        titleTab.remove(title);
                    }
                }
                return titleTab;
            }

        }
        return null;
    }
}

//Owner owner = Titles.getOwnerController().getOwner(player.getName());


// ./tytuly reload
// ./tytuly give <gracz> <title>
// ./tytuly take <gracz> <title>
// ./tytuly show <gracz>
