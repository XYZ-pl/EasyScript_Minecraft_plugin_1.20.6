package pl.macie.easyscript.script.condition;

import org.bukkit.inventory.meta.ItemMeta;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.ScriptItemUtil;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class HeldItemNameCondition implements Condition {
    private final String expected;
    private final Mode mode;

    public HeldItemNameCondition(String expected, Mode mode) {
        this.expected = expected;
        this.mode = mode;
    }

    @Override
    public boolean test(ScriptContext context) {
        ItemMeta meta = ScriptItemUtil.heldMeta(context);
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }
        String current = meta.getDisplayName();
        String prepared = ColorUtil.colorize(TextUtil.applyPlaceholders(expected, context));
        return switch (mode) {
            case EQUALS -> current.equalsIgnoreCase(prepared);
            case CONTAINS -> current.toLowerCase(Locale.ROOT).contains(prepared.toLowerCase(Locale.ROOT));
        };
    }

    public enum Mode {
        EQUALS,
        CONTAINS
    }
}
