<p align="center">
    <img alt="logo" src="https://raw.githubusercontent.com/evs-dev/EnderDragonTweaks/master/logo.png" width="150px" />
</p>

EnderDragonTweaks improves the defeating of the Ender Dragon, especially in multiplayer. All features can be enabled/disabled individually and are highly configurable.

### Features

- Change Dragon health and damage to players
    - Enhance the Dragon by customising its health and the damage it outputs, providing a greater (or lesser) challenge for players
- Shared XP drop
    - Share experience levels/points equally between players or reward only the Dragon killer when the Dragon is defeated
- Decorative XP orbs
    - XP orbs with no value are spawned above every player in the End to add excitement to the defeat
- Respawn Dragon Egg
    - The Dragon Egg respawns in a configurable place every time the Dragon is defeated
- Dragon death announcement
    - A customisable message is displayed in chat when the Dragon is defeated, including the killer's name and the contributors to the fight
- Custom commands
    - Run custom commands when the Dragon is defeated
- Bossbar customisation
    - Change the title, colour, and style of the Dragon's bossbar to your liking
- Dragon respawn cooldown
    - Set a cooldown between the defeat of the Dragon and when it can next be respawned
- Dragon statistics
    - Various statistics about the Dragon (like its deaths and killers) are tracked and can be used in configured messages
- Respawn Dragon command
    - Respawn the Dragon manually using the command `/respawndragon` (permission: `enderdragontweaks.respawndragon`)
    - Heed the respawn cooldown (i.e. don't respawn if the cooldown is active) using `/respawndragon heedCooldown`
    - Override the cooldown (i.e. respawn even if the cooldown is active) using `/respawndragon overrideCooldown`


### Config
<details>
<summary>Click to Open</summary>

```yaml
# EnderDragonTweaks - Ender Dragon tweaks, mainly for SMP servers
# by EvsDev (https://github.com/evs-dev)
# contributors: https://github.com/evs-dev/EnderDragonTweaks/graphs/contributors
# CONFIGURATION FILE
# ❗ IMPORTANT INFO: ❗
# - All text supports using the ampersand (&) symbol for colours and formats
#   (https://minecraft.wiki/w/Formatting_codes#Color_codes)
# - Placeholders can be included in strings and are replaced by the plugin
# - Every string that has placeholders has a comment above explaining them
# - You can use any statistic from statistics.yml as a placeholder, e.g. <stat-dragonDeathCount>
# - Every string requires quotes ("example string") unless the default does not have them
# - 20 ticks = 1 second
# - If you need to update your config, use https://evs-dev.github.io/edt-config-updater
# - This plugin uses bStats. To opt out of its anonymous stats collection,
#   find the bStats folder in your plugins folder and set enabled to false in its config
#   (https://bstats.org/plugin/bukkit/EnderDragonTweaks/12284)

# Config version - DO NOT CHANGE (is set by plugin)
version: 0

# Is the plugin enabled?
# DEFAULT: true
enabled: true

# Should the plugin check for updates and report to the console?
# DEFAULT: true
check-for-updates: true

# The delay in ticks between the death of the Dragon and when this plugin triggers
# DEFAULT: 80
delay: 80

# The radius (in blocks) from the centre of the End island where players will be counted as Dragon fight participants
# DEFAULT: 128
max-player-distance-from-end-centre: 128

# |------------------------|
# |        Features        |
# |------------------------|

dragon-enhancements:
  enabled: false

  health:
    # The amount of health points a newly-spawned Dragon has
    # DEFAULT: 200
    amount: 200

    # The way the health amount should be determined
    # fixed:       The health is always set to the amount specified (mode-value is ignored)
    # random:      The health is randomly chosen between mode-value and the given amount
    # progressive: The health amount increases by mode-value every time a new Dragon is spawned
    #              Statistics must be enabled for progressive mode to work
    # DEFAULT: fixed
    mode: fixed
    mode-value: 0

  damage:
    # The multiplier for the damage the Dragon's normal attacks deal to players
    # DEFAULT: 1.0
    hit-multiplier: 1.0

    # The multiplier for the damage the Dragon's breath deals to players
    # DEFAULT: 1.0
    breath-multiplier: 1.0

xp-drop:
  enabled: true

  # The amount of XP to give each Dragon fight participant upon Dragon death
  # DEFAULT: 68
  amount: 68

  # The way the XP amount should be interpreted
  # levels: The value is given in levels (e.g. 68 will add 68 to the XP number displayed to the player)
  #         This is useful to add a consistent number of points
  # points: The value is given in points, which changes based on the player's current XP
  #         i.e. 12000 points will not always equal 68 levels
  # DEFAULT: levels
  interpretation: levels

  # The way the XP amount should be distributed
  # equal:        Each player gets the full amount
  #               e.g. when amount is 68, 68 is given to each player in the End upon Dragon death
  # split:        The value is divided by the number of Dragon fight participants, and each player gets the result
  #               e.g. when amount is 68, 34 is given to each player if there are 2 players in the End upon Dragon death
  # killer-bias:  The Dragon killer gets double the full amount, while the others just get the full amount
  #               e.g. when amount is 68, 136 is given to the killer and 68 is given to every other player in the End upon Dragon death
  # killer-only:  Only the Dragon killer gets XP
  #               e.g. when amount is 68, 68 is given to the killer and no-one else gets XP
  # DEFAULT: equal
  distribution: equal

decoration-orbs:
  enabled: true

  # The number of XP orbs spawned per player upon Dragon death (these are decorative only and have no XP value)
  # If there are a large number of players in the End upon Dragon death, having this too high may cause a lag spike
  # This will not be triggered if enable-xp-drop is disabled
  # DEFAULT: 8
  orb-count-per-player: 8

egg-respawn:
  enabled: true

  # The chance of the Dragon egg respawning upon Dragon defeat and when enable-egg-respawn is true
  # RANGE: 0.0-1.0 (0.0 = 0%, 1.0 = 100%)
  # DEFAULT: 1.0
  chance: 1.0

  # The message broadcasted when the Dragon egg respawns (leave blank to have no message)
  # PLACEHOLDERS:
  #   <position>: x, y, and z coordinates of the Egg (e.g. "x=0 y=68 z=0")
  # DEFAULT: ""
  announcement: ""

  # The coordinates of the Dragon egg when it spawns
  # DEFAULT: 0, 0, 0 (this does not need to be configured by default since the portal coordinates are always (0, 0))
  position:
    x: 0
    y: 0
    z: 0
    # Should the y coordinate specified here dictate exactly where the Dragon egg should go?
    # If false, the plugin will try to find an air block above a bedrock block
    # DEFAULT: false
    override-y: false

defeat-announcement:
  enabled: true

  # The message broadcasted when the Dragon is defeated
  # PLACEHOLDERS:
  #   <killer>: name of the player who killed the Dragon
  #   <participants>: list of players within max-player-distance-from-end-centre upon Dragon defeat (EXCEPT the killer)
  # DEFAULT: "&6<killer>&r just defeated the &5Ender Dragon&r!"
  one-participant: "&6<killer>&r just defeated the &5Ender Dragon&r!"

  # DEFAULT: "&6<killer>&r just defeated the &5Ender Dragon&r with help from <participants>!"
  multiple-participants: "&6<killer>&r just defeated the &5Ender Dragon&r with help from <participants>!"

custom-commands:
  enabled: false

  # The commands to be run by the server when the Dragon is defeated
  # Note that they will always send command feedback to the server console and all online ops
  # PLACEHOLDERS:
  #   <killer>: name of the player who killed the Dragon
  #   <killer-display-name>: display name of the player who killed the Dragon (may be the same as <killer>, or e.g. the nickname of the player)
  #   <participants-list>: list of players within max-player-distance-from-end-centre upon Dragon defeat (e.g. "p1, p2, & p3") (EXCEPT the killer)
  #   <each-participant>: the command will be run individually for players within max-player-distance-from-end-centre upon Dragon defeat (EXCEPT the killer)
  # DEFAULT: [a list of commands]
  commands:
    - "give <killer> minecraft:diamond 4"
    - "give <each-participant> minecraft:iron_ingot 8"
    - "say Congratulations <killer-display-name> and <participants-list>!"
    - "say The killer <killer-display-name> has now killed the Dragon <stat-dragonKillers.<killer>> times!"

  # The text to replace <participants-list> with if there are no Dragon fight participants other than the killer
  # E.g. (using the 3rd example command) "Congratulations dragonkiller495 and no-one else!"
  # DEFAULT: "no-one else"
  no-participants-filler: "no-one else"

# Customisation of the Dragon's bossbar
bossbar-customisation:
  enabled: false

  # List of possible names to be displayed above the bossbar (leave empty for "Ender Dragon")
  # DEFAULT: [a list of names]
  names:
    #- "Bertha"

  # The colour of the bossbar
  # COLOURS: blue, green, pink, purple, red, white, yellow
  # DEFAULT: pink
  colour: pink

  # The style of the bossbar
  # STYLES: progress, notched_6, notched_10, notched_12, notched_20
  # DEFAULT: progress
  style: progress

dragon-respawn-cooldown:
  enabled: false

  # The delay in ticks before the Dragon can be respawned after having been defeated
  # The cooldown is reset when the server is restarted
  # DEFAULT: 6000 (5 minutes)
  cooldown: 6000

  # The message broadcasted when the Dragon respawn cooldown begins (if there is one)
  # PLACEHOLDERS:
  #   <time-remaining>: amount of time left in the cooldown in hh:mm:ss format
  # DEFAULT: "The Dragon respawn cooldown has started! <time-remaining> is left."
  enter-announcement: "The Dragon respawn cooldown has started! <time-remaining> is left."

  # The message broadcasted when the Dragon respawn cooldown ends
  # PLACEHOLDERS:
  #   <cooldown-length>: total length of the cooldown in hh:mm:ss format; dragon-respawn-cooldown / 20
  # DEFAULT: "The <cooldown-length> Dragon respawn cooldown has ended!"
  leave-announcement: "The <cooldown-length> Dragon respawn cooldown has ended!"

  # The message sent to a player who tries to place an End Crystal to respawn the Dragon when the cooldown is active
  # PLACEHOLDERS:
  #   <time-remaining>: amount of time left in the cooldown in hh:mm:ss format
  # DEFAULT: "&cThe Ender Dragon cannot be respawned at the moment because it's in cooldown. &r<time-remaining> &cis left."
  warning: "&cThe Ender Dragon cannot be respawned at the moment because it's in cooldown. &r<time-remaining> &cis left."

statistics:
  enabled: true
```

</details>

### Installation

1. Download the latest EnderDragonTweaks-x.x.x.jar
2. Place in into your server plugins folder
   - If updating EnderDragonTweaks, you probably need to update your config.yml using https://evs-dev.github.io/edt-config-updater
3. (Re)start your server!
