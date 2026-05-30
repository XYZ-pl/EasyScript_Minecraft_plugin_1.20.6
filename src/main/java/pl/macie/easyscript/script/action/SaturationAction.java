package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class SaturationAction implements Action {
    private final float saturation;
    private final SourceLocation source;

    public SaturationAction(float saturation, SourceLocation source) {
        this.saturation = Math.max(0.0F, saturation);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.setSaturation(saturation);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
