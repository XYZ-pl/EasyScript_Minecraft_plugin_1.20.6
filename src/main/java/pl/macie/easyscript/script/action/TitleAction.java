package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class TitleAction implements Action {
    private final String title;
    private final String subtitle;
    private final int fadeInTicks;
    private final int stayTicks;
    private final int fadeOutTicks;
    private final SourceLocation source;

    public TitleAction(String title, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks, SourceLocation source) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeInTicks = fadeInTicks;
        this.stayTicks = stayTicks;
        this.fadeOutTicks = fadeOutTicks;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        player.sendTitle(
                ColorUtil.colorize(TextUtil.applyPlaceholders(title, context)),
                ColorUtil.colorize(TextUtil.applyPlaceholders(subtitle, context)),
                fadeInTicks,
                stayTicks,
                fadeOutTicks
        );
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
