package pl.macie.easyscript.script.condition;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class GameModeCondition implements Condition {
    private final String gameMode;

    public GameModeCondition(String gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }
        String prepared = TextUtil.applyPlaceholders(gameMode, context).trim().toUpperCase(Locale.ROOT);
        return player.getGameMode() == GameMode.valueOf(prepared);
    }
}
