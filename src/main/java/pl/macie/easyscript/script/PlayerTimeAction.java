package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class PlayerTimeAction implements Action {
    private final String time;
    private final boolean reset;
    private final SourceLocation source;

    public PlayerTimeAction(String time, boolean reset, SourceLocation source) {
        this.time = time;
        this.reset = reset;
        this.source = source;
    }

    public static PlayerTimeAction reset(SourceLocation source) {
        return new PlayerTimeAction("", true, source);
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        if (reset) {
            player.resetPlayerTime();
            return;
        }
        player.setPlayerTime(parseTime(context), false);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private long parseTime(ScriptContext context) {
        String prepared = TextUtil.applyPlaceholders(time, context).trim().toLowerCase(Locale.ROOT);
        return switch (prepared) {
            case "day" -> 1000L;
            case "noon" -> 6000L;
            case "night" -> 13000L;
            case "midnight" -> 18000L;
            default -> Long.parseLong(prepared);
        };
    }
}
