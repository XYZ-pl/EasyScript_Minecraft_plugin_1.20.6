# EasyScript example.
# This file is copied to plugins/EasyScript/scripts/example.es on the first start.

options:
    prefix: &8[&aEasyScript&8]
    no-permission: &cYou do not have permission to use this command.

on load:
    create folder "logs"
    append file "logs/server.log" with "EasyScript loaded\n"

on unload:
    append file "logs/server.log" with "EasyScript unloaded\n"

function welcome-line(name):
    send "{@prefix} &7Welcome back, &a{name}&7!" to player

on join:
    set variable "last-join" to "{player}"
    set join message to "&8[&a+&8] &a{player} &7joined &8(&f{online}&7 online&8)"
    call welcome-line with "{player}"
    title "&aWelcome" subtitle "&7Online: &f{online}" to player for 3 seconds
    actionbar "&7Location: &f{x}&8, &f{y}&8, &f{z}" to player
    sound "ENTITY_PLAYER_LEVELUP" to player volume 0.8 pitch 1.2
    particle "HAPPY_VILLAGER" at player count 12

on quit:
    set quit message to "&8[&c-&8] &c{player} &7left the server."
    broadcast "&8[&c-&8] &7{player} left the server."

on chat:
    set format to "&8[&4ADMIN&8] &f{player}&8: &7{message}"
    if message contains "discord": cancel event
    if message contains "discord": send "&cPlease do not advertise here." to player

command /ping:
    aliases: /pong
    permission: easyscript.ping
    permission message: "{@no-permission}"
    cooldown: 5 seconds
    cooldown message: "&cWait &f{remaining}&c before using /ping again."
    trigger:
        send "&aPong, &f{player}&a!" to sender
        after 2 seconds: actionbar "&7Last join saved as: &f{var:last-join}" to player

command /boost:
    permission: easyscript.boost
    trigger:
        effect "SPEED" to player for 10 seconds amplifier 1
        if chance 25%: sound "ENTITY_EXPERIENCE_ORB_PICKUP" to player volume 1 pitch 1.5
        bossbar "&aBoost active" to player color GREEN progress 1 for 10 seconds

command /uidemo:
    permission: easyscript.ui
    trigger:
        sidebar title "{@prefix}" lines "&7Player: &f{player}|&7World: &f{world}|&7Online: &f{online}|&7Coins: &f{var:coins-{player}}" to player
        tab header "{@prefix}" footer "&7World: &f{world} &8| &7Online: &f{online}" to player
        set tab name to "&a{player}"
        set display name to "&a{player}"
        actionbar "&aUI refreshed for &f{player}" to all
        add xp 10 to player
        set flight to true
        set fly speed to 0.12
        if player can fly: send "{@prefix} &7Flight is allowed for you now." to sender
        extinguish player
        send "{@prefix} &aUI demo enabled." to sender

command /playerplus:
    permission: easyscript.playerplus
    trigger:
        set health to 20
        set max health to 24
        set food to 20
        set saturation to 10
        set exhaustion to 0
        set xp progress to 0.5
        set compass target to spawn
        set player time to night
        set player weather to clear
        clear potion effects of player
        give permission "easyscript.demo.temp" to player for 30 seconds
        set freeze ticks to 0
        set remaining air to 300
        set arrows in body to 0
        set glowing to true
        after 5 seconds: set glowing to false
        reset walk speed
        reset fly speed
        swing main hand
        save exact location to variable "last-location-{player}"
        send "{@prefix} &aPlayer tools applied. Biome: &f{biome}&a, light: &f{light}&a, air: &f{air}" to sender

command /kit:
    cooldown: 30 seconds
    trigger:
        give "DIAMOND_SWORD" amount 1 to player
        give "COOKED_BEEF" amount 16 to player
        set respawn point to player
        append file "logs/kits.log" with "{player} claimed /kit at {world} {x} {y} {z}\n"
        repeat 3 times every 1 second: actionbar "&aKit delivered to &f{player}" to player

command /itemdemo:
    permission: easyscript.items
    trigger:
        if held item is air: give "DIAMOND_SWORD" amount 1 to player
        set held slot to 1
        rename held item to "&bEasy Blade"
        set lore of held item to "&7Created by EasyScript|&7Owner: {player}|&7World: {world}"
        enchant held item with "sharpness" level 1
        set held item unbreakable to true
        set custom model data to 1001
        add item flag "HIDE_ATTRIBUTES" to held item
        repair held item
        send "{@prefix} &aEdited &f{held-item} &ain your main hand." to sender

command /inventorydemo:
    permission: easyscript.inventory
    trigger:
        if inventory has space: drop "EMERALD" amount 1 at player
        if inventory is full: send "{@prefix} &cYour inventory is full." to sender
        open enderchest to player
        after 5 seconds: close inventory of player

command /worlddemo:
    permission: easyscript.world
    trigger:
        set block at player to "GOLD_BLOCK"
        drop "DIAMOND" amount 1 at player
        explosion at player power 0 fire false break false
        set player time to day
        save world seed to variable "seed-{world}"
        if weather is clear: send "{@prefix} &eWeather is clear in &f{world}&e." to sender
        if time is day: send "{@prefix} &eIt is daytime." to sender
        send "{@prefix} &7World seed saved as &f{var:seed-{world}}" to sender
        save world

command /staffping:
    permission: easyscript.staffping
    trigger:
        send "&8[&cStaff&8] &f{player}&7 needs help at &f{x}&8, &f{y}&8, &f{z}" to players with permission "easyscript.staff"
        send nearby "&8[&eLocal&8] &7{player} used staff ping nearby." radius 20 self false
        log "Staff ping by {player} in {world} at {x},{y},{z}"
        if nearby players radius 15 >= 1: send "{@prefix} &aThere is another player nearby." to sender
        if whitelist is enabled: send "{@prefix} &eWhitelist is currently enabled." to sender

command /checkonline:
    arguments: text
    permission: easyscript.checkonline
    usage: /checkonline <player>
    trigger:
        if player "{arg-1}" is online: send "{@prefix} &a{arg-1} is online." to sender
        if not player "{arg-1}" is online: send "{@prefix} &c{arg-1} is offline." to sender
        if gamemode is "survival": send "{@prefix} &7You are currently in survival." to sender

command /filedemo:
    permission: easyscript.files
    trigger:
        create folder "demo"
        write file "demo/info.txt" to "&aSaved by {player}\nWorld: {world}\n"
        append file "demo/info.txt" with "Online: {online}\n"
        read file "demo/info.txt" to variable "demo-info"
        send "&7File content loaded: &f{var:demo-info}" to sender
        list folder "demo" to variable "demo-files"
        send "&7Files: &f{var:demo-files}" to sender

command /blockdemo:
    permission: easyscript.blockdemo
    trigger:
        if player has permission "easyscript.blockdemo":
            send "{@prefix} &aMulti-line if/else works." to sender
            repeat 3 times every 1 second:
                actionbar "&aPulse &f{loop-number}" to player
        else:
            send "{@no-permission}" to sender

command /targetdemo:
    arguments: text
    permission: easyscript.target
    usage: /targetdemo <player>
    trigger:
        as player "{arg-1}":
            send "{@prefix} &aYou were targeted by &f{sender}&a." to player
            give "EMERALD" amount 1 to player

command /listdemo:
    permission: easyscript.list
    trigger:
        add "{player}" to list "visitors"
        set variable "player-lower" to lowercase "{player}"
        set variable "short-uuid" to substring "{uuid}" from 1 to 8
        send "{@prefix} &7Visitors: &f{list:visitors}" to sender
        if list "visitors" contains "{player}":
            send "{@prefix} &aYou are on the visitor list as &f{var:player-lower}&a." to sender
        loop list "visitors":
            send "&8#&f{loop-number} &7{loop-value}" to sender

command /guidemo:
    permission: easyscript.gui
    trigger:
        open gui "&8EasyScript Shop" rows 3 to player
        set gui slot 13 to "DIAMOND" named "&bVIP" lore "&7Click example|&7Slot: 13"

command /addcoins:
    arguments: text, integer
    permission: easyscript.coins
    usage: /addcoins <player> <amount>
    trigger:
        add "{arg-2}" to variable "coins-{arg-1}"
        send "{@prefix} &a{arg-1} now has &f{var:coins-{arg-1}} &acoins." to sender

on block break:
    if block is "DIAMOND_ORE": broadcast "&b{player} found diamond ore at &f{block-x}&8, &f{block-y}&8, &f{block-z}&b!"

on death:
    set death message to "&8[&cDeath&8] &7{player} died. Cause: &f{cause}"

on gui click:
    if "{inventory-title}" = "&8EasyScript Shop": cancel event
    if "{inventory-title}" = "&8EasyScript Shop": send "{@prefix} &aClicked slot &f{slot}&a with &f{click}&a." to player

on item consume:
    send "{@prefix} &7Consumed &f{item}&7." to player

on projectile hit:
    send "{@prefix} &7Projectile hit: &f{projectile}&7." to player

on teleport:
    send "{@prefix} &7Teleport cause: &f{teleport-cause}&7." to player
