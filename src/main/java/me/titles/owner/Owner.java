package me.titles.owner;

import me.titles.title.Title;

import java.util.ArrayList;

public class Owner {

    private String currentTitle;
    private ArrayList<Title> unlockedTitles;

    public Owner(String currentTitle, ArrayList<Title> unlockedTitles){
        this.currentTitle = currentTitle;
        this.unlockedTitles = unlockedTitles;
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public void setCurrentTitle(String currentTitle) {
        this.currentTitle = currentTitle;
    }

    public ArrayList<Title> getUnlockedTitles() {
        return unlockedTitles;
    }

    public void setUnlockedTitles(ArrayList<Title> unlockedTitles) {
        this.unlockedTitles = unlockedTitles;
    }
}
