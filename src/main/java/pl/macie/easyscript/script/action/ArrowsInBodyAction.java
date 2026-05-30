package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class ArrowsInBodyAction implements Action {
    private final int arrows;
    private final SourceLocation source;

    public ArrowsInBodyAction(int arrows, SourceLocation source) {
        this.arrows = Math.max(0, arrows);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.setArrowsInBody(arrows);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
