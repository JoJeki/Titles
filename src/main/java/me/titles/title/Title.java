package me.titles.title;

public class Title {

    private String name;
    private String category;
    private String prefix;
    private double price;

    public Title(String name, String category, String prefix, double price){
        this.name = name;
        this.category = category;
        this.prefix = prefix;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
