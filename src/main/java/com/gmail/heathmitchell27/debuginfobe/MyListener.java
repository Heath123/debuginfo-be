package com.gmail.heathmitchell27.debuginfobe;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
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
import java.util.HashSet;

public class MyListener implements Listener
{
    private static DecimalFormat twoPlaces = new DecimalFormat("#.###");
    static HashMap<Player, BossBar> bossBarMap = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        updateInfo(player, e.getTo());
    }

    public static void updateInfo(Player player, Location location) {
        if (!BrandPluginMessageListener.playerBrands.get(player).equals("Geyser")) return;

        BossBar currentBossBar = bossBarMap.get(player);

        Block targetBlock = player.getTargetBlockExact(6);

        if (currentBossBar == null) {
            currentBossBar = Bukkit.createBossBar(getBossBarTitle(player, location, targetBlock), BarColor.BLUE, BarStyle.SOLID);
            currentBossBar.addPlayer(player);
            bossBarMap.put(player, currentBossBar);
        } else {
            currentBossBar.setTitle(getBossBarTitle(player, location, targetBlock));
        }

        if (targetBlock == null || targetBlock.getLocation() == null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("-"));
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    targetBlock.getBlockData().getAsString()
                            .replaceFirst("^minecraft:", "") // Frees up space
                            .replaceAll(",", ",\n") // Split onto
                            .replaceAll("\\[", "\n[") // multiple lines
            ));
        }
    }

    private static String getBossBarTitle(Player player, Location location, Block targetBlock) {
        String debugString = "------- Debug info for Geyser -------\n\n" +
                "Pos: " +  twoPlaces.format(location.getX()) + " " +  twoPlaces.format(location.getY()) +
                " " +  twoPlaces.format(location.getZ());

        if (targetBlock == null || targetBlock.getLocation() == null) {
            debugString += "\nLooking at: - - -";
        } else {
            debugString += "\nLooking at: " + (int) targetBlock.getLocation().getX() + " " +
                    (int) targetBlock.getLocation().getY() + " " +
                    (int) targetBlock.getLocation().getZ();
        }

        return debugString;
    }
}