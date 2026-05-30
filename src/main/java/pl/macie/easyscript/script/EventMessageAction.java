package pl.macie.easyscript.script.action;

import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class EventMessageAction implements Action {
    private final Type type;
    private final String message;
    private final SourceLocation source;

    public EventMessageAction(Type type, String message, SourceLocation source) {
        this.type = type;
        this.message = message;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Event event = context.getEvent();
        String prepared = ColorUtil.colorize(TextUtil.applyPlaceholders(message, context));
        switch (type) {
            case JOIN -> {
                if (event instanceof PlayerJoinEvent joinEvent) {
                    joinEvent.setJoinMessage(prepared);
                }
            }
            case QUIT -> {
                if (event instanceof PlayerQuitEvent quitEvent) {
                    quitEvent.setQuitMessage(prepared);
                }
            }
            case DEATH -> {
                if (event instanceof PlayerDeathEvent deathEvent) {
                    deathEvent.setDeathMessage(prepared);
                }
            }
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    public enum Type {
        JOIN,
        QUIT,
        DEATH
    }
}
