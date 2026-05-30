package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class KickAction implements Action {
    private final String reason;
    private final SourceLocation source;

    public KickAction(String reason, SourceLocation source) {
        this.reason = reason;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.kickPlayer(ColorUtil.colorize(TextUtil.applyPlaceholders(reason, context)));
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
