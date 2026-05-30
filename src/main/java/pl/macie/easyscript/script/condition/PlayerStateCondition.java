package pl.macie.easyscript.script.condition;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;

public final class PlayerStateCondition implements Condition {
    private final State state;

    public PlayerStateCondition(State state) {
        this.state = state;
    }

    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }

        return switch (state) {
            case SNEAKING -> player.isSneaking();
            case SPRINTING -> player.isSprinting();
            case FLYING -> player.isFlying();
            case GLIDING -> player.isGliding();
            case SWIMMING -> player.isSwimming();
            case BLOCKING -> player.isBlocking();
        };
    }

    public enum State {
        SNEAKING,
        SPRINTING,
        FLYING,
        GLIDING,
        SWIMMING,
        BLOCKING
    }
}
