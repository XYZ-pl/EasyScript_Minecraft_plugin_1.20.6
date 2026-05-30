package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class FlightAction implements Action {
    private final Mode mode;
    private final boolean enabled;
    private final float speed;
    private final SourceLocation source;

    private FlightAction(Mode mode, boolean enabled, float speed, SourceLocation source) {
        this.mode = mode;
        this.enabled = enabled;
        this.speed = Math.max(-1.0F, Math.min(1.0F, speed));
        this.source = source;
    }

    public static FlightAction toggle(Mode mode, boolean enabled, SourceLocation source) {
        return new FlightAction(mode, enabled, 0.0F, source);
    }

    public static FlightAction speed(Mode mode, float speed, SourceLocation source) {
        return new FlightAction(mode, false, speed, source);
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        switch (mode) {
            case ALLOW_FLIGHT -> player.setAllowFlight(enabled);
            case FLYING -> player.setFlying(enabled);
            case WALK_SPEED -> player.setWalkSpeed(speed);
            case FLY_SPEED -> player.setFlySpeed(speed);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    public enum Mode {
        ALLOW_FLIGHT,
        FLYING,
        WALK_SPEED,
        FLY_SPEED
    }
}
