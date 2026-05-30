package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class ResourcePackAction implements Action {
    private final String url;
    private final SourceLocation source;

    public ResourcePackAction(String url, SourceLocation source) {
        this.url = url;
        this.source = source;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.setResourcePack(TextUtil.applyPlaceholders(url, context));
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
