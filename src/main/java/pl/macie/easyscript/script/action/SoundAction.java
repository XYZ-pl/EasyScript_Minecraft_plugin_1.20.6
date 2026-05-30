package pl.macie.easyscript.script.action;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class SoundAction implements Action {
    private final String soundName;
    private final float volume;
    private final float pitch;
    private final SourceLocation source;

    public SoundAction(String soundName, float volume, float pitch, SourceLocation source) {
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        String preparedName = TextUtil.applyPlaceholders(soundName, context).toUpperCase(Locale.ROOT);
        Sound sound = Sound.valueOf(preparedName);
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
