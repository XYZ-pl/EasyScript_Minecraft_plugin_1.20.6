package pl.macie.easyscript.script.condition;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class MessageCondition implements Condition {
    private final Mode mode;
    private final String expected;

    public MessageCondition(Mode mode, String expected) {
        this.mode = mode;
        this.expected = expected;
    }

    @Override
    public boolean test(ScriptContext context) {
        if (!(context.getEvent() instanceof AsyncPlayerChatEvent event)) {
            return false;
        }

        String message = event.getMessage().toLowerCase(Locale.ROOT);
        String preparedExpected = TextUtil.applyPlaceholders(expected, context).toLowerCase(Locale.ROOT);
        return switch (mode) {
            case CONTAINS -> message.contains(preparedExpected);
            case EQUALS -> message.equals(preparedExpected);
            case STARTS_WITH -> message.startsWith(preparedExpected);
        };
    }

    @Override
    public boolean canRunAsynchronously() {
        return true;
    }

    public enum Mode {
        CONTAINS,
        EQUALS,
        STARTS_WITH
    }
}
