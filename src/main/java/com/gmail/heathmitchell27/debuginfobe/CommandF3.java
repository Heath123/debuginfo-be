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
	            if (currentOption == null) currentOption = false;
	
	            try {
	            	if(args.length==1)
	            	{
	            		int number = Integer.parseInt(args[0]);
	            		MyListener.particleLevel = number;
	            	}
	            }
	            catch (NumberFormatException e) {
	            	help(sender);
	            }
	            if(args.length==0)
	            	MyListener.showDebugScreenMap.put(player, !currentOption);
	        } else {
	            sender.sendMessage(ChatColor.RED + "You cannot use this from the console!");
	        }
        }
        else if(label.equals("debuginfo-be"))
        {
        	if(args.length>0)
        		help(sender);
        	else
        		sender.sendMessage(ChatColor.AQUA + "This plugin displays debug information for Bedrock Edition Players!\n"
        			+ "Endorsed by Geyser on https://wiki.geysermc.org/other/developer-guide/"
        			+ "\nReleases: https://github.com/Heath123/debuginfo-be/releases"
        			+ "\nFor commands use /f3 ?");
        }
        return true;
    }
    
    public void help(CommandSender sender)
    {
    	sender.sendMessage(ChatColor.AQUA + "\nUse /f3 <ParticleLevel> for the amount of particles each block height."+"\nUsing \"/f3 0\" will turn off particles from spawning."
	            +"\nExample of usage is \"/f3 1\" will show 4 levels of particles. (2 levels from player position to above your head and 2 below your head)"
	            +"\n/f3 by default uses level 3. (12 total levels in height, 6 above 6 below.");
    }
}
