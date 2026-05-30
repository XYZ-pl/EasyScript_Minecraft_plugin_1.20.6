package pl.macie.easyscript.script.action;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class CancelEventAction implements Action {
    private final SourceLocation source;

    public CancelEventAction(SourceLocation source) {
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Event event = context.getEvent();
        if (event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }
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
