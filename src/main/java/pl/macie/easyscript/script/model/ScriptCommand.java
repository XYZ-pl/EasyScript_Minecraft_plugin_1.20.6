package pl.macie.easyscript.script.model;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.action.Action;

import java.util.List;

public final class ScriptCommand {
    private final String name;
    private final List<String> aliases;
    private final String permission;
    private final String permissionMessage;
    private final String usage;
    private final String description;
    private final List<String> argumentTypes;
    private final long cooldownTicks;
    private final String cooldownMessage;
    private final List<Action> actions;
    private final SourceLocation source;

    public ScriptCommand(
            String name,
            List<String> aliases,
            String permission,
            String permissionMessage,
            String usage,
            String description,
            List<String> argumentTypes,
            long cooldownTicks,
            String cooldownMessage,
            List<Action> actions,
            SourceLocation source
    ) {
        this.name = name;
        this.aliases = List.copyOf(aliases);
        this.permission = blankToNull(permission);
        this.permissionMessage = blankToNull(permissionMessage);
        this.usage = usage == null || usage.isBlank() ? "/" + name : usage;
        this.description = description == null || description.isBlank() ? "EasyScript command" : description;
        this.argumentTypes = List.copyOf(argumentTypes == null ? List.of() : argumentTypes);
        this.cooldownTicks = Math.max(0L, cooldownTicks);
        this.cooldownMessage = blankToNull(cooldownMessage);
        this.actions = List.copyOf(actions);
        this.source = source;
    }

    public ScriptCommand withAliases(List<String> newAliases) {
        return new ScriptCommand(
                name,
                newAliases,
                permission,
                permissionMessage,
                usage,
                description,
                argumentTypes,
                cooldownTicks,
                cooldownMessage,
                actions,
                source
        );
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getPermission() {
        return permission;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getArgumentTypes() {
        return argumentTypes;
    }

    public long getCooldownTicks() {
        return cooldownTicks;
    }

    public String getCooldownMessage() {
        return cooldownMessage;
    }

    public List<Action> getActions() {
        return actions;
    }

    public SourceLocation getSource() {
        return source;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
