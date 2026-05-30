package pl.macie.easyscript.script.action;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class RepeatAction implements Action {
    private final int times;
    private final long periodTicks;
    private final Action nestedAction;
    private final SourceLocation source;

    public RepeatAction(int times, long periodTicks, Action nestedAction, SourceLocation source) {
        this.times = Math.max(1, times);
        this.periodTicks = Math.max(1L, periodTicks);
        this.nestedAction = nestedAction;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Plugin plugin = context.getPlugin();
        AtomicInteger remaining = new AtomicInteger(times);
        AtomicReference<BukkitTask> taskRef = new AtomicReference<>();
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            nestedAction.execute(context.asSynchronous());
            if (remaining.decrementAndGet() <= 0) {
                BukkitTask taskToCancel = taskRef.get();
                if (taskToCancel != null) {
                    taskToCancel.cancel();
                }
            }
        }, 0L, periodTicks);
        taskRef.set(task);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
