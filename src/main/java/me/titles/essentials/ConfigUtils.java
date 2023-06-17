package me.titles.essentials;

import me.titles.Titles;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigUtils {
    
    public static YamlConfiguration load(String configName, Plugin plugin){
        YamlConfiguration config;
        File file = new File(plugin.getDataFolder() + File.separator + configName);
        if (!file.exists())
            plugin.saveResource(configName, false);
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        return config;
    }
    
    public static void save(YamlConfiguration c, String file) {
        try {
            c.save(new File(Titles.getInstance().getDataFolder(), file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
