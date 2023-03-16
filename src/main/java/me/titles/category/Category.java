package me.titles.category;

import org.bukkit.inventory.ItemStack;

public class Category {

    private String name;
    private int slot;
    private ItemStack icon;

    public Category(String name, int slot, ItemStack icon){
        this.name = name;
        this.slot = slot;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }
}
