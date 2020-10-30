package com.gmail.heathmitchell27.debuginfobe;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class BrandPluginMessageListener implements PluginMessageListener {
    static HashMap<Player, String> playerBrands = new HashMap<>();

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] msg) {
        try {
            playerBrands.put(p, new String(msg, "UTF-8").substring(1));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}