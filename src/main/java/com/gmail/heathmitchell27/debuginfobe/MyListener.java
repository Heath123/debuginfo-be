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
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.geysermc.floodgate.api.FloodgateApi;

import java.text.DecimalFormat;
import java.util.HashMap;

/*	Changes Added to Project by TBYT
 * 	Biome of current player. *Blocks Edges at the of biome may not be accurate for determining correct biome, when compared to Java debugging.
 * 	Difficulty.
 * 	Current Chunk.
 * 	Brackets next to Chunk will now display player location residing in chunk. (Nickname: SubChunk) Example usage would be for finding buried treasure.
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

    private static DecimalFormat twoPlaces = new DecimalFormat("#.##");
    static HashMap<Player, BossBar> bossBarMap = new HashMap<>();
    static HashMap<Player, Boolean> showDebugScreenMap = new HashMap<>();
    private static int alternatingTicks = 0; //offsets the bukkitscheduler period every interval.
	static int particleLevel = 2;
	static boolean changedDimensionsEvent;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) 
    {
    	Player player = e.getPlayer();
    	// Return if no brand yet sent or the player isn't on Geyser/floodgate.
    	if ((BrandPluginMessageListener.playerBrands.get(player) == null || !BrandPluginMessageListener.playerBrands.get(player).equals("Geyser"))
    			&&(!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())))
    	{
    		return;
    	}
    	String defaultMessage = e.getJoinMessage();
		e.setJoinMessage(defaultMessage+"\n"+ChatColor.AQUA+"Use /f3 to disable or enable debug menu.");
		e.getPlayer().sendMessage("\n"+ChatColor.AQUA+"Use /f3 to disable or enable debug menu.");
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
    	alternatingTicks = 0;
        Player player = e.getPlayer();
        updateInfo(player, e.getTo());
    }
    
    @EventHandler
    public void onDimensionChange(PlayerChangedWorldEvent e)
    {
    	changedDimensionsEvent = true;
    	alternatingTicks=0;
    }

    public static void updateInfo(Player player, Location location) {
    	// Return if no brand yet sent or the player isn't on Geyser/floodgate.
    	if ((BrandPluginMessageListener.playerBrands.get(player) == null || !BrandPluginMessageListener.playerBrands.get(player).equals("Geyser"))
    			&&(!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())))
    	{
    		return;
    	}
        BossBar currentBossBar = bossBarMap.get(player);

        Boolean currentOption = showDebugScreenMap.get(player);
        //start with debug off.
        if (currentOption == null) 
        	currentOption = false;

     // If the player boss bar isn't null and should be removed
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
        	// If bossbar immediately does not appear, stand still for 8 intervals. (2 seconds)
        	if(changedDimensionsEvent==true&&alternatingTicks==8)
        	{
        		currentBossBar.removePlayer(player);
            	currentBossBar.addPlayer(player);
            	changedDimensionsEvent = false;
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
		String debugString = "- F3 Debug Information -\n\n";
		World world = player.getWorld();
		Chunk chunk = world.getChunkAt(player.getLocation());
		
		//adding alternating intervals decreases amount of times visual effect appears, reducing lag.
		if(particleLevel>4)
		{
			particleLevel=2;
			player.sendMessage("\n"+ChatColor.RED+"Number too large for particles!");
		}
		int n = particleLevel*4;

		if (alternatingTicks % 2 == 0) {
			int y = (int) player.getLocation().getY() + 1; //adding one to spawn the particles one block higher to be in the middle of the player.
			for (int i = 0; i < n; i++) {
				if (i == (n / 2)) {
					y = (int) player.getLocation().getY() + 1;
				}
				if (i < (n / 2) && i != 0) {
					y++;
				} else if (i > ((n / 2) - 1)) {
					y--;
				}
				for (int x = 0; x < 16; x++) {
					if (x == 0 || x == 15) {
						for (int z = 0; z < 16; z++) {
							if (y < 321 && y > -65) {
								Block b = chunk.getBlock(x, y, z);
								if (b.getBlockData().getMaterial() != Material.WATER
										&& b.getBlockData().getMaterial() != Material.LAVA) {
									if ((b.getBlockData().getMaterial() == Material.AIR
											|| b.getBlockData().getMaterial() == Material.CAVE_AIR)) {
										world.spawnParticle(Particle.FIREWORKS_SPARK, b.getLocation().getX() + 0.5,
												b.getLocation().getY() + 0.5, b.getLocation().getZ() + 0.5, 1);
									} else {
										player.sendBlockDamage(b.getLocation(), 1.0f);
									}
								}
							}
						}
					}
				}
				for (int z = 0; z < 16; z++) {
					if (z == 0 || z == 15) {
						for (int x = 0; x < 16; x++) {
							if (y < 321 && y > -65) {
								Block b = chunk.getBlock(x, y, z);
								if (b.getBlockData().getMaterial() != Material.WATER
										&& b.getBlockData().getMaterial() != Material.LAVA) {
									if ((b.getBlockData().getMaterial() == Material.AIR
											|| b.getBlockData().getMaterial() == Material.CAVE_AIR)) {
										world.spawnParticle(Particle.FIREWORKS_SPARK, b.getLocation().getX() + 0.5,
												b.getLocation().getY() + 0.5, b.getLocation().getZ() + 0.5, 1);
									} else {
										player.sendBlockDamage(b.getLocation(), 1.0f);
									}
								}
							}
						}
					}
				}
			}
		}
		
		alternatingTicks++;
		
		debugString += "minecraft:"+world.getName();
        debugString += "\nDifficulty: "+world.getDifficulty();
        debugString += "\nMode: "+ player.getGameMode();
        debugString += "\nView Distance: "+ player.getServer().getViewDistance();
        debugString += "\nSimulation Distance: "+ player.getServer().getSimulationDistance();
        debugString += "\nTime: "+world.getTime()+"\n";
        debugString += "Pos: " +  twoPlaces.format(location.getX()) + ", " +  twoPlaces.format(location.getY()) +
                ", " +  twoPlaces.format(location.getZ());
        //Rigorous Mathematics to determine player equivalent for Java Position Where At in Chunk (SubChunk?)
        //SubChunks now exactly as is in Java Edition Lines 222-256
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();
        if(y<0)
    		y-=15;
        debugString += "\nChunk: "+chunk.getX()+","+(y/16)+","+chunk.getZ();
        //Axis Spawn Fix, the negative end of the zero border (-1) for X and Z, subchunk of each axis defaults to 0 when its supposed to be 15. Like 15 to 14 to 13, not 0 to 14 to 13. This fixes that.
        boolean xAxisSpawnFix=false;
        boolean zAxisSpawnFix=false;
        if(location.getX()<0&&location.getX()>-1)
        	xAxisSpawnFix=true;
        if(location.getZ()<0&&location.getZ()>-1)
        	zAxisSpawnFix=true;
        //Offset because its 0-5 not 1-16. 
        if(x<0)
    		x-=1;
        if(z<0)
    		z-=1;
        y = (int) location.getY();
        //Getting the remainder of dividing by 16 determines subchunk position.
        x=(x)%16;
        y=(y)%16;
        z=(z)%16;
        //Don't display subchunks as negatives, doing this makes it exactly how Java Edition does it.
        if(x<0)
        	x+=16;
        if(y<0)
    		y+=16;
        if(z<0)
        	z+=16;
        
        if(xAxisSpawnFix)
        	x=15;
        if(zAxisSpawnFix)
        	z=15;
        //Lines 222-260 by TBYT
        
        debugString += " in ["+x+","+y+","+z+"]";
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
        
        return debugString;
    }
}