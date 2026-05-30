package pl.macie.easyscript.script.action;

import org.bukkit.Location;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptLocationUtil;

public final class ExplosionAction implements Action {
    private final String target;
    private final float power;
    private final boolean fire;
    private final boolean breakBlocks;
    private final SourceLocation source;

    public ExplosionAction(String target, float power, boolean fire, boolean breakBlocks, SourceLocation source) {
        this.target = target;
        this.power = Math.max(0.0F, power);
        this.fire = fire;
        this.breakBlocks = breakBlocks;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Location location = ScriptLocationUtil.location(context, target);
        location.getWorld().createExplosion(location, power, fire, breakBlocks);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
