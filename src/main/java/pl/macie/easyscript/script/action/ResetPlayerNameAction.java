package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class ResetPlayerNameAction implements Action {
    private final Type type;
    private final SourceLocation source;

    public ResetPlayerNameAction(Type type, SourceLocation source) {
        this.type = type;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        if (type == Type.DISPLAY) {
            player.setDisplayName(player.getName());
        } else {
            player.setPlayerListName(player.getName());
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    public enum Type {
        DISPLAY,
        TAB
    }
}
