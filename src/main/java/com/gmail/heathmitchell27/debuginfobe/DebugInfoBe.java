package com.gmail.heathmitchell27.debuginfobe;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;

public class DebugInfoBe extends JavaPlugin {
    @Override
    public void onLoad() {
        getLogger().info("Loaded debuginfo-be!");
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new MyListener(), this);

        Messenger messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(this, "minecraft:brand", new BrandPluginMessageListener());

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Update every 0.25 seconds
                    MyListener.updateInfo(player, player.getLocation());
                }
            }
        }, 0L, 5L);

        getLogger().info("Enabled debuginfo-be!");
    }
}
