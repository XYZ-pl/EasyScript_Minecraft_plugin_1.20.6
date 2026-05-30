package pl.macie.easyscript.script.condition;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;

public final class TimeCondition implements Condition {
    private final Mode mode;

    public TimeCondition(Mode mode) {
        this.mode = mode;
    }

    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }
        long time = player.getWorld().getTime() % 24000L;
        return switch (mode) {
            case DAY -> time >= 0L && time < 12300L;
            case NIGHT -> time >= 12300L;
        };
    }

    public enum Mode {
        DAY,
        NIGHT
    }
}
