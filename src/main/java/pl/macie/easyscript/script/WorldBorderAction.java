package pl.macie.easyscript.script.action;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptLocationUtil;

public final class WorldBorderAction implements Action {
    private final Mode mode;
    private final String target;
    private final double size;
    private final SourceLocation source;

    private WorldBorderAction(Mode mode, String target, double size, SourceLocation source) {
        this.mode = mode;
        this.target = target;
        this.size = Math.max(1.0, size);
        this.source = source;
    }

    public static WorldBorderAction center(String target, double size, SourceLocation source) {
        return new WorldBorderAction(Mode.CENTER_AND_SIZE, target, size, source);
    }

    public static WorldBorderAction size(double size, SourceLocation source) {
        return new WorldBorderAction(Mode.SIZE_ONLY, "player", size, source);
    }

    @Override
    public void execute(ScriptContext context) {
        Location location = ScriptLocationUtil.location(context, target);
        WorldBorder border = location.getWorld().getWorldBorder();
        if (mode == Mode.CENTER_AND_SIZE) {
            border.setCenter(location);
        }
        border.setSize(size);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private enum Mode {
        CENTER_AND_SIZE,
        SIZE_ONLY
    }
}
