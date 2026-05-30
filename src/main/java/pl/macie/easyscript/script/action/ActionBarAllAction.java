package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class ActionBarAllAction implements Action {
    private final String message;
    private final SourceLocation source;

    public ActionBarAllAction(String message, SourceLocation source) {
        this.message = message;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        String prepared = ColorUtil.colorize(TextUtil.applyPlaceholders(message, context));
        for (Player player : context.getPlugin().getServer().getOnlinePlayers()) {
            player.sendActionBar(prepared);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
