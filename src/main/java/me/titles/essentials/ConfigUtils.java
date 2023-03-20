package me.titles.essentials;

//import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.titles.Titles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
//import pl.pijok.titles.Titles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    

    /**
     * Gets itemstack from config
     * @param configuration
     * @param path
     * @return
     */
    public static ItemStack getItemstack(YamlConfiguration configuration, String path){

        Material material = Material.valueOf(configuration.getString(path + ".material"));

        List<String> lore = new ArrayList<>();

        if(configuration.contains(path + ".lore")){
            for(String a : configuration.getStringList(path + ".lore")){
                lore.add(ChatUtils.fixColor(a));
            }
        }

        int amount = 1;

        if(configuration.contains(path + ".amount")){
            amount = configuration.getInt(path + ".amount");
        }

        String itemName = material.name();

        if(configuration.contains(path + ".name")){
            itemName = ChatUtils.fixColor(configuration.getString(path + ".name"));
        }

        ItemCreator creator = new ItemCreator(material, amount).setName(itemName).setLore(lore);

        if(configuration.contains(path + ".enchants")){
            for(String enchant : configuration.getConfigurationSection(path + ".enchants").getKeys(false)){
                //itemStack.addUnsafeEnchantment(Enchantment.getByName(enchant), configuration.getInt(path + ".enchants." + enchant));
                creator.addUnsafeEnchantment(Enchantment.getByName(enchant), configuration.getInt(path + ".enchants." + enchant));
            }
        }

        return creator.toItemStack();
    }

}
