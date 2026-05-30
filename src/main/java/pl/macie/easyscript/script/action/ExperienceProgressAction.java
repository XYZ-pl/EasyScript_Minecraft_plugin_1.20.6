package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class ExperienceProgressAction implements Action {
    private final float progress;
    private final SourceLocation source;

    public ExperienceProgressAction(float progress, SourceLocation source) {
        this.progress = Math.max(0.0F, Math.min(1.0F, progress));
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.setExp(progress);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
