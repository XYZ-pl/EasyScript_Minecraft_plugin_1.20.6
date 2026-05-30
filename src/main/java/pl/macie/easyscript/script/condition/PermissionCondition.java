package pl.macie.easyscript.script.condition;

import org.bukkit.command.CommandSender;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class PermissionCondition implements Condition {
    private final String permission;

    public PermissionCondition(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean test(ScriptContext context) {
        CommandSender sender = context.getSender();
        return sender != null && sender.hasPermission(TextUtil.applyPlaceholders(permission, context));
    }
}
