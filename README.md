<center>
<img style="width: 150px" alt="logo" src="https://raw.githubusercontent.com/evs-dev/EnderDragonTweaks/master/logo.png" />
</center>

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


### Config

```yaml
# EnderDragonTweaks - Ender Dragon tweaks, mainly for SMP servers
# by EvsDev (https://github.com/evs-dev/EnderDragonTweaks)
# CONFIGURATION FILE

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

# |------------------------|
# |        Settings        |
# |------------------------|

# The number of experience points to give each player in the End upon Dragon death
# DEFAULT: 12000 (68 levels)
xp-points-per-player: 12000

# The number of XP orbs spawned per player upon Dragon death (these are decorative only and have no XP value)
# If there are a large number of players in the End upon Dragon death, having this too high may cause a lag spike
# This will not be triggered if enable-xp-drop is disabled
# DEFAULT: 8
orb-count-per-player: 8

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
#   <players-in-end>: list of players in the End upon Dragon defeat
# DEFAULT: <player> just defeated the Ender Dragon!
defeat-announcement-message: "&6<killer>&r just defeated the &5Ender Dragon&r with help from <players-in-end>!"
```

### Installation

1. Download the latest EnderDragonTweaks-x.x.x.jar
2. Place in into your server plugins folder
3. (Re)start your server!