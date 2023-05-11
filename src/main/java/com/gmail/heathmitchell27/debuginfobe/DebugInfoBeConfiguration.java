package com.gmail.heathmitchell27.debuginfobe;


import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public final class DebugInfoBeConfiguration {
	/* README.md
	 * Toggle the Chunk Border Particle Level with /f3 <ParticleLevel>

		An example would be /f3 2
		To turn off borders use /f3 0
	 */
	@Comment("\nFor example, if set to 4, this will show 8 y levels of particles, if in this example default particle level is 2.")
	private int defaultParticleMultiplier = 1;
	@Comment("\nThe Max Number for the Particle Level a player can set.")
	private int defaultMaxParticleLevel = 16;
	//This will show up first in the .conf file although it is placed last here.
	@Comment("\nIf you do not want Chunk Border Particles Showing for players, set defaultParticleLevel to 0 and defaultMaxParticleLevel to 0."
			+ "\nFor example, if set to 2, this will show 2 y levels of particles, if in this example the multiplier is 1.")
	private int defaultParticleLevel = 8;

	public int defaultParticleMultiplier() {
		return defaultParticleMultiplier;
	}
	
	public int defaultParticleLevel() {
		return defaultParticleLevel;
	}
	
	public int defaultMaxParticleLevel() {
		return defaultMaxParticleLevel;
	}
}