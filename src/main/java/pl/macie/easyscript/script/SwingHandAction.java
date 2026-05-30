package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class SwingHandAction implements Action {
    private final Hand hand;
    private final SourceLocation source;

    public SwingHandAction(Hand hand, SourceLocation source) {
        this.hand = hand;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        if (hand == Hand.OFF) {
            player.swingOffHand();
        } else {
            player.swingMainHand();
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    public enum Hand {
        MAIN,
        OFF
    }
}
