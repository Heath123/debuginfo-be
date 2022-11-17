package com.gmail.heathmitchell27.debuginfobe;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandF3 implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equals("f3"))
        {
	    	if (sender instanceof Player) {
	            Player player = (Player) sender;
	
	            // Return if no brand yet sent or the player isn't on Geyser
	            if (BrandPluginMessageListener.playerBrands.get(player) == null ||
	                    !BrandPluginMessageListener.playerBrands.get(player).equals("Geyser")) {
	                sender.sendMessage(ChatColor.RED + "You don't appear to be a Geyser player!");
	                return true;
	            }
	
	            Boolean currentOption = MyListener.showDebugScreenMap.get(player);
	            if (currentOption == null) currentOption = true;
	
	            MyListener.showDebugScreenMap.put(player, !currentOption);
	        } else {
	            sender.sendMessage(ChatColor.RED + "You cannot use this from the console!");
	        }
        }
        else if(label.equals("debuginfo-be"))
        {
        	sender.sendMessage(ChatColor.AQUA + "This plugin displays debug information for Bedrock Edition Players!\n"
        			+ "Endorsed by Geyser on https://wiki.geysermc.org/other/developer-guide/"
        			+ "\nReleases: https://github.com/Heath123/debuginfo-be/releases");
        }
        return true;
    }
}
