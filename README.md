# Scott's Essential (SCE)

A **highly configurable** Minecraft mod which adds many server friendly commands, performance improvements, and other useful things.

Feel free to report any bug or enhancement!

# Commands

**All commands have reasonable suggestions!**

**All commands can be customized! (You could configure /home to /h)**

**All list commands (e.g. /listhomes) are interactive! (you could click one in the list to teleport)**

- /scessential
  - /scessential
    - Usage: Print current mod version.
  - /scessential getRegistryName item
      - Usage: Get the registry name (minecraft:stone for eg) of your main hand held item. You could easily copy that name by clicking the message popped up.
      - Permission: Level 2 (Vanilla permission for temp use.)
  - /scessential getRegistryName mob
      - Usage: Get the registry names of living entities near you in certain radius (configurable).
      - Permission: Level 2 (Vanilla permission for temp use.)
      - <img src="https://i.loli.net/2021/01/13/ZmNRGOH14A6qanS.png" alt="image.png" style="zoom:67%;" />
- /spawn 
    - Usage: Teleport to the world spawn point.
- /back
    - Usage: Teleport to the position before last teleport.
- /tpa
    - /tpa [Target]
      - Usage: Send a teleport request to target to let you teleport to target.
      - ![image.png](https://i.loli.net/2021/01/14/G8Ryl6sK7Pox4qH.png)
    - /tpahere [Target]
      - Usage: Send a teleport request to target to let target teleport to you.
    - /tphere [Target]
      - Usage: Teleport the target to your position.
      - Permission: 2 Level
    - /tpallhere
      - Usage: Teleport all players to your position. 
    - /tpaaccept [RequestId]
      - Usage: Accept a tpa / tpahere request. (You don't need to type this command. You can just click "Accept" in the chat.)
    - /tpadeny
      - Usage: Deny a tpa / tpahere request. (You don't need to type this command. You can just click "Deny" in the chat.)
- /home
    - /home [HomeName]
      - Usage: Teleport you to one of your home. (You don't need to remember the names of your homes because the command will suggest each of your home.)
    - /sethome [HomeName]
      - Usage: Set a home at your position.
    - /delhome = /removehome [HomeName]
      - Usage: Delete a home.
    - /listhomes
      - Usage: List all of your homes. You can click one in the list to teleport to the clicked one.
      - ![image.png](https://i.loli.net/2021/01/13/z45M3e7t2BUGFVA.png)
    - /homeother [Target] [HomeName]
      - Usage: Teleport to other's home.
      - Permission: 2 Level
    - /delotherhome = /removeotherhome [Target] [HomeName]
      - Usage: Delete other's home.
      - Permission: 2 Level
    - /listotherhomes [Player]
      - Usage: List all homes of [Player]
      - Permission: 2 Level
- /warp
    - /warp [WarpName]
      - Usage: Teleport to a warp.
    - /setwarp [WarpName]
      - Usage: Set a warp.
      - Permission: 2 Level
    - /delwarp [WarpName]
      - Usage: Delete a warp.
      - Permission: 2 Level
    - /listwarps
      - Usage: List all warps.
- /rtp
    - Usage: Randomly teleport to a safe location in current world.
- /fly
    - /fly [Player]
      - Usage: Let a player fly permanently.
      - Permission: 2 Level
    - /fly [Player] [Minutes]
      - Usage: Let a player fly in [Minutes]
      - Permission: 2 Level
- /hat
    - /hat
        - Usage: Let the current held item jump onto your head.
    - /hat [Target]
        - Usage: Let the current held item jump onto Target's head.
        - Permission: 2 Level
- /invsee [Target]
    - Usage: Open a GUI containing Target's all items including armor and second hand.
    - Permission: 2 Level
    - <img src="https://i.loli.net/2021/01/14/aFYtjO9rHf4unTx.png" alt="image.png" style="zoom:90%;" />
- /trashcan
    - Usage: Open a GUI trashcan that you can put trash items in. You could clear all the items in the trashcan by clicking a button, and the trashcan will clear after some seconds (Configurable). 
    - <img src="https://i.loli.net/2021/01/13/yP8msuVo7LE6Jcj.png" alt="image.png" style="zoom: 67%;" />
- /rank
    - Usage: Open a GUI, displaying the ranking information of all players. (E.g Number of deaths)
    - <img src="https://i.loli.net/2021/01/14/3aDYzZy9GL6KCXH.png" alt="image.png" style="zoom:67%;" />
- More in progress
  
# Services

- **Entity Cleaner**
  - Entity Cleaner could clean the entities in all worlds with very high configurability. (Almost all kinds of entities) 
  - You could easily add whitelist of entities that shouldn't be cleaned up. 
  - You could get the registry name of items or other entities by using /scessential getRegistryName item / mob (OP required), and then add it to the whitelist.
  - For more information, check the server config file of my mod.
- **Information Recorder** (Disabled by default)
  - Information recorder can record the information of your server to help you find the "bad" one, including player name, position, time, and some extra information about that record.
  - Currently, my mod supports recording
    - Player Chats
    - Player use commands
    - Player login / logout
    - Player join dimension
    - Player death
    - Player kill other entities
    - Player open containers
    - Player place blocks (You could specify blocks need to be recorded in server config file.)
    - Periodically (A configurable interval) records player's information (name, position, time).
  - All functions have options to enable / disable.
  - All recoding actions are performed asynchronous.
  - Recordings are stored in your world folder/scessential/infoRecorder, separated by date and categories.
- **MOTD Customization** (Disabled by default)
  - MOTD Customization can help you to display different information of your server (server description) as every time the player hit "Refresh" button.
  - You can use '&' to specify the format of text of server description. (Like '&l' in Minecraft stands for **bold** font)
  - For more information, check the config of my mod.

# About

Author: ScottCTD (ScottCTD@outlook.com)

You could modify my mod for private use only.

I was inspired by FTB Essential and created it, but I did **not** copy code from FTB.