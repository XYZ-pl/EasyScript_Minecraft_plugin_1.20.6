package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class SendPermissionAction implements Action {
    private final String message;
    private final String permission;
    private final SourceLocation source;

    public SendPermissionAction(String message, String permission, SourceLocation source) {
        this.message = message;
        this.permission = permission;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        String preparedPermission = TextUtil.applyPlaceholders(permission, context);
        String preparedMessage = ColorUtil.colorize(TextUtil.applyPlaceholders(message, context));
        for (Player onlinePlayer : context.getPlugin().getServer().getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(preparedPermission)) {
                onlinePlayer.sendMessage(preparedMessage);
            }
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
