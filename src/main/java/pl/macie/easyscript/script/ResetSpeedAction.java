package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class ResetSpeedAction implements Action {
    private final Type type;
    private final SourceLocation source;

    public ResetSpeedAction(Type type, SourceLocation source) {
        this.type = type;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        if (type == Type.WALK) {
            player.setWalkSpeed(0.2F);
        } else {
            player.setFlySpeed(0.1F);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    public enum Type {
        WALK,
        FLY
    }
}
