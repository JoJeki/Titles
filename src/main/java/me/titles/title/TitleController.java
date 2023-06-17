package me.titles.title;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.titles.Titles;
import me.titles.essentials.ConfigUtils;
import me.titles.essentials.Debug;
import me.titles.owner.Owner;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.titles.Titles.server;
import static me.titles.essentials.ChatUtils.*;

public class TitleController {

    private LinkedHashMap<String, Title> availableTitles;
    private Gui mainGui;

    public void load(){
        availableTitles = new LinkedHashMap<>();

        YamlConfiguration configuration = ConfigUtils.load("titles.yml", Titles.getInstance());

        for(String titleName : configuration.getConfigurationSection("titles").getKeys(false)){

            Title title = new Title(
                    titleName,
                    configuration.getString("titles." + titleName + ".prefix"),
                    configuration.getDouble("titles." + titleName + ".price")
            );
            availableTitles.put(titleName, title);
        }
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
            command = "lp user " + nickname + " meta removeprefix 10 * server="+server;

            Debug.log(command);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        prefixCommand = "\"prefix.10." + title.getPrefix() + "&r\"";

        owner.setCurrentTitle(titleName);

        command = "lp user " + nickname + " permission set " + prefixCommand + " server="+server;
        Debug.log(command);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public Title getTitle(String titleName){
        return availableTitles.get(titleName);
    }


    public void openShopMenu(Player player) {
        Owner owner = Titles.getOwnerController().getOwner(player.getName());

        ArrayList<Title> notUnlockedTitles = new ArrayList<>();

        for (String titleName : availableTitles.keySet()) {

            if (titleName.equalsIgnoreCase("default")) {
                continue;
            }

            Title title = availableTitles.get(titleName);

            if (!owner.getUnlockedTitles().contains(title)) {
                notUnlockedTitles.add(title);
            }
        }
        if (notUnlockedTitles.size() != 0) {
            PaginatedGui shopMenu = new PaginatedGui(6, 21, " ", Collections.singleton(InteractionModifier.PREVENT_ITEM_TAKE));

            shopMenu.setDefaultClickAction(event -> {
                event.setCancelled(true);
            });

            int[] grayLocked = new int[]
                    {0, 1, 2, 3, 4, 5, 6, 7, 8,
                            9, 17,
                            18, 26,
                            27, 35,
                            36, 37, 38, 39, 40, 41, 42, 43, 44};

            int[] blackLocked = new int[]
                    {45, 46, 47, 48, 51, 52, 53};

            GuiItem grayFiller = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem();

            GuiItem blackFiller = ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem();

            for (int slot : grayLocked) {
                shopMenu.setItem(slot, grayFiller);
            }

            for (int slot : blackLocked) {
                shopMenu.setItem(slot, blackFiller);
            }

            shopMenu.setItem(48, ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).setName(color("&c←")).asGuiItem(event2 -> {
                shopMenu.previous();
                shopMenu.updateTitle(translate("&#29dcfb&lS&#2ad6fb&lK&#2cd0fb&lL&#2dcafb&lE&#2fc4fb&lP &#30befb&lZ &#31b8fb&lT&#33b1fb&lY&#34abfb&lT&#35a5fb&lU&#379ffb&lŁ&#3899fb&lA&#3a93fb&lM&#3b8dfb&lI &7") + shopMenu.getCurrentPageNum() + "/" + shopMenu.getPagesNum());
                shopMenu.update();
            }));

            String name = color("&e&lTWOJE TYTUŁY");
            String lore = color("&7ᴋʟɪᴋɴɪᴊ, ᴀʙʏ ᴏᴛᴡᴏʀᴢʏᴄ ᴅᴏsᴛᴇᴘɴᴇ ᴛʏᴛᴜʟʏ!");

            shopMenu.setItem(49, ItemBuilder.from(Material.BOOK).setName(name).setLore(" ", lore).asGuiItem(event1 -> {
                if(owner.getUnlockedTitles().size() >= 1) {
                    openChooseMenu(player);
                } else {
                    prefix(player, "&cNie posiadasz żadnego tytułu!");
                }
            }));

            shopMenu.setItem(50, ItemBuilder.from(Material.LIME_STAINED_GLASS_PANE).setName(color("&a→")).asGuiItem(event -> {
                shopMenu.next();
                shopMenu.updateTitle(translate("&#29dcfb&lS&#2ad6fb&lK&#2cd0fb&lL&#2dcafb&lE&#2fc4fb&lP &#30befb&lZ &#31b8fb&lT&#33b1fb&lY&#34abfb&lT&#35a5fb&lU&#379ffb&lŁ&#3899fb&lA&#3a93fb&lM&#3b8dfb&lI &7") + shopMenu.getCurrentPageNum() + "/" + shopMenu.getPagesNum());
                shopMenu.update();
            }));

            DecimalFormat df = new DecimalFormat("#");

            for (Title title : notUnlockedTitles) {
                shopMenu.addItem(ItemBuilder.from(Material.PAPER).setName(translate(title.getPrefix())).setLore(color("&7Koszt: &e" + df.format(title.getPrice()) + "&e$"), " ", color("&8&oKliknij aby zakupić!")).asGuiItem(event -> {

                    Player target = (Player) event.getWhoClicked();

                    if (!canPlayerBuyTitle(target, title.getName())) {
                        prefix(target, "&cBrak funduszy! ");
                        prefix(target, "&cKoszt tego tytułu wynosi &e" + df.format(title.getPrice()) + "&e$");
                        return;
                    }

                    addPlayerTitle(target.getName(), title.getName());
                    prefix(target, "&aZakupiłeś/aś tytuł: " + translate(title.getPrefix()));
                    prefix(target, "&aZnajdziesz go w książce w menu /tytuly!");
                    Titles.getEcon().withdrawPlayer(target, title.getPrice());
                    openChooseMenu(target);
                }));
            }
            shopMenu.open(player);
            shopMenu.updateTitle(translate("&#29dcfb&lS&#2ad6fb&lK&#2cd0fb&lL&#2dcafb&lE&#2fc4fb&lP &#30befb&lZ &#31b8fb&lT&#33b1fb&lY&#34abfb&lT&#35a5fb&lU&#379ffb&lŁ&#3899fb&lA&#3a93fb&lM&#3b8dfb&lI &7") + shopMenu.getCurrentPageNum() + "/" + shopMenu.getPagesNum());
        } else {
            openChooseMenu(player);
        }
    }


    public void openChooseMenu(Player player) {

        Owner owner = Titles.getOwnerController().getOwner(player.getName());

        ArrayList<Title> notUnlockedTitles = new ArrayList<>();

        for (String titleName : availableTitles.keySet()) {

            if (titleName.equalsIgnoreCase("default")) {
                continue;
            }

            Title title = availableTitles.get(titleName);

            if (!owner.getUnlockedTitles().contains(title)) {
                notUnlockedTitles.add(title);
            }
        }

        PaginatedGui chooseMenu = new PaginatedGui(6, 21, " ", Collections.singleton(InteractionModifier.PREVENT_ITEM_TAKE));

        chooseMenu.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        int[] grayLocked = new int[]
                {0, 1, 2, 3, 4, 5, 6, 7, 8,
                        9,                      17,
                        18,                     26,
                        27,                     35,
                        36,37,38,39,40,41,42,43,44};

        int[] blackLocked = new int[]
                {45, 46, 47, 48, 51, 53};

        GuiItem grayFiller = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem();

        GuiItem blackFiller = ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem();

        for (int slot : grayLocked) {
            chooseMenu.setItem(slot, grayFiller);
        }

        for (int slot : blackLocked) {
            chooseMenu.setItem(slot, blackFiller);
        }

        chooseMenu.setItem(48, ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).name(Component.text(color("&c←"))).asGuiItem(event2 -> {
            chooseMenu.previous();
            chooseMenu.updateTitle(translate("&#45fb7d&lM&#48fb85&lE&#4cfb8e&lN&#4ffb96&lU &#52fb9f&lW&#56fba7&lY&#59fbb0&lB&#5cfbb8&lO&#60fbc1&lR&#63fbc9&lU &7") + chooseMenu.getCurrentPageNum() + "/" + chooseMenu.getPagesNum());
            chooseMenu.update();
        }));

        String name1 = color("&e&lSKLEP Z TYTUŁAMI");
        String lore1 = color("&7ᴋʟɪᴋɴɪᴊ, ᴀʙʏ ᴏᴛᴡᴏʀᴢʏᴄ sᴋʟᴇᴘ ᴢ ᴛʏᴛᴜʟᴀᴍɪ!");

        chooseMenu.setItem(49, ItemBuilder.from(Material.PAPER).name(Component.text(name1)).setLore(" ", lore1).asGuiItem(event1 -> {
            if (notUnlockedTitles.size() != 0) {
                openShopMenu(player);
            } else {
                prefix(player, "&7Posiadasz wszystkie dostępne tytuły!");
            }
        }));

        chooseMenu.setItem(50, ItemBuilder.from(Material.LIME_STAINED_GLASS_PANE).name(Component.text(color("&a→"))).asGuiItem(event -> {
            chooseMenu.next();
            chooseMenu.updateTitle(translate("&#45fb7d&lM&#48fb85&lE&#4cfb8e&lN&#4ffb96&lU &#52fb9f&lW&#56fba7&lY&#59fbb0&lB&#5cfbb8&lO&#60fbc1&lR&#63fbc9&lU &7") + chooseMenu.getCurrentPageNum() + "/" + chooseMenu.getPagesNum());
            chooseMenu.update();
        }));

        if(owner.getCurrentTitle().contains("default")){
            chooseMenu.setItem(52, ItemBuilder.from(Material.PLAYER_HEAD).setSkullOwner(player).name(Component.text(color("&7Aktualny tytuł:"))).setLore(color("&cʙʀᴀᴋ")).asGuiItem());
        } else {
            chooseMenu.setItem(52, ItemBuilder.from(Material.PLAYER_HEAD).setSkullOwner(player).name(Component.text(color("&7Aktualny tytuł:"))).setLore(translate(getTitle(owner.getCurrentTitle()).getPrefix())).asGuiItem());
        }

        for (Title title : owner.getUnlockedTitles()) {
            ItemStack is = new ItemStack(Material.PAPER);
            ItemMeta im = is.getItemMeta();

            if (Objects.equals(title.getName(), owner.getCurrentTitle())) {

                im.addEnchant(Enchantment.MENDING, 1, true);
                im.setLore(Arrays.asList(color("&aᴀᴋᴛᴜᴀʟɴɪᴇ ᴡʏʙʀᴀɴʏ ᴛʏᴛᴜʟ"), " ", color("&cᴋʟɪᴋɴɪᴊ, ᴀʙʏ ᴅᴇᴢᴀᴋᴛʏᴡᴏᴡᴀᴄ!")));
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                im.setDisplayName(translate(title.getPrefix()));

                is.setItemMeta(im);

                chooseMenu.addItem(ItemBuilder.from(is).asGuiItem(event -> {
                    Player target = (Player) event.getWhoClicked();
                    setPlayerPrefix(target.getName(), "default");
                    prefix(target, "&cDezaktywowano tytuł!");
                    ItemMeta im2 = is.getItemMeta();
                    im2.setLore(Arrays.asList(color("&7ᴋʟɪᴋɴɪᴊ, ᴀʙʏ ᴡʏʙʀᴀᴄ ᴛᴇɴ ᴛʏᴛᴜʟ!")));
                    im2.removeEnchant(Enchantment.MENDING);
                    is.setItemMeta(im2);
                    openChooseMenu(player);
                }));
            } else {

                im.setLore(Arrays.asList(color("&7ᴋʟɪᴋɴɪᴊ, ᴀʙʏ ᴡʏʙʀᴀᴄ ᴛᴇɴ ᴛʏᴛᴜʟ!")));
                im.setDisplayName(translate(title.getPrefix()));

                is.setItemMeta(im);

                chooseMenu.addItem(ItemBuilder.from(is).asGuiItem(event -> {
                    Player target = (Player) event.getWhoClicked();
                    setPlayerPrefix(target.getName(), title.getName());
                    prefix(target, "&7Ustawiono tytuł: " + translate(title.getPrefix()));
                    openChooseMenu(player);
                }));
            }
        }
        chooseMenu.open(player);
        chooseMenu.updateTitle(translate("&#45fb7d&lM&#48fb85&lE&#4cfb8e&lN&#4ffb96&lU &#52fb9f&lW&#56fba7&lY&#59fbb0&lB&#5cfbb8&lO&#60fbc1&lR&#63fbc9&lU &7") + chooseMenu.getCurrentPageNum() + "/" + chooseMenu.getPagesNum());
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