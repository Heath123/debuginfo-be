package com.gmail.heathmitchell27.debuginfobe;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.text.DecimalFormat;
import java.util.HashMap;

/*	Changes Added to Project by TBYT
 * 	Biome of current player. Edges of biome may not be accurate for determining correct biome.
 * 	Updated pom.xml to newest api.
 * 	Difficulty.
 * 	Current Chunk.
 * 	Exclusive: Chunk Borders for Bedrock Players! inline edge of the 16x16 chunk is displayed as a border.
 * 	Current Player World.
 * 	Current Player Gamemode.
 * 	Client View Distance.
 * 	Minimalized code.
 */
public class MyListener implements Listener
{
    public static void main(String[] args) {
        System.out.println("Hello, world!");
    }

    private static DecimalFormat twoPlaces = new DecimalFormat("#.###");
    static HashMap<Player, BossBar> bossBarMap = new HashMap<>();
    static HashMap<Player, Boolean> showDebugScreenMap = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        updateInfo(player, e.getTo());
    }

    public static void updateInfo(Player player, Location location) {
        // Return if no brand yet sent
        if (BrandPluginMessageListener.playerBrands.get(player) == null) return;
        // Return if not Geyser
        if (!BrandPluginMessageListener.playerBrands.get(player).equals("Geyser")) return;

        BossBar currentBossBar = bossBarMap.get(player);

        // If the player boss bar isn't null and should be removed
        Boolean currentOption = showDebugScreenMap.get(player);
        if (currentOption == null) currentOption = true;

        if (currentBossBar != null && !currentOption) {
            // Remove the boss bar
            currentBossBar.removePlayer(player);
            bossBarMap.remove(player);
        }

        if (!currentOption) return;

        Block targetBlock = player.getTargetBlockExact(6);

        if (currentBossBar == null) {
            currentBossBar = Bukkit.createBossBar(getBossBarTitle(player, location, targetBlock), BarColor.BLUE, BarStyle.SOLID);
            currentBossBar.addPlayer(player);
            bossBarMap.put(player, currentBossBar);
        } else {
            currentBossBar.setTitle(getBossBarTitle(player, location, targetBlock));
        }

        if (targetBlock == null || targetBlock.getLocation() == null) 
        {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("-"));
        }
        else 
        {
        //Light does not work properly, left out of implementation: new TextComponent("Light: "+targetBlock.getLightLevel()+" ("+targetBlock.getLightFromSky()+" Sky, "+targetBlock.getLightFromBlocks()+" block)\n"+
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
            		targetBlock.getBlockData().getAsString()
                            .replaceFirst("^minecraft:", "") // Frees up space
                            .replaceAll(",", ",\n") // Split onto
                            .replaceAll("\\[", "\n[") // multiple lines
            +"\nBlock at: " + (int) targetBlock.getLocation().getX() + ", " +
            (int) targetBlock.getLocation().getY() + ", " +
            (int) targetBlock.getLocation().getZ()));
        }
    }

	private static String getBossBarTitle(Player player, Location location, Block targetBlock) {
		World world = player.getWorld();
		Chunk chunk = world.getChunkAt(player.getLocation());
		int y = (int) player.getLocation().getY();
		for (int i = 0; i < 16; i++) {
			if (i == 8) {
				y = (int) player.getLocation().getY();
			}
			if (i < 8 && i!=0) {
				y++;
			} 
			else if (i > 7) {
				y--;
			}
			for (int x = 0; x < 16; x++) {
				if (x==0 || x==15) {
					for (int z = 0; z < 16; z++) {
							Block b = chunk.getBlock(x, y, z);
							player.sendBlockDamage(b.getLocation(), 1.0f);
					}
				}
			}
			for (int z = 0; z < 16; z++) {
				if (z==0 || z==15) {
					for (int x = 0; x < 16; x++) {
							Block b = chunk.getBlock(x, y, z);
							player.sendBlockDamage(b.getLocation(), 1.0f);
					}
				}
			}
		}
		
        String debugString = "- F3 Debug Info for Geyser -\n\n";
        debugString += "minecraft:"+world.getName()+"\n";
        debugString += "Difficulty: "+world.getDifficulty()+"\n";
        debugString += "Mode: "+ player.getGameMode()+"\n";
        
        debugString += "Pos: " +  twoPlaces.format(location.getX()) + ", " +  twoPlaces.format(location.getY()) +
                ", " +  twoPlaces.format(location.getZ());
        
        debugString += "\nChunk: "+chunk.getX()+","+chunk.getZ();
        
        debugString += "\nBiome: "+world.getBiome(location);
        
        debugString += "\nFacing: ";

        // https://stackoverflow.com/questions/35831619/get-the-direction-a-player-is-looking - second answer
        float yaw = location.getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            debugString += "south";
        } else if (yaw < 135) {
            debugString += "west";
        } else if (yaw < 225) {
            debugString += "north";
        } else if (yaw < 315) {
            debugString += "east";
        } else {
            debugString += "north";
        }

        debugString += "\nClient View Distance: "+ player.getClientViewDistance();
        
        return debugString;
    }
}