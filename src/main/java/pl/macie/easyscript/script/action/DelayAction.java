package pl.macie.easyscript.script.action;

import org.bukkit.plugin.Plugin;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class DelayAction implements Action {
    private final long delayTicks;
    private final Action nestedAction;
    private final SourceLocation source;

    public DelayAction(long delayTicks, Action nestedAction, SourceLocation source) {
        this.delayTicks = delayTicks;
        this.nestedAction = nestedAction;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Plugin plugin = context.getPlugin();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> nestedAction.execute(context.asSynchronous()), delayTicks);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
