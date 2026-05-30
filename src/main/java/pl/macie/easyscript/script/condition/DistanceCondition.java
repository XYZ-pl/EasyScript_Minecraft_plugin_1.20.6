package pl.macie.easyscript.script.condition;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptLocationUtil;

public final class DistanceCondition implements Condition {
    private final String target;
    private final double radius;

    public DistanceCondition(String target, double radius) {
        this.target = target;
        this.radius = Math.max(0.0, radius);
    }

    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }
        Location playerLocation = player.getLocation();
        Location targetLocation = ScriptLocationUtil.location(context, target);
        return playerLocation.getWorld().equals(targetLocation.getWorld())
                && playerLocation.distanceSquared(targetLocation) <= radius * radius;
    }
}
