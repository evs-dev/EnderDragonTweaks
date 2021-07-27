<p align="center">
    <img alt="logo" src="https://raw.githubusercontent.com/evs-dev/EnderDragonTweaks/master/logo.png" width="150px" />
</p>

EnderDragonTweaks improves the defeating of the Ender Dragon, especially in multiplayer. All features can be enabled/disabled individually and are highly configurable.

### Features

- Shared XP drop
    - The same number of experience points is given to every player in the End when the Dragon is defeated
- Decorative XP orbs
    - XP orbs with no value are spawned above every player in the End to add excitement to the defeat
- Respawn Dragon Egg
    - The Dragon Egg respawns in a configurable place every time the Dragon is defeated
- Dragon death announcement
    - A customisable message is displayed in chat when the Dragon is defeated, including the killer's name and the contributors to the fight
- Custom commands
    - Run custom commands when the Dragon is defeated


### Config
```yaml
# EnderDragonTweaks - Ender Dragon tweaks, mainly for SMP servers
# by EvsDev (https://github.com/evs-dev)
# contributors: https://github.com/evs-dev/EnderDragonTweaks/graphs/contributors
# CONFIGURATION FILE

# Config version - DO NOT CHANGE
version: 0

# Is the plugin enabled? Should the Dragon death event be registered?
# DEFAULT: true
enabled: true

# The delay in ticks (20 ticks = 1 second) between the death of the Dragon and when this plugin triggers
# DEFAULT: 80
delay: 80

# |------------------------|
# |        Features        |
# |------------------------|

# Should every player in the End receive XP upon Dragon death?
enable-xp-drop: true

# Should every player in the End have decorative (valueless) XP orbs spawned above them upon Dragon death?
enable-decoration-orbs: true

# Should the Dragon egg respawn every time the Dragon is defeated?
enable-egg-respawn: true

# Should a message be broadcasted when the Dragon is defeated?
enable-defeat-announcement: true

# Should commands be run when the Dragon is defeated?
enable-commands: true

# |------------------------|
# |        Settings        |
# |------------------------|

# The radius (in blocks) from the centre of the End island where players will be counted as Dragon fight participants
# DEFAULT: 128
max-player-distance-from-end-centre: 128

# The delay in ticks (20 ticks = 1 second) before the Dragon can be respawned after having been defeated
# The cooldown is reset when the server is restarted
# DEFAULT: 0
dragon-respawn-cooldown: 0
# The message broadcasted when the Dragon respawn cooldown begins (if there is one)
# Colour/formatting codes are supported using the ampersand (&) symbol (https://minecraft.gamepedia.com/Formatting_codes#Color_codes)
# MACROS:
#   <time-remaining>: number of seconds before the cooldown ends
# DEFAULT: "The <time-remaining>-second Dragon respawn cooldown has started!"
enter-respawn-cooldown-announcement: "The <time-remaining>-second Dragon respawn cooldown has started!"
# The message broadcasted when the Dragon respawn cooldown ends
# Colour/formatting codes are supported using the ampersand (&) symbol (https://minecraft.gamepedia.com/Formatting_codes#Color_codes)
# MACROS:
#   <cooldown-length>: number of seconds the cooldown lasts; dragon-respawn-cooldown / 20
# DEFAULT: "The <cooldown-length>-second Dragon respawn cooldown has ended!"
leave-respawn-cooldown-announcement: "The <cooldown-length>-second Dragon respawn cooldown has ended!"
# The message sent to a player who tries to place an End Crystal to respawn the Dragon when the cooldown is active 
# Colour/formatting codes are supported using the ampersand (&) symbol (https://minecraft.gamepedia.com/Formatting_codes#Color_codes)
# MACROS:
#   <time-remaining>: number of seconds before the cooldown ends
# DEFAULT: "The Ender Dragon cannot be respawned at the moment because it's in cooldown. Cooldown time left: &r<time-remaining> seconds"
respawn-cooldown-warning: "&cThe Ender Dragon cannot be respawned at the moment because it's in cooldown. Cooldown time left: &r<time-remaining> seconds"

# The way xp-per-player should be interpreted.
# levels: The value is given in levels (e.g. 68 will add 68 to the XP number displayed to the player)
#         This is useful to add a consistent number of points
# points: The value is given in points, which changes based on the player's current XP
#         i.e. 12000 points will not always equal 68 levels 
# DEFAULT: levels
xp-mode: levels

# The amount of XP to give each Dragon fight participant upon Dragon death
# This is interpreted differently depending on xp-mode
# DEFAULT: 68
xp-per-player: 68

# The number of XP orbs spawned per player upon Dragon death (these are decorative only and have no XP value)
# If there are a large number of players in the End upon Dragon death, having this too high may cause a lag spike
# This will not be triggered if enable-xp-drop is disabled
# DEFAULT: 8
orb-count-per-player: 8

# The chance of the Dragon egg respawning upon Dragon defeat and when enable-egg-respawn is true
# RANGE: 0.0-1.0 (0.0 = 0%, 1.0 = 100%)
# DEFAULT: 1.0
egg-respawn-chance: 1.0

# The message broadcasted when the Dragon egg respawns (leave blank to have no message)
# # Colour/formatting codes are supported using the ampersand (&) symbol (https://minecraft.gamepedia.com/Formatting_codes#Color_codes)
# MACROS:
#   <position>: x, y, and z coordinates of the Egg (e.g. "x=0 y=68 z=0")
# DEFAULT: ""
egg-respawn-announcement: ""

# The coordinates of the Dragon egg when it spawns
# DEFAULT: 0, 0, 0 (this does not need to be configured by default since the portal coordinates are always (0, 0))
egg-position:
  x: 0
  y: 0
  z: 0
  # Should the y coordinate specified here dictate exactly where the Dragon egg should go?
  # If false, the plugin will try to find an air block above a bedrock block
  # DEFAULT: false
  override-y: false
  
# The message broadcasted when the Dragon is defeated
# Colour/formatting codes are supported using the ampersand (&) symbol (https://minecraft.gamepedia.com/Formatting_codes#Color_codes)
# !!! QUOTES ("") ARE REQUIRED !!!
# Macros are replaced when the message is sent
# MACROS:
#   <killer>: name of the player who killed the Dragon
#   <participants>: list of players within max-player-distance-from-end-centre upon Dragon defeat (EXCEPT the killer)
# DEFAULT: "&6<killer>&r just defeated the &5Ender Dragon&r!"
defeat-announcement-message-one-participant: "&6<killer>&r just defeated the &5Ender Dragon&r!"
# DEFAULT: "&6<killer>&r just defeated the &5Ender Dragon&r with help from <players-in-end>!"
defeat-announcement-message-multiple-participants: "&6<killer>&r just defeated the &5Ender Dragon&r with help from <participants>!"

# The commands to be run by the server when the Dragon is defeated
# Note that they will always send command feedback to the server console and all online ops
# !!! QUOTES ("") ARE REQUIRED !!!
# Macros are replaced when the message is sent
# MACROS:
#   <killer>: name of the player who killed the Dragon
#   <killer-display-name>: display name of the player who killed the Dragon (may be the same as <killer>, or e.g. the nickname of the player)
#   <participants-list>: list of players within max-player-distance-from-end-centre upon Dragon defeat (e.g. "p1, p2, & p3") (EXCEPT the killer)
#   <each-participant>: the command will be run individually for players within max-player-distance-from-end-centre upon Dragon defeat (EXCEPT the killer)
# DEFAULT: []
commands:
  #- "give <killer> minecraft:diamond 4"
  #- "give <each-participant> minecraft:iron_ingot 8"
  #- "say Congratulations <killer-display-name> and <participants-list>!"
  
# The text to replace <participants-list> with if there are no Dragon fight participants other than the killer
# E.g. (using the 3rd example command) "Congratulations dragonkiller495 and no-one else!"
# DEFAULT: "no-one else"
commands-no-participants-filler: "no-one else"
```

### Installation

1. Download the latest EnderDragonTweaks-x.x.x.jar
2. Place in into your server plugins folder
3. (Re)start your server!
