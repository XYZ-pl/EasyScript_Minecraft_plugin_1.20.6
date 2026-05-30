package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class PlayerNameAction implements Action {
    private final Type type;
    private final String name;
    private final SourceLocation source;

    public PlayerNameAction(Type type, String name, SourceLocation source) {
        this.type = type;
        this.name = name;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        String prepared = ColorUtil.colorize(TextUtil.applyPlaceholders(name, context));
        if (type == Type.DISPLAY) {
            player.setDisplayName(prepared);
        } else {
            player.setPlayerListName(prepared);
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
