package me.titles.title;

public class Title {

    private String name;
    private String prefix;
    private double price;

    public Title(String name, String prefix, double price){
        this.name = name;
        this.prefix = prefix;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
