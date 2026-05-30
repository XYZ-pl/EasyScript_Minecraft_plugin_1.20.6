package pl.macie.easyscript.script.condition;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;

public final class PlayerCanFlyCondition implements Condition {
    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        return player != null && player.getAllowFlight();
    }
}
