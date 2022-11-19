# debuginfo-be [![Java CI with Maven](https://github.com/Heath123/debuginfo-be/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/Heath123/debuginfo-be/actions?query=workflow%3A%22Java+CI+with+Maven%22) [![Discord](https://badgen.net/badge/icon/discord?icon=discord&label)](https://discord.geysermc.org)

A small quick Spigot plugin to give F3-like debug info to [Geyser](https://github.com/GeyserMC/Geyser) clients!

Featured on the Official [Geyser Wiki](https://wiki.geysermc.org/other/developer-guide/)!

### Downloading Latest Release
A jar packaged version of the latest release can be downloaded from the latest passing action on the [Releases Page](https://github.com/Heath123/debuginfo-be/releases/latest).

### Features
* Current Position with decimals.
* Show's the position of the block player is looking at.
* Show's the data of the block player is looking at.
* Show's the direction player is facing.
* Biome of current player.
* * Blocks Edges at the of biome may not be accurate for determining correct biome, when compared to Java debugging.
* Difficulty of Server.
* Game Time of Day
* Current Chunk. (X,Z)
* Chunk Borders! 
* * inline edge of the 16x16 chunk is displayed as a border with block breaking animation or particle animation. (Thanks TBYT).
* Current Player World/Dimension.
* Current Player Gamemode.
* Server View Distance & Server Simulation Distance.

## Support
[![Discord](https://badgen.net/badge/icon/discord?icon=discord&label)](https://discord.geysermc.org)

Mention Circuit10 or @TBYT

## Usage

Toggle the debug HUD with `/f3`

Toggle the Chunk Border Particle Level with `/f3 <ParticleLevel>`
- An example would be `/f3 2`
- To turn off borders use `/f3 0`
- More information about command `/f3 ?`

Information about this plugin use `/debuginfo-be`

Contributions are welcomed & appreciated!

## Screenshot 

![Screenshot](https://user-images.githubusercontent.com/48810871/202431633-88617526-3171-43e6-9200-20146c5066b5.JPG)

## Video Showcase

![Coming Soon](https://github.com/Heath123/debuginfo-be)
