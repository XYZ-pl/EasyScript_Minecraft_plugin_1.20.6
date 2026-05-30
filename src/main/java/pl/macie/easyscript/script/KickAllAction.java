package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

import java.util.ArrayList;

public final class KickAllAction implements Action {
    private final String reason;
    private final SourceLocation source;

    public KickAllAction(String reason, SourceLocation source) {
        this.reason = reason;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        String prepared = ColorUtil.colorize(TextUtil.applyPlaceholders(reason, context));
        for (Player player : new ArrayList<>(context.getPlugin().getServer().getOnlinePlayers())) {
            player.kickPlayer(prepared);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
