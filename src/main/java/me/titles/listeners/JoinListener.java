package me.titles.listeners;

import me.titles.Titles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();

        Titles.getOwnerController().loadPlayer(player.getName());
        Titles.getTitleController().checkTitle(player);

//        if(!player.hasPlayedBefore()){
//            String clearPrefix = "lp user " + player.getName() + " meta removeprefix 10 * server=prison";
//            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), clearPrefix);
//
//            String basePrefix = "lp user " + player.getName() + " permission set prefix.10.&r server=prison";
//            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), basePrefix);
//        }
    }
}
