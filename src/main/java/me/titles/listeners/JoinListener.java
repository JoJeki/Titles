package me.titles.listeners;

//import me.clip.placeholderapi.PlaceholderAPI;
//import org.bukkit.Bukkit;
import me.titles.Titles;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
//import pl.pijok.titles.Titles;
//import pl.pijok.titles.owner.Owner;
//import pl.pijok.titles.title.Title;
//import pl.pijok.titles.title.TitleController;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();

        Titles.getOwnerController().loadPlayer(player.getName());
        Titles.getTitleController().checkTitle(player);

    }
}
