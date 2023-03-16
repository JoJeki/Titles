package me.titles;

import me.titles.category.CategoryController;
import me.titles.commands.TitleCommand;
import me.titles.essentials.Debug;
import me.titles.listeners.JoinListener;
import me.titles.listeners.QuitListener;
import me.titles.owner.OwnerController;
import me.titles.title.TitleController;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
//import pl.pijok.titles.category.CategoryController;
//import pl.pijok.titles.commands.TitleCommand;
//import pl.pijok.titles.essentials.Debug;
//import pl.pijok.titles.listeners.JoinListener;
//import pl.pijok.titles.listeners.QuitListener;
//import pl.pijok.titles.owner.OwnerController;
//import pl.pijok.titles.title.TitleController;


public class Titles extends JavaPlugin {

    private static Titles instance;
    private static TitleController titleController;
    private static OwnerController ownerController;
    private static CategoryController categoryController;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        instance = this;

        titleController = new TitleController();
        ownerController = new OwnerController();
        categoryController = new CategoryController();

        if (!setupEconomy() ) {
            Debug.log(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);

        getCommand("sbtitle").setExecutor(new TitleCommand());

        loadStuff();

    }

    @Override
    public void onDisable() {

    }

    public void loadStuff() {

        Debug.log("&aLoading Titles v1.0 by Pijok_");

        Debug.log("&7Loading categories gui...");
        categoryController.load();

        Debug.log("&7Loading titles...");
        titleController.load();

        Debug.log("&7Loading gui...");
        titleController.loadGui();

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

    public static CategoryController getCategoryController() {
        return categoryController;
    }

}
