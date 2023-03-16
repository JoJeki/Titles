package me.titles.category;

//import me.mattstudios.mfgui.gui.components.ItemBuilder;
//import me.mattstudios.mfgui.gui.guis.Gui;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import me.titles.Titles;
import me.titles.essentials.ChatUtils;
import me.titles.essentials.ConfigUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
//import pl.pijok.titles.Titles;
//import pl.pijok.titles.essentials.ChatUtils;
//import pl.pijok.titles.essentials.ConfigUtils;

public class CategoryController {

    private Gui categorySelect;

    public void load(){

        YamlConfiguration configuration = ConfigUtils.load("categories.yml", Titles.getInstance());

        categorySelect = new Gui(
                configuration.getInt("gui.rows"),
                ChatUtils.fixColor(configuration.getString("gui.title"))
        );

        categorySelect.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        categorySelect.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).asGuiItem());

        for(String categoryName : configuration.getConfigurationSection("categories").getKeys(false)){

            ItemStack icon = ConfigUtils.getItemstack(configuration, "categories." + categoryName + ".icon");
            int slot = configuration.getInt("categories." + categoryName + ".slot");

            categorySelect.setItem(slot, ItemBuilder.from(icon).asGuiItem(event -> {

                Titles.getTitleController().openShop((Player) event.getWhoClicked(), categoryName);

            }));
        }
    }

    public void openCategorySelect(Player player){
        categorySelect.open(player);
    }

}
