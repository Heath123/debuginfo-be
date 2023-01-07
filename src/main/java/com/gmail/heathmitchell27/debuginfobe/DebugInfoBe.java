package com.gmail.heathmitchell27.debuginfobe;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public class DebugInfoBe extends JavaPlugin {
    @Override
    public void onLoad() {
        getLogger().info("Loaded debuginfo-be!");
    }

    @Override
    public void onEnable() 
    {
    	final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(getDataFolder().toPath().resolve("debuginfobe.conf"))
                .defaultOptions(opts -> opts.header("DebugInfo-BE"))
                .build();

        final DebugInfoBeConfiguration config;
        try {
            final CommentedConfigurationNode node = loader.load();
            config = node.get(DebugInfoBeConfiguration.class);
            loader.save(node);
        } catch (ConfigurateException e) {
            getLogger().warning("Could not load config!");
            e.printStackTrace();
            return;
        }
    	
        MyListener myListener = new MyListener(this, config);
        getServer().getPluginManager().registerEvents(myListener, this);

        Messenger messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(this, "minecraft:brand", new BrandPluginMessageListener());

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
					// Update every 0.25 seconds
                    myListener.updateInfo(player, player.getLocation());
                }
            }
        }, 0L, 5L);

        this.getCommand("f3").setExecutor(new CommandF3(myListener));
        this.getCommand("debuginfo-be").setExecutor(new CommandF3(myListener));
        getLogger().info("Enabled debuginfo-be!");
    }
}
