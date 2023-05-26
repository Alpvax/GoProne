# GoProne

Minecraft mod to allow players to manually go prone and crawl through 1-block-spaces. Uses the 1.14+ vanilla mechanic.

Just adds a keybind (default "C") which sets the player to the crawling pose, allowing them to sneak through 1-block high gaps, without needing to squash something on top of them.

Version 1.1.0 onwards also adds a second keybind (not bound to anything by default) which allows you to toggle being prone.


The versions are not currently cross compatible (i.e. you can't play on Fabric servers with a GoProne Forge client and vice versa).
Part of my motivation for taking over the fabric port is so that I can hopefully overcome that obstacle.

## Minecraft Forge Version
[![](http://cf.way2muchnoise.eu/versions/goprone.svg)](https://www.curseforge.com/minecraft/mc-mods/goprone)
[![](http://cf.way2muchnoise.eu/full_goprone_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/goprone)

[Download from Curseforge](https://www.curseforge.com/minecraft/mc-mods/goprone/files)

Requires [Minecraft Forge](https://files.minecraftforge.net). Not required on the client (Although clients without it will not be able to go prone).

This version is not compatible with Fabric (i.e. you can't play on Fabric servers with a GoProne Forge client).
Part of the motivation for me taking over the fabric port is so that I can hopefully overcome that obstacle.

## Fabric Version
[![](http://cf.way2muchnoise.eu/versions/goprone-fabric.svg)](https://www.curseforge.com/minecraft/mc-mods/goprone-fabric)
[![](http://cf.way2muchnoise.eu/full_goprone-fabric_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/goprone-fabric)

[Download from Curseforge](https://www.curseforge.com/minecraft/mc-mods/goprone-fabric)

Requires [Fabric](https://fabricmc.net/use/) and [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)

Originally ported by VidTu ([Original repo here](https://github.com/VidTu/GoProne-Fabric)) with my permission.

This version is not compatible with Forge (i.e. you can't play on Forge servers with a GoProne Fabric client).
Part of the motivation for me taking over the fabric port is so that I can hopefully overcome that obstacle.

### Pre-refactor (Pre-1.19.4) Differences from Forge version
Server config can be found in `config/goprone.json` and uses JSON instead of TOML.

### Post refactor
The config has changed to use [the Forge Config API Port for Fabric](https://www.curseforge.com/minecraft/mc-mods/forge-config-api-port-fabric) in order to make the config files transferrable across versions. The first time you play with the new version it should copy your config settings from the old file to the new format.

---
## Configuration
TODO

---
## Licence and Restrictions
You may use this mod in modpacks without asking permission.

You may not upload this mod to alternative sites without my express permission. [StopModReposts](https://stopmodreposts.org/)
(Any sites where the mod is uploaded by someone other than me could be malicious i.e. viruses).

---
## MultiLoader Template

This project was refactored using [jaredlll08's MultiLoader-Template](https://github.com/jaredlll08/MultiLoader-Template/tree/1.19.3) for Minecraft 1.19.4 in order to make developing for multiple loaders easier after I took over the fabric port.

The relevant part of the MultiLoader readme is reproduced (modified) below to give instructions on setting up this project for development

### Getting Started with IntelliJ IDEA
This guide will show how to import the Project into IntelliJ IDEA. The setup process is roughly equivalent to setting up Forge and Fabric independently and should be very familiar to anyone who has worked with their MDKs.

1. Clone or download this repository.
2. Open the project's root folder as a new project in IDEA. This is the folder that contains this README file and the gradlew executable.
4. If your default JVM/JDK is not Java 17 you will encounter an error when opening the project. This error is fixed by going to `File > Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JVM`and changing the value to a valid Java 17 JVM. You will also need to set the Project SDK to Java 17. This can be done by going to `File > Project Structure > Project SDK`. Once both have been set open the Gradle tab in IDEA and click the refresh button to reload the project.
5. Open the Gradle tab in IDEA if it has not already been opened. Navigate to `GoProne > Common > Tasks > vanilla gradle > decompile`. Run this task to decompile Minecraft.
6. Open the Gradle tab in IDEA if it has not already been opened. Navigate to `GoProne > Forge > Tasks > forgegradle runs > genIntellijRuns`. Run this task to set up run configurations for Forge.
7. Open the Run/Debug Configurations. Under the Application category there should now be options to run Forge and Fabric projects. Select one of the client options and try to run it.
8. Assuming you were able to run the game in step 7 your workspace should now be set up.

### Development Guide
When using this template the majority of the mod is developed in the Common project. The Common project is compiled against the vanilla game and is used to hold code that is shared between the different loader-specific versions of the mod. The Common project has no knowledge or access to ModLoader specific code, apis, or concepts. Code that requires something from a specific loader must be done through the project that is specific to that loader, such as the Forge or Fabric project.

Loader specific projects such as the Forge and Fabric project are used to load the Common project into the game. These projects also define code that is specific to that loader. Loader specific projects can access all of the code in the Common project. It is important to remember that the Common project can not access code from loader specific projects.
