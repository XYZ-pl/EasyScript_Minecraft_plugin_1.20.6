package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class StopSoundAction implements Action {
    private final String sound;
    private final SourceLocation source;

    public StopSoundAction(String sound, SourceLocation source) {
        this.sound = sound;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        player.stopSound(TextUtil.applyPlaceholders(sound, context));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
