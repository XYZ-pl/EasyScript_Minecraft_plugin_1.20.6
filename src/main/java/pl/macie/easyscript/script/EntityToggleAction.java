package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class EntityToggleAction implements Action {
    private final Mode mode;
    private final boolean enabled;
    private final SourceLocation source;

    public EntityToggleAction(Mode mode, boolean enabled, SourceLocation source) {
        this.mode = mode;
        this.enabled = enabled;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        switch (mode) {
            case GLOWING -> player.setGlowing(enabled);
            case INVULNERABLE -> player.setInvulnerable(enabled);
            case SILENT -> player.setSilent(enabled);
            case GRAVITY -> player.setGravity(enabled);
            case VISUAL_FIRE -> player.setVisualFire(enabled);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    public enum Mode {
        GLOWING,
        INVULNERABLE,
        SILENT,
        GRAVITY,
        VISUAL_FIRE
    }
}
