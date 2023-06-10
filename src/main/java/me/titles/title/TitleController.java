package me.titles.title;

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
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
//import org.bukkit.event.EventHandler;
//import org.bukkit.inventory.ItemStack;
//import pl.pijok.titles.Titles;
//import pl.pijok.titles.essentials.ChatUtils;
//import pl.pijok.titles.essentials.ConfigUtils;
//import pl.pijok.titles.essentials.Debug;
//import pl.pijok.titles.essentials.ItemCreator;
//import pl.pijok.titles.owner.Owner;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        mainGui = new Gui(3, " ");

        mainGui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        mainGui.setItem(11, ItemBuilder.from(Material.PAPER).setName(ChatUtils.fixColor("&7• &ePosiadane tytuły &7•")).asGuiItem(event -> {

            openTitleSelect((Player) event.getWhoClicked());

        }));

        mainGui.setItem(15, ItemBuilder.from(Material.BOOK).setName(ChatUtils.fixColor("&7• &aZakup nowy tytuł &7•")).asGuiItem(event -> {

            Titles.getTitleController().openShop((Player) event.getWhoClicked(), "other");
            //Titles.getCategoryController().openCategorySelect((Player) event.getWhoClicked());

        }));

        GuiItem fillItem = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" ").asGuiItem();

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
            command = "lp user " + nickname + " meta removeprefix 10 * server=prison";

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

        Debug.log("&aSprawdzanie tytulu gracza " + player.getName());

        Owner owner = Titles.getOwnerController().getOwner(player.getName());
        Title title = availableTitles.get(owner.getCurrentTitle());

        if(!owner.getCurrentTitle().equalsIgnoreCase("none")){

            String prefix = PlaceholderAPI.setBracketPlaceholders(player, "");
            String colorFromPrefix = prefix.substring(prefix.lastIndexOf(" ") + 1 );

            String suffix = PlaceholderAPI.setBracketPlaceholders(player, "");
            String colorFromSuffix = suffix.substring(suffix.lastIndexOf(" ") + 1 );

            if(!colorFromPrefix.equalsIgnoreCase(colorFromSuffix) ){
                Debug.log("PrefixColor: " + colorFromPrefix + "PrefixTest");
                Debug.log("SuffixColor: " + colorFromSuffix + "SuffixTest");

                Debug.log("&aSuffix nie jest prawidlowy... Poprawiam");

                String clearSuffixCommand = "lp user " + player.getName() + " meta removeprefix 10 * server=prison";

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

        PaginatedGui paginatedGui = new PaginatedGui(6, " ");

        paginatedGui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        int[] lockedSlots = new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44, 45,46,47,48,49,50,51,52,53};

        GuiItem fillItem = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" ").asGuiItem();

        for(int a : lockedSlots){
            paginatedGui.setItem(a, fillItem);
        }

        paginatedGui.setItem(50, ItemBuilder.from(Material.LIME_STAINED_GLASS_PANE).setName(ChatUtils.fixColor("&a→")).asGuiItem(event -> {

            String name = ChatUtils.fixColor("&6Strona &e&l" + (paginatedGui.getNextPageNum()));
            String lore = ChatUtils.fixColor("&7Kliknij aby wrócić do menu.");
            paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name).setLore("", lore).asGuiItem(event1 -> {
                Titles.getTitleController().openMainGui((Player) event1.getWhoClicked());
                //Titles.getCategoryController().openCategorySelect((Player) event1.getWhoClicked());
            }));

            paginatedGui.update();
            paginatedGui.next();
        }));


        String lore1 = ChatUtils.fixColor("&7Kliknij aby wrócić do menu.");
        String name1 = ChatUtils.fixColor("&6Strona &e&l" + 1);
        paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name1).setLore("", lore1).asGuiItem(event1 -> {
            Titles.getTitleController().openMainGui((Player) event1.getWhoClicked());
            //Titles.getCategoryController().openCategorySelect((Player) event1.getWhoClicked());
        }));

        paginatedGui.setItem(48, ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).setName(ChatUtils.fixColor("&c←")).asGuiItem(event -> {

            String name = ChatUtils.fixColor("&6Strona &e&l" + (paginatedGui.getPrevPageNum()));
            String lore = ChatUtils.fixColor("&7Kliknij aby wrócić do menu.");
            paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name).setLore("", lore).asGuiItem(event1 -> {
                Titles.getTitleController().openMainGui((Player) event1.getWhoClicked());
                //Titles.getCategoryController().openCategorySelect((Player) event1.getWhoClicked());
            }));
            paginatedGui.update();
            paginatedGui.previous();
        }));

        DecimalFormat df = new DecimalFormat("#");

        for(Title title : notUnlockedTitles){
            paginatedGui.addItem(ItemBuilder.from(Material.BOOK).setName(translate(title.getPrefix())).setLore("", ChatUtils.fixColor("&7Cena: &e" + df.format(title.getPrice()) + "&e$")).asGuiItem(event -> {

                Player target = (Player) event.getWhoClicked();

                if(!canPlayerBuyTitle(target, title.getName())){
                    ChatUtils.sendMessage(target, "&cNie posiadasz wystarczającej ilości pieniędzy!");
                    return;
                }

                addPlayerTitle(target.getName(), title.getName());
                ChatUtils.sendMessage(target, "&aZakupiłeś/aś tytuł: " + translate(title.getPrefix()));
                ChatUtils.sendMessage(target, "&aTytuły znajdziesz na lewo w menu /tytuly!");
                Titles.getEcon().withdrawPlayer(target, title.getPrice());
                paginatedGui.close(target);
            }));
        }

        paginatedGui.open(player);
    }

    private void openTitleSelect(Player player){
        Owner owner = Titles.getOwnerController().getOwner(player.getName());

        if(owner.getUnlockedTitles().size() >= 1) {

            PaginatedGui paginatedGui = new PaginatedGui(6, "Wybierz tytuł");

            paginatedGui.setDefaultClickAction(event -> {
                event.setCancelled(true);
            });

            int[] lockedSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};

            GuiItem fillItem = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" ").asGuiItem();

            for (int a : lockedSlots) {
                paginatedGui.setItem(a, fillItem);
            }

            //HeadDatabaseAPI api = new HeadDatabaseAPI();
            paginatedGui.setItem(50, ItemBuilder.from(Material.LIME_STAINED_GLASS_PANE).setName(ChatUtils.fixColor("&a→")).asGuiItem(event -> {

                String name = ChatUtils.fixColor("&6Strona &e&l" + (paginatedGui.getNextPageNum()));
                String lore = ChatUtils.fixColor("&7Kliknij aby wrócić do menu.");
                paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name).setLore("", lore).asGuiItem(event1 -> {
                    openMainGui((Player) event1.getWhoClicked());
                }));

                paginatedGui.update();
                paginatedGui.next();
            }));

            String lore1 = ChatUtils.fixColor("&7Kliknij aby wrócić do menu.");
            String name1 = ChatUtils.fixColor("&6Strona &e&l" + 1);
            paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name1).setLore("", lore1).asGuiItem(event1 -> {
                openMainGui((Player) event1.getWhoClicked());
            }));

            paginatedGui.setItem(48, ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).setName(ChatUtils.fixColor("&c←")).asGuiItem(event -> {

                String name = ChatUtils.fixColor("&6Strona &e&l" + (paginatedGui.getPrevPageNum()));
                String lore = ChatUtils.fixColor("&7Kliknij aby wrócić do menu.");
                paginatedGui.setItem(49, ItemBuilder.from(Material.WRITABLE_BOOK).setName(name).setLore("", lore).asGuiItem(event1 -> {
                    openMainGui((Player) event1.getWhoClicked());
                }));
                paginatedGui.update();
                paginatedGui.previous();
            }));


            paginatedGui.setItem(43, ItemBuilder.from(Material.BARRIER).setName(ChatUtils.fixColor("&cDezaktywuj tytuł.")).asGuiItem(event -> {
                setPlayerPrefix(event.getWhoClicked().getName(), "default");
                ChatUtils.sendMessage(event.getWhoClicked(), "&cDezaktywowano tytuł!");
                paginatedGui.close(event.getWhoClicked());
            }));

            for (Title title : owner.getUnlockedTitles()) {
                paginatedGui.addItem(ItemBuilder.from(Material.PAPER).setName(translate(title.getPrefix())).setLore("", ChatUtils.fixColor("&eKliknij, aby wybrać ten tytuł!")).asGuiItem(event -> {
                    setPlayerPrefix(event.getWhoClicked().getName(), title.getName());
                    ChatUtils.sendMessage(event.getWhoClicked(), "&aUstawiono tytuł: " + translate(title.getPrefix()));
                    paginatedGui.close(event.getWhoClicked());
                }));
            }

            paginatedGui.open(player);
        } else {
            Titles.getTitleController().openMainGui(player);
            ChatUtils.sendMessage(player, "&cNie posiadasz żadnego tytułu!");
            mainGui.close(player);
        }

    }

    private String translate(String msg2) {
        String msg = msg2.replaceAll("\\&#","#");
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher match = pattern.matcher(msg);
        while (match.find()) {
            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, ChatColor.of(Color.decode(color)) + "");
            match = pattern.matcher(msg);
        }
        String msgFixedFirst = msg.replace("{", "");
        String msgFixedSecond = msgFixedFirst.replace("}", "");
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', msgFixedSecond);
    }
}
// &#rrggbb -> #rrggbb
// {#rrggbb} -> #rrggbb