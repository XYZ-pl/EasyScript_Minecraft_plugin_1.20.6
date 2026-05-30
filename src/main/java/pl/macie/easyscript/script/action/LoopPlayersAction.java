package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class LoopPlayersAction implements Action {
    private final List<Action> actions;
    private final SourceLocation source;

    public LoopPlayersAction(List<Action> actions, SourceLocation source) {
        this.actions = List.copyOf(actions);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        int index = 1;
        for (Player player : new ArrayList<>(context.getPlugin().getServer().getOnlinePlayers())) {
            ScriptContext loopContext = context.withPlayer(player).withVariables(Map.of(
                    "loop-player", player.getName(),
                    "loop-player-uuid", player.getUniqueId().toString(),
                    "loop-number", String.valueOf(index),
                    "loop-index", String.valueOf(index)
            ));
            for (Action action : actions) {
                action.execute(loopContext);
            }
            index++;
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
