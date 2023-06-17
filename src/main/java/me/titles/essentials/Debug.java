package me.titles.essentials;

import org.bukkit.Bukkit;

public class Debug {
    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage("[TITLES] " + message.replace("&", "§"));
    }
}
