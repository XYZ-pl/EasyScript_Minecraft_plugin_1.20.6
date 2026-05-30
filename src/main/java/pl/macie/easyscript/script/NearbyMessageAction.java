package pl.macie.easyscript.script.action;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class NearbyMessageAction implements Action {
    private final String message;
    private final double radius;
    private final boolean includeSelf;
    private final SourceLocation source;

    public NearbyMessageAction(String message, double radius, boolean includeSelf, SourceLocation source) {
        this.message = message;
        this.radius = Math.max(0.0, radius);
        this.includeSelf = includeSelf;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        String prepared = ColorUtil.colorize(TextUtil.applyPlaceholders(message, context));
        if (includeSelf) {
            player.sendMessage(prepared);
        }
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player nearbyPlayer) {
                nearbyPlayer.sendMessage(prepared);
            }
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
