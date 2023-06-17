package me.titles.essentials;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.titles.Titles.prefix;

public class ChatUtils {

    public static void prefix(Player player, String message){
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public static String color(String msg) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher match = pattern.matcher(msg);
        while (match.find()) {
            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, net.md_5.bungee.api.ChatColor.of(Color.decode(color)) + "");
            match = pattern.matcher(msg);
        }
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', msg.replace("{", "").replace("}", ""));
    }
}
