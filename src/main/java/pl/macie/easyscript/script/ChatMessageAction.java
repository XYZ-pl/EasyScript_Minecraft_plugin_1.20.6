package pl.macie.easyscript.script.action;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class ChatMessageAction implements Action {
    private final String message;
    private final SourceLocation source;

    public ChatMessageAction(String message, SourceLocation source) {
        this.message = message;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        if (!(context.getEvent() instanceof AsyncPlayerChatEvent event)) {
            return;
        }

        event.setMessage(ColorUtil.colorize(TextUtil.applyPlaceholders(message, context)));
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
