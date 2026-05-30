package pl.macie.easyscript.script.condition;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;

public final class NearbyPlayersCondition implements Condition {
    private final double radius;
    private final ComparisonCondition.Operator operator;
    private final int amount;

    public NearbyPlayersCondition(double radius, ComparisonCondition.Operator operator, int amount) {
        this.radius = Math.max(0.0, radius);
        this.operator = operator;
        this.amount = Math.max(0, amount);
    }

    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }

        int count = 0;
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) {
                count++;
            }
        }
        int compared = Integer.compare(count, amount);
        return switch (operator) {
            case EQUALS -> compared == 0;
            case NOT_EQUALS -> compared != 0;
            case GREATER -> compared > 0;
            case GREATER_OR_EQUAL -> compared >= 0;
            case LESS -> compared < 0;
            case LESS_OR_EQUAL -> compared <= 0;
            case CONTAINS -> false;
        };
    }
}
