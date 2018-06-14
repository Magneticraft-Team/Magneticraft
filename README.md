[![Support Server Invite](https://img.shields.io/badge/Join-Magneticraft-7289DA.svg?style=flat-square)](https://discord.gg/EhYbA97) [![Build Status](https://travis-ci.org/Magneticraft-Team/Magneticraft.svg?branch=kt1.9)](https://travis-ci.org/Magneticraft-Team/Magneticraft)

# Magneticraft
Magneticraft mod, more info in curseforge https://minecraft.curseforge.com/projects/magneticraft

### Do you want to contribute?
You don't need to be a programer or artist to help with the development of the mod. 

Playtesting the mod and finding bugs is a great way to help with the development of the mod, most people doesn't report bugs they find, so this helps a lot.

You also can help with the guide book, by just proposing new pages or interesting things to add, or even if you feel generous writing them.

We also accept translations, you can make a pull request with a new lang file or with changes to an existing lang file.

We also accept new ideas or critics about the way the mod works, any feedback is welcome, just try not to give too many ideas, the development is kinda slow so the TODO list may grow faster than the mod's content.

Finally, if you have any question feel free to ask at our [discord](https://discord.gg/EhYbA97).

### Compiling the mod
This is a small tutorial for non-modders to get the last version of the mod from github, so you can play with alpha versions of the mod.

Prerequisites:
- Have Java JDK installed, JRE it's not enough, you can get it [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

Steps:
1. Download the project 

  ![alt text](https://image.prntscr.com/image/5o1YEu0VTN_amH5VVyxOjA.png "Download location github")
  
2. Extract the content of the zip file

  ![alt text](https://image.prntscr.com/image/vQhBvUFGQ5O6q9ruodQ2eA.png "This should be the content of the folder")
  
3. Open a terminal/cmd in the extracted folder

4. Run the command `./gradlew setupDecompWorkspace`
  
5. Run the command `./gradlew build`

  ![alt text](https://image.prntscr.com/image/N3HGL8m_RCm9Bk1MDokgFw.png "Command")
  
6. Wait until the mod is compiled

  ![alt text](https://image.prntscr.com/image/C31LCugcTZenH-BYgnucQw.png "Command result")
  
7. Grab the mod jar from `build/libs/<modname or something>.jar`
8. Profit!?!

### Running the mod
To run the mod you will need it's dependencies:
- [Forgelin](https://minecraft.curseforge.com/projects/shadowfacts-forgelin?gameCategorySlug=mc-mods&projectID=248453): or you will get a `java.lang.NoClassDefFoundError: kotlin/TypeCastException`
- ModelLoader: you can found the last version in /libs/modelloader-1.x.x.jar or compile from [here](https://github.com/Magneticraft-Team/ModelLoader).
