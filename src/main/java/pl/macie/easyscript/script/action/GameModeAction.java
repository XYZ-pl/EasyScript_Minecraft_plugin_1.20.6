package pl.macie.easyscript.script.action;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class GameModeAction implements Action {
    private final String mode;
    private final SourceLocation source;

    public GameModeAction(String mode, SourceLocation source) {
        this.mode = mode;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        String prepared = TextUtil.applyPlaceholders(mode, context).toUpperCase(Locale.ROOT);
        player.setGameMode(GameMode.valueOf(prepared));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
