package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class ExperienceAction implements Action {
    private final Mode mode;
    private final int amount;
    private final SourceLocation source;

    public ExperienceAction(Mode mode, int amount, SourceLocation source) {
        this.mode = mode;
        this.amount = amount;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        if (mode == Mode.ADD_XP) {
            player.giveExp(amount);
        } else {
            player.setLevel(Math.max(0, amount));
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    public enum Mode {
        ADD_XP,
        SET_LEVEL
    }
}
