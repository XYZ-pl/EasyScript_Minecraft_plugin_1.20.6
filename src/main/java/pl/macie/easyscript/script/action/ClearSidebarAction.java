package pl.macie.easyscript.script.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class ClearSidebarAction implements Action {
    private final SourceLocation source;

    public ClearSidebarAction(SourceLocation source) {
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null && Bukkit.getScoreboardManager() != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
