package com.gmail.heathmitchell27.debuginfobe;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BlockIterator;

import java.text.DecimalFormat;
import java.util.HashMap;

public class MyListener implements Listener
{
    private static DecimalFormat df2 = new DecimalFormat("#.###");
    static HashMap<Player, BossBar> bossBarMap = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        updateInfo(player, e.getTo());
    }

    public static void updateInfo(Player player, Location location) {
        if (!BrandPluginMessageListener.playerBrands.get(player).equals("Geyser")) return;

        BossBar currentBossBar = bossBarMap.get(player);

        if (currentBossBar == null) {
            currentBossBar = Bukkit.createBossBar(getBossBarTitle(player, location), BarColor.BLUE, BarStyle.SOLID);
            currentBossBar.addPlayer(player);
            bossBarMap.put(player, currentBossBar);
        } else {
            currentBossBar.setTitle(getBossBarTitle(player, location));
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                getTargetBlock(player, 15).getBlockData().getAsString()
                        .replaceFirst("^minecraft:", "") // Frees up space
                        .replaceAll(",", ",\n") // Split onto
                        .replaceAll("\\[", "\n[") // multiple lines
        ));
    }

    private static String getBossBarTitle(Player player, Location location) {
        return "X: " +  df2.format(location.getX()) + " Y: " +  df2.format(location.getY()) +
                " Z: " +  df2.format(location.getZ());
    }

    // https://www.spigotmc.org/threads/solved-get-coords-for-a-block-a-player-is-looking-at.64576/
    // Not the only thing I copied but this is a big chunk of code
    private static Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }
}