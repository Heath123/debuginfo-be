package com.gmail.heathmitchell27.debuginfobe;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.text.DecimalFormat;
import java.util.HashMap;

/*	Changes Added to Project by TBYT
 * 	Biome of current player. *Blocks Edges at the of biome may not be accurate for determining correct biome, when compared to Java debugging.
 * 	Difficulty.
 * 	Current Chunk.
 * 	Exclusive: Chunk Borders for Bedrock Players! inline edge of the 16x16 chunk is displayed as a border with block breaking animation or particle animation.
 * 	Current Player World.
 * 	Current Player Gamemode.
 * 	View Distances.
 * 	Refractored code.
 *  ReEnables Bossbar when crossing dimensions!
 * 	Alternating Timings added to code.
 */
public class MyListener implements Listener
{
    public static void main(String[] args) 
    {
        //System.out.println("Hello, world!");
    }

    private static DecimalFormat twoPlaces = new DecimalFormat("#.###");
    static HashMap<Player, BossBar> bossBarMap = new HashMap<>();
    static HashMap<Player, Boolean> showDebugScreenMap = new HashMap<>();
    private static int alternatingTicks = 0; //offsets the bukkitscheduler period every interval.
    private static World prevWorld;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) 
    {
    	Player player = e.getPlayer();
    	if (BrandPluginMessageListener.playerBrands.get(player) == null ||
                !BrandPluginMessageListener.playerBrands.get(player).equals("Geyser"))
    	{
    		return;
    	}
		e.getPlayer().sendMessage("\n"+ChatColor.AQUA+"Use /f3 to disable or enable debug menu.");
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
    	alternatingTicks = 0;
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
            currentBossBar = Bukkit.createBossBar(getBossBarTitle(player, location), BarColor.BLUE, BarStyle.SOLID);
            currentBossBar.addPlayer(player);
            bossBarMap.put(player, currentBossBar);
        } else {
        	// When switching dimensions, bossbar does not appear, reenable when switching dimenions only.
            if(player.getWorld()!=prevWorld)
            {
            	currentBossBar.removePlayer(player);
            	currentBossBar.addPlayer(player);
            }
            currentBossBar.setTitle(getBossBarTitle(player, location));
            if(alternatingTicks>0&&alternatingTicks%5==0)
    		{
            currentBossBar.setColor(BarColor.GREEN);
    		} else {
    			currentBossBar.setColor(BarColor.BLUE);
    		}
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

	private static String getBossBarTitle(Player player, Location location) {
		String debugString = "- F3 Debug Info for Geyser -\n\n";
		World world = player.getWorld();
		Chunk chunk = world.getChunkAt(player.getLocation());
		debugString += "minecraft:"+world.getName()+"\n";
		debugString += "Time:"+world.getTime()+"\n";
		
		//adding alternating intervals decreases amount of times visual effect appears, reducing lag.
		if(alternatingTicks%2==0)
		{
			int y = (int) player.getLocation().getY();
			for (int i = 0; i < 16; i++) {
				if (i == 8) {
					y = (int) player.getLocation().getY();
				}
				if (i < 8 && i != 0) {
					y++;
				} else if (i > 7) {
					y--;
				}
				for (int x = 0; x < 16; x++) {
					if (x == 0 || x == 15) {
						for (int z = 0; z < 16; z++) {
							Block b = chunk.getBlock(x, y, z);
							if(b.getBlockData().getMaterial()!=Material.WATER && b.getBlockData().getMaterial()!=Material.LAVA)
							{
								if(b.getBlockData().getMaterial()==Material.AIR || b.getBlockData().getMaterial()==Material.CAVE_AIR)
								{
									world.spawnParticle(Particle.FIREWORKS_SPARK, b.getLocation().getX()+0.5, b.getLocation().getY()+0.5, b.getLocation().getZ()+0.5, 1);
								}
								else {
									player.sendBlockDamage(b.getLocation(), 1.0f);
								}
							}
						}
					}
				}
				for (int z = 0; z < 16; z++) {
					if (z == 0 || z == 15) {
						for (int x = 0; x < 16; x++) {
							Block b = chunk.getBlock(x, y, z);
							if(b.getBlockData().getMaterial()!=Material.WATER && b.getBlockData().getMaterial()!=Material.LAVA)
							{
								if(b.getBlockData().getMaterial()==Material.AIR || b.getBlockData().getMaterial()==Material.CAVE_AIR)
								{
									world.spawnParticle(Particle.FIREWORKS_SPARK, b.getLocation().getX()+0.5, b.getLocation().getY()+0.5, b.getLocation().getZ()+0.5, 1);
								}
								else {
									player.sendBlockDamage(b.getLocation(), 1.0f);
								}
							}
						}
					}
				}
			}
		}
		alternatingTicks++;
		
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
            debugString += "south (towards +Z)";
        } else if (yaw < 135) {
            debugString += "west (towards -X)";
        } else if (yaw < 225) {
            debugString += "north (towards -Z)";
        } else if (yaw < 315) {
            debugString += "east (towards +X)";
        } else {
            debugString += "north (towards -Z)";
        }
        
        debugString += "\nServer View Distance: "+ player.getServer().getViewDistance();
        debugString += "\nServer Simulation Distance: "+ player.getServer().getSimulationDistance();
        
        return debugString;
    }
}