package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class FreezeTicksAction implements Action {
    private final int ticks;
    private final SourceLocation source;

    public FreezeTicksAction(int ticks, SourceLocation source) {
        this.ticks = Math.max(0, ticks);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.setFreezeTicks(ticks);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
