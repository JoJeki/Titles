package me.titles.title;

//import me.arcaniax.hdb.api.HeadDatabaseAPI;
//import me.clip.placeholderapi.PlaceholderAPI;
//import me.mattstudios.mfgui.gui.components.ItemBuilder;
//import me.mattstudios.mfgui.gui.guis.Gui;
//import me.mattstudios.mfgui.gui.guis.GuiItem;
//import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.clip.placeholderapi.PlaceholderAPI;
import me.titles.Titles;
import me.titles.essentials.ChatUtils;
import me.titles.essentials.ConfigUtils;
import me.titles.essentials.Debug;
import me.titles.owner.Owner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.inventory.ItemStack;
//import pl.pijok.titles.Titles;
//import pl.pijok.titles.essentials.ChatUtils;
//import pl.pijok.titles.essentials.ConfigUtils;
//import pl.pijok.titles.essentials.Debug;
//import pl.pijok.titles.essentials.ItemCreator;
//import pl.pijok.titles.owner.Owner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class TitleController {

    private LinkedHashMap<String, Title> availableTitles;
    private Gui mainGui;

    public void load(){
        availableTitles = new LinkedHashMap<>();

        YamlConfiguration configuration = ConfigUtils.load("titles.yml", Titles.getInstance());

        for(String titleName : configuration.getConfigurationSection("titles").getKeys(false)){

            String category = "other";

            if(configuration.contains("titles." + titleName + ".category")){
                category = configuration.getString("titles." + titleName + ".category");
            }

            Title title = new Title(
                    titleName,
                    category,
                    configuration.getString("titles." + titleName + ".prefix"),
                    configuration.getDouble("titles." + titleName + ".price")
            );
            availableTitles.put(titleName, title);
        }
    }

    public void loadGui(){

        mainGui = new Gui(3, "Tytuly");

        mainGui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        mainGui.setItem(11, ItemBuilder.from(Material.PAPER).setName(ChatUtils.fixColor("&a&lTwoje tytuly")).asGuiItem(event -> {

            openTitleSelect((Player) event.getWhoClicked());

        }));

        mainGui.setItem(15, ItemBuilder.from(Material.BOOK).setName(ChatUtils.fixColor("&e&lKup nowe tytuly")).asGuiItem(event -> {

            //openShop((Player) event.getWhoClicked());
            Titles.getCategoryController().openCategorySelect((Player) event.getWhoClicked());

        }));

        GuiItem fillItem = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName("").asGuiItem();

        mainGui.getFiller().fill(fillItem);

    }

    public void addPlayerTitle(String nickname, String titleName){
        Title title = availableTitles.get(titleName);
        Owner owner = Titles.getOwnerController().getOwner(nickname);
        if(!owner.getUnlockedTitles().contains(title)){
            owner.getUnlockedTitles().add(title);
        }
        else{
            Debug.log("&cPlayer already has title " + titleName + "!");
        }
    }

    public void removePlayerTitle(String nickname, String titleName){
        Title title = availableTitles.get(titleName);
        Owner owner = Titles.getOwnerController().getOwner(nickname);
        if(owner.getUnlockedTitles().contains(title)){
            owner.getUnlockedTitles().remove(title);
        }
        else{
            Debug.log("&cPlayer doesn't have title " + titleName + "!");
        }
    }

    public boolean canPlayerBuyTitle(Player player, String titleName){
        Title title = availableTitles.get(titleName);
        return Titles.getEcon().getBalance(player) >= title.getPrice();
    }

    public void setPlayerPrefix(String nickname, String titleName){
        Title title = availableTitles.get(titleName);

        Owner owner = Titles.getOwnerController().getOwner(nickname);

        String command = "";
        String prefixCommand;
        if(!owner.getCurrentTitle().equalsIgnoreCase("none")){
            command = "lp user " + nickname + " meta removeprefix 10 * prison";

            Debug.log("[Unset command] " + command);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        prefixCommand = "\"prefix.10." + title.getPrefix() + "&r\"";

        owner.setCurrentTitle(titleName);

        command = "lp user " + nickname + " permission set " + prefixCommand + " server=prison";
        Debug.log("[Set command] " + command);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public void checkTitle(Player player){

        Debug.log("&aSprawdzam tytul gracza " + player.getName());

        Owner owner = Titles.getOwnerController().getOwner(player.getName());
        Title title = availableTitles.get(owner.getCurrentTitle());

        if(!owner.getCurrentTitle().equalsIgnoreCase("none")){

            String prefix = PlaceholderAPI.setBracketPlaceholders(player, "{PREFIX}");
            String colorFromPrefix = prefix.substring(prefix.lastIndexOf(" ") + 1 );

            String suffix = PlaceholderAPI.setBracketPlaceholders(player, "{SUFFIX}");
            String colorFromSuffix = suffix.substring(suffix.lastIndexOf(" ") + 1 );

            if(!colorFromPrefix.equalsIgnoreCase(colorFromSuffix) ){
                Debug.log("PrefixColor: " + colorFromPrefix + "PrefixTest");
                Debug.log("SuffixColor: " + colorFromSuffix + "SuffixTest");

                Debug.log("&aSuffix nie jest prawidlowy... Poprawiam");

                String clearSuffixCommand = "lp user " + player.getName() + " meta removeprefix 10 * prison";

                Debug.log("[Unset command] " + clearSuffixCommand);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), clearSuffixCommand);

                String prefixCommand = "\"prefix.10." + availableTitles.get(title.getName()).getPrefix() + colorFromPrefix + "&r\"";
                String command = "lp user " + player.getName() + " permission set " + prefixCommand + " server=prison";
                Debug.log("[Set command] " + command);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
            else{
                Debug.log("&aSuffix jest prawidlowy");
            }
        }
    }

    public Title getTitle(String titleName){
        return availableTitles.get(titleName);
    }

    public void openMainGui(Player player){
        mainGui.open(player);
    }

    public void openShop(Player player, String category){
        Owner owner = Titles.getOwnerController().getOwner(player.getName());

        ArrayList<Title> notUnlockedTitles = new ArrayList<>();

        for(String titleName : availableTitles.keySet()){

            if(titleName.equalsIgnoreCase("default")){
                continue;
            }

            Title title = availableTitles.get(titleName);

            if(!title.getCategory().equalsIgnoreCase(category)){
                continue;
            }

            if(!owner.getUnlockedTitles().contains(title)){
                notUnlockedTitles.add(title);
            }
        }

        PaginatedGui paginatedGui = new PaginatedGui(6, "Kup nowe tytuly!");

        paginatedGui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        int[] lockedSlots = new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44, 45,46,47,48,49,50,51,52,53};

        GuiItem fillItem = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName("").asGuiItem();

        for(int a : lockedSlots){
            paginatedGui.setItem(a, fillItem);
        }

        paginatedGui.setItem(50, ItemBuilder.from(Material.LIME_STAINED_GLASS_PANE).setName(ChatUtils.fixColor("&a&lNastepna strona")).asGuiItem(event -> {

            String name = ChatUtils.fixColor("&5&lStrona " + (paginatedGui.getNextPageNum()));
            String lore = ChatUtils.fixColor("&7Kliknij aby wrocic do wyboru kategori");
            paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name).setLore("", lore).asGuiItem(event1 -> {
                Titles.getCategoryController().openCategorySelect((Player) event1.getWhoClicked());
            }));

            paginatedGui.update();
            paginatedGui.next();
        }));


        String lore1 = ChatUtils.fixColor("&7Kliknij aby wrocic do wyboru kategori");
        String name1 = ChatUtils.fixColor("&5&lStrona " + 1);
        paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name1).setLore("", lore1).asGuiItem(event1 -> {
            Titles.getCategoryController().openCategorySelect((Player) event1.getWhoClicked());
        }));

        paginatedGui.setItem(48, ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).setName(ChatUtils.fixColor("&c&lPoprzednia strona")).asGuiItem(event -> {

            String name = ChatUtils.fixColor("&5&lStrona " + (paginatedGui.getPrevPageNum()));
            String lore = ChatUtils.fixColor("&7Kliknij aby wrocic do wyboru kategori");
            paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name).setLore("", lore).asGuiItem(event1 -> {
                Titles.getCategoryController().openCategorySelect((Player) event1.getWhoClicked());
            }));
            paginatedGui.update();
            paginatedGui.previous();
        }));

        for(Title title : notUnlockedTitles){
            paginatedGui.addItem(ItemBuilder.from(Material.BOOK).setName(ChatUtils.fixColor(title.getPrefix())).setLore("", ChatUtils.fixColor("&7Cena: &e" + title.getPrice())).asGuiItem(event -> {

                Player target = (Player) event.getWhoClicked();

                if(!canPlayerBuyTitle(target, title.getName())){
                    ChatUtils.sendMessage(target, "&cNie stac cie na ten tytul!");
                    return;
                }

                addPlayerTitle(target.getName(), title.getName());
                ChatUtils.sendMessage(target, "&aOdblokowano tytul " + title.getPrefix());
                Titles.getEcon().withdrawPlayer(target, title.getPrice());
                paginatedGui.close(target);
            }));
        }

        paginatedGui.open(player);
    }

    private void openTitleSelect(Player player){

        PaginatedGui paginatedGui = new PaginatedGui(6, "Wybierz swoj tytul");

        paginatedGui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        int[] lockedSlots = new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,49,50,51,52,53};

        GuiItem fillItem = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName("").asGuiItem();

        for(int a : lockedSlots){
            paginatedGui.setItem(a, fillItem);
        }

        //HeadDatabaseAPI api = new HeadDatabaseAPI();
        paginatedGui.setItem(50, ItemBuilder.from(Material.LIME_STAINED_GLASS_PANE).setName(ChatUtils.fixColor("&a&lNastepna strona")).asGuiItem(event -> {

            String name = ChatUtils.fixColor("&5&lStrona " + (paginatedGui.getNextPageNum()));
            String lore = ChatUtils.fixColor("&7Kliknij aby wrocic do menu");
            paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name).setLore("", lore).asGuiItem(event1 -> {
                openMainGui((Player) event1.getWhoClicked());
            }));

            paginatedGui.update();
            paginatedGui.next();
        }));

        String lore1 = ChatUtils.fixColor("&7Kliknij aby wrocic do menu");
        String name1 = ChatUtils.fixColor("&5&lStrona " + 1);
        paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name1).setLore("", lore1).asGuiItem(event1 -> {
            openMainGui((Player) event1.getWhoClicked());
        }));

        paginatedGui.setItem(48, ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).setName(ChatUtils.fixColor("&c&lPoprzednia strona")).asGuiItem(event -> {

            String name = ChatUtils.fixColor("&5&lStrona " + (paginatedGui.getPrevPageNum()));
            String lore = ChatUtils.fixColor("&7Kliknij aby wrocic do menu");
            paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name).setLore("", lore).asGuiItem(event1 -> {
                openMainGui((Player) event1.getWhoClicked());
            }));
            paginatedGui.update();
            paginatedGui.previous();
        }));

        Owner owner = Titles.getOwnerController().getOwner(player.getName());

        paginatedGui.setItem(43, ItemBuilder.from(Material.BARRIER).setName(ChatUtils.fixColor("&c&lZresetuj")).asGuiItem(event -> {
            setPlayerPrefix(event.getWhoClicked().getName(), "default");
            ChatUtils.sendMessage(event.getWhoClicked(), "&aZmieniono prefix!");
            paginatedGui.close(event.getWhoClicked());
        }));

        for(Title title : owner.getUnlockedTitles()){
            paginatedGui.addItem(ItemBuilder.from(Material.PAPER).setName(ChatUtils.fixColor(title.getPrefix())).setLore("", ChatUtils.fixColor("&eWcisnij aby ustawic ten prefix")).asGuiItem(event -> {
                setPlayerPrefix(event.getWhoClicked().getName(), title.getName());
                ChatUtils.sendMessage(event.getWhoClicked(), "&aZmieniono prefix!");
                paginatedGui.close(event.getWhoClicked());
            }));
        }

        paginatedGui.open(player);

    }
}
