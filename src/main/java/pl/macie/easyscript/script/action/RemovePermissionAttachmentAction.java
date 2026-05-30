package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class RemovePermissionAttachmentAction implements Action {
    private final String permission;
    private final SourceLocation source;

    public RemovePermissionAttachmentAction(String permission, SourceLocation source) {
        this.permission = permission;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        String prepared = TextUtil.applyPlaceholders(permission, context).toLowerCase(Locale.ROOT);
        PermissionAttachmentAction.remove(player, prepared);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
