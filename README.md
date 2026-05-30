# EasyScript

EasyScript is a lightweight `.es` scripting plugin for Minecraft servers. It is inspired by Skript-style workflows, but it is designed as a smaller and stricter engine that pre-parses scripts into action objects on `/es reload`.

EasyScript does **not** claim 100% Skript compatibility. Skript is a very large language with years of addon APIs and thousands of expressions. EasyScript focuses on the most useful core gameplay, chat, command and event features, plus a few built-in features that normally require addon plugins.

## Compatibility

| Item | Support |
| --- | --- |
| Minecraft target | 1.20.6+ |
| Tested API target | Paper API `1.20.6-R0.1-SNAPSHOT` |
| Java | Java 17+ |
| Recommended server engine | Paper or Purpur |
| Also supported | Spigot 1.20.6+ should work, but Paper/Purpur are preferred |
| Not supported | Minecraft versions below 1.20.6 |

The plugin is built as a normal Bukkit/Paper plugin and uses `plugin.yml` with `api-version: 1.20`.

## Commands

- `/easyscript help`
- `/easyscript reload`
- `/easyscript list`
- `/easyscript info`
- `/easyscript debug`
- `/easyscript disable <file.es>`
- `/easyscript enable <file.es>`
- `/es` is the short alias for `/easyscript`

Players with `easyscript.cooldown.bypass` bypass EasyScript command cooldowns.

## Script Files

Put scripts here:

```text
plugins/EasyScript/scripts/*.es
```

EasyScript translates legacy colors such as `&8`, `&4`, `&a` and hex colors such as `&#ff55aa`.

Files starting with `-` are disabled and skipped on reload. For example:

```text
/es disable example.es
```

renames `example.es` to `-example.es`. To enable it again:

```text
/es enable example.es
```

## Skript-Style Coverage

EasyScript now includes a broad set of Skript-like core features:

- Custom commands with aliases, permissions, usage text, descriptions and cooldowns.
- Simple typed command arguments with `arguments: text, integer`.
- `options:` support with `{@option-name}` replacements.
- User functions with `function name(arg):` and `call name with "value"`.
- Runtime permission attachments for temporary player permissions.
- Server lifecycle triggers: `on load:` and `on unload:`.
- Player lifecycle events: join, quit, respawn and death.
- Chat events with editable message and format.
- Command preprocess events.
- Block events: break and place.
- Interaction events.
- Item events: drop and pickup.
- Damage events.
- Cancellable events with `cancel event`.
- Block syntax with `if/else`, delayed blocks, repeated blocks and loop blocks.
- Target blocks with `as player "name":`.
- Basic GUI inventories and GUI click events.
- Named runtime lists with add, remove, clear, loop and contains checks.
- Built-in string and math transforms for variables.
- Join, quit and death message editing.
- Player messages, broadcasts and console/player command execution.
- Inventory actions: give, remove, clear inventory and edit the held item.
- Inventory UI actions: open workbench, enchanting table, ender chest or player inventory.
- Player actions: heal, damage, feed, set food, saturation, exhaustion, set gamemode, teleport, kick, XP, level, flight, velocity, freeze ticks, air, glowing, gravity and fire ticks.
- Respawn-point actions for setting a player's forced bed spawn.
- World actions: set time, set weather, strike lightning, spawn entities, set blocks, drop items, create explosions, worldborder and save worlds.
- Visual/audio actions: title, subtitle, actionbar, sound, particles, potion effects, bossbars, sidebars and tab header/footer.
- Safe script file actions for creating folders, creating files, reading files, overwriting files and appending files.
- Persistent variables saved to `variables.yml`.
- One-line conditions for permissions, chat messages, variables, world, items, blocks, player states, random chance and comparisons.
- Delayed actions and repeated scheduled actions.
- Debug information through `/es debug`.
- Enable/disable script files without deleting them.
- A small addon API for registering custom actions and conditions from Java plugins.

## EasyScript Custom Features

These are built-in EasyScript conveniences that are intentionally simpler than full Skript syntax:

- `cooldown:` directly inside command definitions.
- `arguments:` command validation for `text`, `number`, `integer` and `player`.
- `options:` are replaced when the `.es` file is parsed, so common prefixes stay cheap at runtime.
- User functions are pre-parsed and called by name without reparsing text.
- `{var:name}` explicit variable placeholders to avoid conflicts with built-in placeholders.
- More built-in runtime placeholders for biome, weather, light, held item, inventory space, coordinates, yaw, pitch and player state.
- `after 5 seconds: action` for compact scheduled actions.
- `repeat 3 times every 1 second: action` as a compact custom scheduler.
- Multi-line blocks for readable scripts:
  `if condition:`, `else:`, `after 5 seconds:`, `repeat 3 times:` and `loop all players:`.
- `as player "name":` for running a block against another online player.
- `loop list "name":` and list placeholders such as `{list:name}` and `{list-size:name}`.
- Built-in GUI helpers with `open gui ...` and `set gui slot ...`.
- `/es disable` renames scripts to `-file.es`, and `/es enable` restores them.
- Built-in bossbar/actionbar/title/sound/particle/effect actions without separate addons.
- Built-in sidebar, tablist, display-name and held-item editing actions without separate scoreboard or item addon plugins.
- Strict parser errors that point to `file.es:line`.
- In-memory variable cache with batched autosaves for lower disk usage.
- Script file I/O is sandboxed to `plugins/EasyScript/files`, so `.es` scripts cannot write outside the plugin's file workspace.

## Supported Triggers

```text
on load:
on unload:
on join:
on quit:
on leave:
on chat:
on command:
on death:
on respawn:
on block break:
on break:
on block place:
on place:
on interact:
on right click:
on drop:
on pickup:
on pick up:
on damage:
on inventory click:
on gui click:
on item consume:
on consume:
on projectile hit:
on entity death:
on player move:
on move:
on teleport:
on login:
on server ping:

command /name:
```

## Command Options

```text
aliases: /alias1, /alias2
permission: example.permission
permission message: "&cNo permission."
usage: /example
description: Example command
arguments: text, integer
cooldown: 5 seconds
cooldown message: "&cWait &f{remaining}&c."
```

## Options And Functions

```text
options:
    prefix: &8[&aEasyScript&8]
    no-permission: &cNo permission.

function welcome(name):
    send "{@prefix} &7Welcome, &a{name}&7!" to player

on join:
    call welcome with "{player}"
```

Function arguments are exposed as `{function-arg-1}`, `{function-arg-2}` and `{function-args}`. Named parameters are also available as placeholders using their parameter names, for example `{name}`.

## Supported Actions

### Messaging And Commands

```text
send "&aText" to player
send "&aText" to sender
send "&aText" to console
send "&cStaff alert" to players with permission "server.staff"
send nearby "&7Local message" radius 20 self true
broadcast "&aText"
log "Loaded {online} online players"
console "say Hello"
execute console "say Hello"
execute player "spawn"
cancel event
call welcome with "{player}"
```

### Chat And Event Messages

```text
set format to "&8[&4ADMIN&8] &f{player}&8: &7{message}"
set chat format to "&8[&4ADMIN&8] &f{player}&8: &7{message}"
set message to "&a{message}"
set join message to "&8[&a+&8] &a{player}"
set quit message to "&8[&c-&8] &c{player}"
set death message to "&8[&cDeath&8] &7{player} died."
```

### Visuals, Sounds And Effects

```text
actionbar "&eText" to player
actionbar "&eText" to all
clear chat of player
clear chat of all
title "&aTitle" subtitle "&7Subtitle" to player for 3 seconds
sound "ENTITY_PLAYER_LEVELUP" to player volume 1 pitch 1
stop sound "ENTITY_PLAYER_LEVELUP" for player
particle "HAPPY_VILLAGER" at player count 12
effect "SPEED" to player for 10 seconds amplifier 1
bossbar "&aBoost active" to player color GREEN progress 1 for 10 seconds
```

### Player UI And Scoreboards

```text
sidebar title "&aStats" lines "&7Player: &f{player}|&7World: &f{world}|&7Online: &f{online}" to player
clear sidebar of player
tab header "&aEasyScript" footer "&7World: &f{world}" to player
set display name to "&a{player}"
set tab name to "&a{player}"
reset display name
reset tab name
```

### Inventory And Player

```text
give "DIAMOND" amount 3 to player
remove "STONE" amount 8 from player
clear inventory of player
open enderchest to player
open workbench to player
open enchanting to player
open inventory to player
open gui "&8Shop" rows 3 to player
set gui slot 13 to "DIAMOND" named "&bVIP" lore "&7Click to buy|&7Price: 10"
close inventory of player
set held slot to 1
set held item amount to 32
rename held item to "&bEasy Blade"
set lore of held item to "&7Created by EasyScript|&7Owner: {player}"
enchant held item with "sharpness" level 5
repair held item
set held item unbreakable to true
set custom model data to 1001
clear custom model data of held item
add item flag "HIDE_ATTRIBUTES" to held item
remove item flag "HIDE_ATTRIBUTES" from held item
clear item flags of held item
set helmet to "DIAMOND_HELMET"
set chestplate to "DIAMOND_CHESTPLATE"
set leggings to "DIAMOND_LEGGINGS"
set boots to "DIAMOND_BOOTS"
set offhand to "SHIELD"
clear armor of player
heal player
set health to 20
set max health to 40
heal player by 4
damage player by 2
feed player
set food to 20
set saturation to 10
set exhaustion to 0
add xp 25 to player
set level to 10
set xp progress to 0.5
set flight to true
set flying to true
set walk speed to 0.25
set fly speed to 0.15
reset walk speed
reset fly speed
set fire ticks to 100
extinguish player
set freeze ticks to 80
clear freeze ticks
set remaining air to 300
set arrows in body to 0
set glowing to true
set invulnerable to false
set silent to false
set gravity to true
set visual fire to false
swing main hand
swing off hand
save location to variable "last-location-{player}"
save exact location to variable "last-exact-location-{player}"
send resource pack "https://example.com/pack.zip" to player
clear potion effects of player
set op to false
set whitelist to false
give permission "example.temp" to player for 30 seconds
deny permission "example.blocked" to player
remove permission "example.temp" from player
gamemode "creative" to player
teleport player to spawn
teleport player to "world,0,80,0"
set compass target to spawn
set player time to night
reset player time
set player weather to rain
reset player weather
set velocity to "0,1,0"
push player by "0,0.5,0"
launch "arrow" from player
kick player "&cYou were kicked."
kick all players "&cServer restarting."
set respawn point to player
set respawn point to spawn
set respawn point to "world,0,80,0"
clear respawn point
```

### World And Entities

```text
set time to day
set time to night
set time to 13000
set weather to clear
set weather to rain
set weather to thunder
lightning at player
lightning effect at player
spawn "ZOMBIE" at player
drop "DIAMOND" amount 1 at player
set block at player to "GOLD_BLOCK"
set block at "world,0,80,0" to "STONE"
break block at player
break block naturally at "world,0,80,0"
explosion at player power 2 fire false break false
set worldborder size to 500
set worldborder center to player size 250
save world
save world seed to variable "world-seed"
```

### Variables And Scheduling

```text
set variable "last-player" to "{player}"
add "5" to variable "coins-{player}"
remove "2" from variable "coins-{player}"
subtract "1" from variable "coins-{player}"
delete variable "last-player"
set variable "name-lower" to lowercase "{player}"
set variable "name-upper" to uppercase "{player}"
set variable "trimmed" to trimmed "  text  "
set variable "rounded" to rounded "3.6"
set variable "short-uuid" to substring "{uuid}" from 1 to 8
set variable "patched" to replace "hello world" target "world" with "server"
add "{player}" to list "visitors"
remove "{player}" from list "visitors"
clear list "visitors"
after 5 seconds: send "&aDelayed text" to player
repeat 3 times every 1 second: actionbar "&aPulse" to player
```

### Blocks, Loops And Targets

```text
if player has permission "example.admin":
    send "&aAllowed" to player
else:
    send "&cDenied" to player

after 5 seconds:
    send "&aDelayed block" to player
    sound "ENTITY_PLAYER_LEVELUP" to player volume 1 pitch 1

repeat 3 times every 1 second:
    actionbar "&aPulse &f{loop-number}" to player

loop all players:
    send "&7Looped player: &f{loop-player}" to sender

loop list "visitors":
    send "&8#&f{loop-number} &7{loop-value}" to sender

as player "{arg-1}":
    send "&aYou were targeted by &f{sender}&a." to player
    give "EMERALD" amount 1 to player
```

### Safe Files And Folders

All file and folder paths are relative to:

```text
plugins/EasyScript/files
```

`../` traversal and absolute paths are blocked.

```text
create folder "logs"
create file "logs/latest.txt"
write file "logs/latest.txt" to "First line\n"
overwrite file "logs/latest.txt" to "Replacement text\n"
append file "logs/latest.txt" with "Another line\n"
read file "logs/latest.txt" to variable "latest-log"
send file "logs/latest.txt" to player
send file "logs/latest.txt" to sender
send file "logs/latest.txt" to console
list folder "logs" to variable "log-files"
delete file "logs/latest.txt"
delete folder "logs"
```

## Supported Conditions

```text
if not player is op: send "&cNot operator" to player
if player is op: send "&cOperator" to player
if player is sneaking: send "&7Sneaking" to player
if player is sprinting: send "&7Sprinting" to player
if player is flying: send "&7Flying" to player
if player can fly: send "&7Flight allowed" to player
if player is gliding: send "&7Gliding" to player
if player is swimming: send "&7Swimming" to player
if player is blocking: send "&7Blocking" to player
if player is whitelisted: send "&aWhitelisted" to player
if whitelist is enabled: send "&eWhitelist is on." to sender
if server is full: send "&cServer full." to sender
if server is empty: log "No players online"
if gamemode is "creative": send "&bCreative mode." to player
if player has effect "speed": send "&aSpeed active." to player
if player "{arg-1}" is online: send "&aThat player is online." to sender
if player has permission "example.admin": send "&aAllowed" to player
if sender has permission "example.admin": send "&aAllowed" to sender
if player is in world "world": send "&aMain world" to player
if player has item "DIAMOND" amount 1: send "&bShiny" to player
if inventory has space: send "&aYou can carry more." to player
if inventory is full: send "&cInventory full." to player
if inventory is empty: send "&7Empty inventory." to player
if held item is air: send "&cHold an item first." to player
if held item is "DIAMOND_SWORD": send "&bNice sword." to player
if held item name contains "Blade": send "&aNamed blade detected." to player
if held item has enchant "sharpness" level 3: send "&aSharp enough." to player
if block is "DIAMOND_ORE": broadcast "&bDiamond ore!"
if weather is clear: send "&eSunny." to player
if weather is rain: send "&9Rainy." to player
if time is day: send "&eDaytime." to player
if time is night: send "&8Nighttime." to player
if biome is "PLAINS": send "&aPlains biome." to player
if nearby players radius 10 >= 2: send "&aPeople nearby." to player
if player is within 5 of "world,0,80,0": send "&aClose to the point." to player
if message contains "discord": cancel event
if message starts with "!": set message to "&7{message}"
if message is "hello": send "&aHi!" to player
if variable "last-player" exists: send "&7Last: &f{var:last-player}" to player
if variable "last-player" is "{player}": send "&aYou were last." to player
if list "visitors" exists: send "&7Visitors: &f{list:visitors}" to sender
if list "visitors" is empty: send "&7No visitors yet." to sender
if list "visitors" contains "{player}": send "&aYou are on the visitor list." to player
if chance 25%: sound "ENTITY_EXPERIENCE_ORB_PICKUP" to player volume 1 pitch 1.5
if "{health}" <= "10": send "&cLow health!" to player
if "{online}" > "5": broadcast "&aServer is active."
if "{message}" contains "help": send "&eUse /help." to player
```

## Placeholders

```text
{player}
{display-name}
{sender}
{uuid}
{world}
{x}
{y}
{z}
{yaw}
{pitch}
{biome}
{light}
{health}
{max-health}
{food}
{saturation}
{exhaustion}
{level}
{xp-progress}
{gamemode}
{ping}
{ip}
{online}
{max-players}
{player-list}
{weather}
{time}
{world-time}
{inventory-free}
{held-item}
{held-amount}
{held-name}
{is-op}
{is-flying}
{is-sneaking}
{is-sprinting}
{is-whitelisted}
{is-glowing}
{is-invulnerable}
{is-silent}
{has-gravity}
{freeze-ticks}
{air}
{max-air}
{arrows-in-body}
{message}
{command}
{command-line}
{args}
{arg-1}, {arg-2}, ...
{function-arg-1}
{function-arg-2}
{function-args}
{remaining}
{var:name}
{list:name}
{list-size:name}
{loop-player}
{loop-value}
{loop-number}
{loop-index}
{random-1-100}

Event-specific placeholders:
{block}
{block-type}
{block-world}
{block-x}
{block-y}
{block-z}
{item}
{item-type}
{item-amount}
{action}
{damage}
{cause}
{damager}
{damager-type}
{death-message}
{slot}
{raw-slot}
{click}
{inventory-title}
{projectile}
{hit-entity}
{hit-entity-type}
{entity}
{entity-type}
{killer}
{teleport-cause}
{address}
{hostname}
{motd}
```

## Addon API

Other Java plugins can extend EasyScript without editing the parser directly:

```java
EasyScriptApi.registry().registerAction("myaddon", line -> {
    if (!line.startsWith("my action ")) {
        return Optional.empty();
    }
    return Optional.of(context -> {
        context.sender().sendMessage("Hello from an addon action");
    });
});
```

The registry also supports custom conditions. `/es debug` shows the number of registered addon actions and conditions.

## Configuration

```yaml
scripts-folder: scripts
script-files-folder: files
max-file-read-chars: 20000
save-example-script: true
max-visible-errors: 8
variables-autosave-minutes: 5
```

Variables are kept in memory while the server is running and saved in batches to avoid writing to disk on every script action.

## Build

This project uses Maven:

```powershell
mvn clean package
```

The plugin jar will be created in:

```text
target/EasyScript-1.0.0.jar
```

Copy that jar to your server `plugins` folder and restart the server.
