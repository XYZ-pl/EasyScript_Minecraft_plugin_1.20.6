package pl.macie.easyscript.script.action;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptLocationUtil;

public final class CompassTargetAction implements Action {
    private final String target;
    private final SourceLocation source;

    public CompassTargetAction(String target, SourceLocation source) {
        this.target = target;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        Location location = ScriptLocationUtil.location(context, target);
        player.setCompassTarget(location);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
