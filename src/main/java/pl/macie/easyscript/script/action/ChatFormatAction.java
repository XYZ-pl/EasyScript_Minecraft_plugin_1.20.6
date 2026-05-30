package pl.macie.easyscript.script.action;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class ChatFormatAction implements Action {
    private final String format;
    private final SourceLocation source;

    public ChatFormatAction(String format, SourceLocation source) {
        this.format = format;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        if (!(context.getEvent() instanceof AsyncPlayerChatEvent event)) {
            return;
        }

        String formatted = ColorUtil.colorize(TextUtil.applyPlaceholders(format, context));
        event.setFormat(TextUtil.escapePercent(formatted));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    @Override
    public boolean canRunAsynchronously() {
        return true;
    }
}
