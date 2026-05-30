package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class FireAction implements Action {
    private final int ticks;
    private final SourceLocation source;

    public FireAction(int ticks, SourceLocation source) {
        this.ticks = Math.max(0, ticks);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.setFireTicks(ticks);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
