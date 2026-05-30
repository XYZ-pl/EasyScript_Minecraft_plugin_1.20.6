package pl.macie.easyscript.script.action;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class LoopTimesAction implements Action {
    private final int times;
    private final long periodTicks;
    private final List<Action> actions;
    private final SourceLocation source;

    public LoopTimesAction(int times, long periodTicks, List<Action> actions, SourceLocation source) {
        this.times = Math.max(1, times);
        this.periodTicks = Math.max(1L, periodTicks);
        this.actions = List.copyOf(actions);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        if (periodTicks <= 1L) {
            for (int index = 1; index <= times; index++) {
                executeIteration(context, index);
            }
            return;
        }

        Plugin plugin = context.getPlugin();
        AtomicInteger current = new AtomicInteger(1);
        AtomicReference<BukkitTask> taskRef = new AtomicReference<>();
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            int loopNumber = current.getAndIncrement();
            executeIteration(context.asSynchronous(), loopNumber);
            if (loopNumber >= times) {
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

    private void executeIteration(ScriptContext context, int index) {
        ScriptContext loopContext = context.withVariables(Map.of(
                "loop-number", String.valueOf(index),
                "loop-index", String.valueOf(index)
        ));
        for (Action action : actions) {
            action.execute(loopContext);
        }
    }
}
