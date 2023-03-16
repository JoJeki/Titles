package me.titles.owner;

import me.titles.Titles;
import me.titles.essentials.ConfigUtils;
import me.titles.title.Title;
import org.bukkit.configuration.file.YamlConfiguration;
//import pl.pijok.titles.Titles;
//import pl.pijok.titles.essentials.ConfigUtils;
//import pl.pijok.titles.title.Title;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OwnerController {

    private HashMap<String, Owner> owners = new HashMap<>();

    public void loadPlayer(String nickname){
        YamlConfiguration configuration = ConfigUtils.load("players.yml", Titles.getInstance());

        if(configuration.contains("players." + nickname)){

            ArrayList<Title> unlockedTitles = new ArrayList<>();
            String current = null;

            for(String titleName : configuration.getStringList("players." + nickname + ".titles")){
                unlockedTitles.add(Titles.getTitleController().getTitle(titleName));
            }
            current = configuration.getString("players." + nickname + ".current");

            owners.put(nickname, new Owner(current, unlockedTitles));
        }
        else{
            owners.put(nickname, new Owner("none", new ArrayList<>()));
        }
    }

    public void savePlayer(String nickname){
        YamlConfiguration configuration = ConfigUtils.load("players.yml", Titles.getInstance());

        List<String> titlesIDs = new ArrayList<>();
        Owner owner = owners.get(nickname);

        for(Title title : owner.getUnlockedTitles()){
            titlesIDs.add(title.getName());
        }

        configuration.set("players." + nickname + ".titles", titlesIDs);
        configuration.set("players." + nickname + ".current", owner.getCurrentTitle());

        ConfigUtils.save(configuration, "players.yml");
    }

    public Owner getOwner(String nickname){
        return owners.get(nickname);
    }
}
