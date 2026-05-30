package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class TabHeaderFooterAction implements Action {
    private final String header;
    private final String footer;
    private final SourceLocation source;

    public TabHeaderFooterAction(String header, String footer, SourceLocation source) {
        this.header = header;
        this.footer = footer;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.setPlayerListHeaderFooter(
                    ColorUtil.colorize(TextUtil.applyPlaceholders(header, context)),
                    ColorUtil.colorize(TextUtil.applyPlaceholders(footer, context))
            );
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
