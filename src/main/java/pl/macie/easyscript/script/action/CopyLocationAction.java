package pl.macie.easyscript.script.action;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class CopyLocationAction implements Action {
    private final String variableName;
    private final boolean includeYawPitch;
    private final SourceLocation source;

    public CopyLocationAction(String variableName, boolean includeYawPitch, SourceLocation source) {
        this.variableName = variableName;
        this.includeYawPitch = includeYawPitch;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        String key = TextUtil.applyPlaceholders(variableName, context);
        context.getVariables().set(key, serialize(player.getLocation()));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private String serialize(Location location) {
        String base = location.getWorld().getName()
                + "," + round(location.getX())
                + "," + round(location.getY())
                + "," + round(location.getZ());
        if (!includeYawPitch) {
            return base;
        }
        return base + "," + round(location.getYaw()) + "," + round(location.getPitch());
    }

    private String round(double value) {
        return String.valueOf(Math.round(value * 100.0) / 100.0);
    }
}
