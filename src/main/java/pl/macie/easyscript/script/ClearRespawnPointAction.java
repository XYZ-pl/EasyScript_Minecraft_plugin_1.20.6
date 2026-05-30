package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class ClearRespawnPointAction implements Action {
    private final SourceLocation source;

    public ClearRespawnPointAction(SourceLocation source) {
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.setRespawnLocation(null, true);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
