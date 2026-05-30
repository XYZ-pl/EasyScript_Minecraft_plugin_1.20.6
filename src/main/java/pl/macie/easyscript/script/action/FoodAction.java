package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class FoodAction implements Action {
    private final int foodLevel;
    private final SourceLocation source;

    public FoodAction(int foodLevel, SourceLocation source) {
        this.foodLevel = Math.max(0, Math.min(20, foodLevel));
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.setFoodLevel(foodLevel);
            player.setSaturation(Math.max(player.getSaturation(), foodLevel));
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
