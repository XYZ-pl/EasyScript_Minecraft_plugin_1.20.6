package pl.macie.easyscript.script;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import pl.macie.easyscript.EasyScriptPlugin;
import pl.macie.easyscript.command.DynamicCommandRegistry;
import pl.macie.easyscript.script.action.Action;
import pl.macie.easyscript.script.model.RuntimeScripts;
import pl.macie.easyscript.script.model.ScriptCommand;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.script.model.ScriptEvent;
import pl.macie.easyscript.script.model.ScriptFunction;
import pl.macie.easyscript.script.model.TriggerType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ScriptManager implements Listener {
    private static final Set<String> RESERVED_COMMANDS = Set.of("easyscript", "es");

    private final EasyScriptPlugin plugin;
    private final DynamicCommandRegistry dynamicCommandRegistry;
    private final ScriptParser parser = new ScriptParser();

    private volatile RuntimeScripts runtimeScripts = RuntimeScripts.empty();
    private volatile ScriptLoadResult lastResult = ScriptLoadResult.empty();

    public ScriptManager(EasyScriptPlugin plugin, DynamicCommandRegistry dynamicCommandRegistry) {
        this.plugin = plugin;
        this.dynamicCommandRegistry = dynamicCommandRegistry;
    }

    public ScriptLoadResult reloadScripts() {
        long started = System.currentTimeMillis();
        File folder = plugin.getScriptsFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            String error = "Could not create scripts folder: " + folder.getAbsolutePath();
            ScriptLoadResult result = ScriptLoadResult.failure(List.of(error));
            lastResult = result;
            return result;
        }

        List<String> loadedFiles = new ArrayList<>();
        List<String> disabledFiles = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<ScriptCommand> commands = new ArrayList<>();
        Map<String, ScriptFunction> functions = new java.util.HashMap<>();
        Map<TriggerType, List<ScriptEvent>> events = emptyEventMap();

        File[] files = folder.listFiles((directory, name) -> name.toLowerCase(Locale.ROOT).endsWith(".es"));
        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
            for (File file : files) {
                if (file.getName().startsWith("-")) {
                    disabledFiles.add(file.getName());
                    continue;
                }
                try {
                    ParsedScript parsedScript = parser.parse(file);
                    loadedFiles.add(file.getName());
                    errors.addAll(parsedScript.getErrors());
                    commands.addAll(parsedScript.getCommands());
                    for (ScriptFunction function : parsedScript.getFunctions()) {
                        String name = function.getName().toLowerCase(Locale.ROOT);
                        if (functions.putIfAbsent(name, function) != null) {
                            errors.add(function.getSource().display() + ": duplicate function '" + function.getName() + "'");
                        }
                    }
                    for (ScriptEvent event : parsedScript.getEvents()) {
                        events.get(event.getTriggerType()).add(event);
                    }
                } catch (IOException exception) {
                    errors.add(file.getName() + ": could not read file: " + exception.getMessage());
                }
            }
        }

        List<ScriptCommand> validCommands = validateCommands(commands, errors);
        RuntimeScripts newRuntime = new RuntimeScripts(validCommands, events, functions);
        runtimeScripts = newRuntime;
        dynamicCommandRegistry.registerAll(validCommands, this);
        runLoadEvents();

        ScriptLoadResult result = new ScriptLoadResult(
                loadedFiles,
                disabledFiles,
                validCommands.size(),
                newRuntime.countEvents(),
                functions.size(),
                countActions(validCommands, events, functions),
                System.currentTimeMillis() - started,
                errors
        );
        lastResult = result;
        return result;
    }

    public void executeActions(List<Action> actions, ScriptContext context) {
        if (!context.isAsynchronous()) {
            for (Action action : actions) {
                executeSafely(action, context);
            }
            return;
        }

        List<Action> syncActions = new ArrayList<>();
        for (Action action : actions) {
            if (action.canRunAsynchronously()) {
                executeSafely(action, context);
            } else {
                syncActions.add(action);
            }
        }

        if (!syncActions.isEmpty()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (Action action : syncActions) {
                    executeSafely(action, context.asSynchronous());
                }
            });
        }
    }

    public ScriptLoadResult getLastResult() {
        return lastResult;
    }

    public List<String> getLoadedCommandNames() {
        return runtimeScripts.getCommands().stream()
                .map(command -> "/" + command.getName())
                .toList();
    }

    public List<String> getScriptFileNames(boolean includeDisabled) {
        File[] files = plugin.getScriptsFolder().listFiles((directory, name) -> name.toLowerCase(Locale.ROOT).endsWith(".es")
                && (includeDisabled || !name.startsWith("-")));
        if (files == null) {
            return List.of();
        }
        return Arrays.stream(files)
                .map(File::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    public ScriptToggleResult disableScript(String scriptName) {
        String fileName = normalizeScriptFileName(scriptName);
        if (fileName.startsWith("-")) {
            return new ScriptToggleResult(false, "Script is already disabled: " + fileName);
        }
        File source = new File(plugin.getScriptsFolder(), fileName);
        File target = new File(plugin.getScriptsFolder(), "-" + fileName);
        return moveScriptFile(source, target, "Disabled " + fileName);
    }

    public ScriptToggleResult enableScript(String scriptName) {
        String fileName = normalizeScriptFileName(scriptName);
        File source = new File(plugin.getScriptsFolder(), fileName.startsWith("-") ? fileName : "-" + fileName);
        String enabledName = source.getName().startsWith("-") ? source.getName().substring(1) : source.getName();
        File target = new File(plugin.getScriptsFolder(), enabledName);
        return moveScriptFile(source, target, "Enabled " + enabledName);
    }

    public void callFunction(String name, List<String> arguments, ScriptContext parentContext) {
        ScriptFunction function = runtimeScripts.getFunction(name);
        if (function == null) {
            plugin.getLogger().warning("Unknown EasyScript function: " + name);
            return;
        }

        Map<String, String> variables = new java.util.HashMap<>();
        for (int index = 0; index < function.getParameters().size(); index++) {
            String value = index < arguments.size() ? arguments.get(index) : "";
            variables.put(function.getParameters().get(index), value);
            variables.put("function-arg-" + (index + 1), value);
        }
        variables.put("function-args", String.join(" ", arguments));

        executeActions(function.getActions(), parentContext.withVariables(variables));
    }

    public void runLoadEvents() {
        runEvents(TriggerType.LOAD, ScriptContext.event(plugin, plugin.getServer().getConsoleSender(), null, null, false, Map.of()));
    }

    public void runUnloadEvents() {
        runEvents(TriggerType.UNLOAD, ScriptContext.event(plugin, plugin.getServer().getConsoleSender(), null, null, false, Map.of()));
    }

    public int getFunctionCount() {
        return runtimeScripts.getFunctionNames().size();
    }

    public int getRegisteredAddonActionCount() {
        return pl.macie.easyscript.api.EasyScriptApi.registry().actionCount();
    }

    public int getRegisteredAddonConditionCount() {
        return pl.macie.easyscript.api.EasyScriptApi.registry().conditionCount();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerJoin(PlayerJoinEvent event) {
        runEvents(TriggerType.JOIN, ScriptContext.playerEvent(plugin, event.getPlayer(), event, false));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerQuit(PlayerQuitEvent event) {
        runEvents(TriggerType.QUIT, ScriptContext.playerEvent(plugin, event.getPlayer(), event, false));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        ScriptContext context = ScriptContext.chat(plugin, event.getPlayer(), event, event.isAsynchronous());
        runEvents(TriggerType.CHAT, context);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String commandLine = event.getMessage();
        String commandName = commandLine.startsWith("/") ? commandLine.substring(1) : commandLine;
        int spaceIndex = commandName.indexOf(' ');
        if (spaceIndex >= 0) {
            commandName = commandName.substring(0, spaceIndex);
        }
        Map<String, String> variables = Map.of(
                "command-line", commandLine,
                "command", commandName,
                "message", commandLine
        );
        runEvents(TriggerType.COMMAND, ScriptContext.event(plugin, event.getPlayer(), event.getPlayer(), event, false, variables));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Map<String, String> variables = new java.util.HashMap<>();
        variables.put("death-message", event.getDeathMessage() == null ? "" : event.getDeathMessage());
        EntityDamageEvent lastDamage = event.getEntity().getLastDamageCause();
        variables.put("cause", lastDamage == null ? "" : lastDamage.getCause().name().toLowerCase(Locale.ROOT));
        runEvents(TriggerType.DEATH, ScriptContext.event(plugin, event.getEntity(), event.getEntity(), event, false, variables));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        runEvents(TriggerType.RESPAWN, ScriptContext.playerEvent(plugin, event.getPlayer(), event, false));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {
        runEvents(TriggerType.BLOCK_BREAK, blockContext(event.getPlayer(), event, event.getBlock()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        runEvents(TriggerType.BLOCK_PLACE, blockContext(event.getPlayer(), event, event.getBlockPlaced()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Map<String, String> variables = new java.util.HashMap<>();
        variables.put("action", event.getAction().name().toLowerCase(Locale.ROOT));
        variables.put("item", event.getItem() == null ? "" : event.getItem().getType().name().toLowerCase(Locale.ROOT));
        variables.put("item-type", variables.get("item"));
        if (block != null) {
            addBlockVariables(variables, block);
        }
        runEvents(TriggerType.INTERACT, ScriptContext.event(plugin, event.getPlayer(), event.getPlayer(), event, false, variables));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        runEvents(TriggerType.DROP, itemContext(event.getPlayer(), event, event.getItemDrop()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        runEvents(TriggerType.PICKUP, itemContext(player, event, event.getItem()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Map<String, String> variables = new java.util.HashMap<>();
        variables.put("damage", String.valueOf(Math.round(event.getFinalDamage() * 10.0) / 10.0));
        variables.put("cause", event.getCause().name().toLowerCase(Locale.ROOT));
        if (event instanceof EntityDamageByEntityEvent byEntityEvent) {
            Entity damager = byEntityEvent.getDamager();
            variables.put("damager", damager.getName());
            variables.put("damager-type", damager.getType().name().toLowerCase(Locale.ROOT));
        }
        runEvents(TriggerType.DAMAGE, ScriptContext.event(plugin, player, player, event, false, variables));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!runtimeScripts.hasEvents(TriggerType.INVENTORY_CLICK) && !runtimeScripts.hasEvents(TriggerType.GUI_CLICK)) {
            return;
        }

        Map<String, String> variables = new java.util.HashMap<>();
        variables.put("slot", String.valueOf(event.getSlot()));
        variables.put("raw-slot", String.valueOf(event.getRawSlot()));
        variables.put("click", event.getClick().name().toLowerCase(Locale.ROOT));
        variables.put("inventory-title", event.getView().getTitle());
        addItemStackVariables(variables, event.getCurrentItem());
        ScriptContext context = ScriptContext.event(plugin, player, player, event, false, variables);
        runEvents(TriggerType.INVENTORY_CLICK, context);
        runEvents(TriggerType.GUI_CLICK, context);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Map<String, String> variables = new java.util.HashMap<>();
        addItemStackVariables(variables, event.getItem());
        runEvents(TriggerType.ITEM_CONSUME, ScriptContext.event(plugin, event.getPlayer(), event.getPlayer(), event, false, variables));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) {
            return;
        }
        Map<String, String> variables = new java.util.HashMap<>();
        variables.put("projectile", projectile.getType().name().toLowerCase(Locale.ROOT));
        if (event.getHitEntity() != null) {
            variables.put("hit-entity", event.getHitEntity().getName());
            variables.put("hit-entity-type", event.getHitEntity().getType().name().toLowerCase(Locale.ROOT));
        }
        if (event.getHitBlock() != null) {
            addBlockVariables(variables, event.getHitBlock());
        }
        runEvents(TriggerType.PROJECTILE_HIT, ScriptContext.event(plugin, player, player, event, false, variables));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Map<String, String> variables = new java.util.HashMap<>();
        variables.put("entity", event.getEntity().getName());
        variables.put("entity-type", event.getEntity().getType().name().toLowerCase(Locale.ROOT));
        variables.put("killer", killer == null ? "" : killer.getName());
        CommandSenderAndPlayer target = killer == null
                ? new CommandSenderAndPlayer(plugin.getServer().getConsoleSender(), null)
                : new CommandSenderAndPlayer(killer, killer);
        runEvents(TriggerType.ENTITY_DEATH, ScriptContext.event(plugin, target.sender(), target.player(), event, false, variables));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!runtimeScripts.hasEvents(TriggerType.PLAYER_MOVE)) {
            return;
        }
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        runEvents(TriggerType.PLAYER_MOVE, ScriptContext.playerEvent(plugin, event.getPlayer(), event, false));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onTeleport(PlayerTeleportEvent event) {
        Map<String, String> variables = Map.of("teleport-cause", event.getCause().name().toLowerCase(Locale.ROOT));
        runEvents(TriggerType.TELEPORT, ScriptContext.event(plugin, event.getPlayer(), event.getPlayer(), event, false, variables));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onLogin(PlayerLoginEvent event) {
        Map<String, String> variables = Map.of(
                "address", event.getAddress().getHostAddress(),
                "hostname", event.getHostname() == null ? "" : event.getHostname()
        );
        runEvents(TriggerType.LOGIN, ScriptContext.event(plugin, event.getPlayer(), event.getPlayer(), event, false, variables));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onServerPing(ServerListPingEvent event) {
        Map<String, String> variables = Map.of(
                "motd", event.getMotd(),
                "address", event.getAddress().getHostAddress()
        );
        runEvents(TriggerType.SERVER_PING, ScriptContext.event(plugin, plugin.getServer().getConsoleSender(), null, event, false, variables));
    }

    private void runEvents(TriggerType triggerType, ScriptContext context) {
        for (ScriptEvent event : runtimeScripts.getEvents(triggerType)) {
            executeActions(event.getActions(), context);
        }
    }

    private ScriptContext blockContext(Player player, Event event, Block block) {
        Map<String, String> variables = new java.util.HashMap<>();
        addBlockVariables(variables, block);
        return ScriptContext.event(plugin, player, player, event, false, variables);
    }

    private ScriptContext itemContext(Player player, Event event, Item item) {
        Map<String, String> variables = new java.util.HashMap<>();
        variables.put("item", item.getItemStack().getType().name().toLowerCase(Locale.ROOT));
        variables.put("item-type", variables.get("item"));
        variables.put("item-amount", String.valueOf(item.getItemStack().getAmount()));
        return ScriptContext.event(plugin, player, player, event, false, variables);
    }

    private void addBlockVariables(Map<String, String> variables, Block block) {
        variables.put("block", block.getType().name().toLowerCase(Locale.ROOT));
        variables.put("block-type", variables.get("block"));
        variables.put("block-world", block.getWorld().getName());
        variables.put("block-x", String.valueOf(block.getX()));
        variables.put("block-y", String.valueOf(block.getY()));
        variables.put("block-z", String.valueOf(block.getZ()));
    }

    private void addItemStackVariables(Map<String, String> variables, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            variables.put("item", "air");
            variables.put("item-type", "air");
            variables.put("item-amount", "0");
            return;
        }
        variables.put("item", itemStack.getType().name().toLowerCase(Locale.ROOT));
        variables.put("item-type", variables.get("item"));
        variables.put("item-amount", String.valueOf(itemStack.getAmount()));
    }

    private void executeSafely(Action action, ScriptContext context) {
        try {
            action.execute(context);
        } catch (RuntimeException exception) {
            Event event = context.getEvent();
            String source = action.getSource().display();
            String eventName = event == null ? "command" : event.getEventName();
            plugin.getLogger().warning("Error in " + source + " during " + eventName + ": " + exception.getMessage());
        }
    }

    private List<ScriptCommand> validateCommands(List<ScriptCommand> commands, List<String> errors) {
        Set<String> usedLabels = new HashSet<>();
        List<ScriptCommand> validCommands = new ArrayList<>();

        for (ScriptCommand command : commands) {
            String name = command.getName().toLowerCase(Locale.ROOT);
            if (!isValidCommandName(name)) {
                errors.add(command.getSource().display() + ": invalid command name '/" + command.getName() + "'");
                continue;
            }

            if (RESERVED_COMMANDS.contains(name)) {
                errors.add(command.getSource().display() + ": '/" + command.getName() + "' is reserved by EasyScript");
                continue;
            }

            if (!usedLabels.add(name)) {
                errors.add(command.getSource().display() + ": duplicate command '/" + command.getName() + "'");
                continue;
            }

            List<String> aliases = new ArrayList<>();
            for (String alias : command.getAliases()) {
                String normalizedAlias = alias.toLowerCase(Locale.ROOT);
                if (!isValidCommandName(normalizedAlias)) {
                    errors.add(command.getSource().display() + ": invalid alias '/" + alias + "'");
                    continue;
                }
                if (RESERVED_COMMANDS.contains(normalizedAlias) || normalizedAlias.equals(name)) {
                    errors.add(command.getSource().display() + ": skipped reserved alias '/" + alias + "'");
                    continue;
                }
                if (!usedLabels.add(normalizedAlias)) {
                    errors.add(command.getSource().display() + ": duplicate alias '/" + alias + "'");
                    continue;
                }
                aliases.add(normalizedAlias);
            }

            validCommands.add(command.withAliases(aliases));
        }

        return validCommands;
    }

    private boolean isValidCommandName(String value) {
        return value.matches("[a-z0-9][a-z0-9_.-]*");
    }

    private int countActions(List<ScriptCommand> commands, Map<TriggerType, List<ScriptEvent>> events, Map<String, ScriptFunction> functions) {
        int count = 0;
        for (ScriptCommand command : commands) {
            count += command.getActions().size();
        }
        for (List<ScriptEvent> eventList : events.values()) {
            for (ScriptEvent event : eventList) {
                count += event.getActions().size();
            }
        }
        for (ScriptFunction function : functions.values()) {
            count += function.getActions().size();
        }
        return count;
    }

    private String normalizeScriptFileName(String scriptName) {
        String value = scriptName == null ? "" : scriptName.trim().replace('\\', '/');
        int slash = value.lastIndexOf('/');
        if (slash >= 0) {
            value = value.substring(slash + 1);
        }
        if (!value.toLowerCase(Locale.ROOT).endsWith(".es")) {
            value += ".es";
        }
        return value;
    }

    private ScriptToggleResult moveScriptFile(File source, File target, String successMessage) {
        try {
            if (!source.getCanonicalFile().getParentFile().equals(plugin.getScriptsFolder().getCanonicalFile())) {
                return new ScriptToggleResult(false, "Invalid script path.");
            }
            if (!source.exists()) {
                return new ScriptToggleResult(false, "Script does not exist: " + source.getName());
            }
            if (target.exists()) {
                return new ScriptToggleResult(false, "Target file already exists: " + target.getName());
            }
            Files.move(source.toPath(), target.toPath());
            reloadScripts();
            return new ScriptToggleResult(true, successMessage);
        } catch (IOException exception) {
            return new ScriptToggleResult(false, "Could not rename script: " + exception.getMessage());
        }
    }

    private Map<TriggerType, List<ScriptEvent>> emptyEventMap() {
        Map<TriggerType, List<ScriptEvent>> events = new EnumMap<>(TriggerType.class);
        for (TriggerType triggerType : TriggerType.values()) {
            events.put(triggerType, new ArrayList<>());
        }
        return events;
    }

    private record CommandSenderAndPlayer(org.bukkit.command.CommandSender sender, Player player) {
    }

    public record ScriptToggleResult(boolean success, String message) {
    }
}
