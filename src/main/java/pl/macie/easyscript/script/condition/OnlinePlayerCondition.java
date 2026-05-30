package pl.macie.easyscript.script.condition;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class OnlinePlayerCondition implements Condition {
    private final String playerName;

    public OnlinePlayerCondition(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public boolean test(ScriptContext context) {
        String prepared = TextUtil.applyPlaceholders(playerName, context);
        Player player = context.getPlugin().getServer().getPlayerExact(prepared);
        return player != null && player.isOnline();
    }
}
