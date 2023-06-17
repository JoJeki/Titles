package me.titles;

import me.titles.commands.TitleCommand;
import me.titles.commands.TitleTabCompleter;
import me.titles.essentials.Debug;
import me.titles.listeners.JoinListener;
import me.titles.listeners.QuitListener;
import me.titles.owner.OwnerController;
import me.titles.title.TitleController;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Titles extends JavaPlugin {

    private static Titles instance;
    private static TitleController titleController;
    private static OwnerController ownerController;
    private static Economy econ = null;
    public static final String prefix = "&a&lSKYBLOCK &7» &r";
    public static final String server = "skyblock";

    @Override
    public void onEnable() {
        instance = this;

        titleController = new TitleController();
        ownerController = new OwnerController();

        if (!setupEconomy() ) {
            Debug.log(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);

        getCommand("tytuly").setExecutor(new TitleCommand());
        getCommand("tytuly").setTabCompleter(new TitleTabCompleter());

        loadStuff();

    }

    @Override
    public void onDisable() {

    }

    public void loadStuff() {

        Debug.log("&aLoading Titles v1.0 by Pijok_");
        Debug.log("&aRedesigned by y0yek");

        Debug.log("&7Loading titles...");
        titleController.load();

        Debug.log("&aEverything loaded! Starting!");

    }

    public static Titles getInstance() {
        return instance;
    }

    public static TitleController getTitleController(){
        return titleController;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEcon(){
        return econ;
    }

    public static OwnerController getOwnerController() {
        return ownerController;
    }

}
