package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.List;

public final class TargetPlayerAction implements Action {
    private final String playerName;
    private final List<Action> actions;
    private final SourceLocation source;

    public TargetPlayerAction(String playerName, List<Action> actions, SourceLocation source) {
        this.playerName = playerName;
        this.actions = List.copyOf(actions);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        String preparedName = TextUtil.applyPlaceholders(playerName, context);
        Player target = context.getPlugin().getServer().getPlayerExact(preparedName);
        if (target == null) {
            return;
        }
        ScriptContext targetContext = context.withPlayer(target);
        for (Action action : actions) {
            action.execute(targetContext);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
