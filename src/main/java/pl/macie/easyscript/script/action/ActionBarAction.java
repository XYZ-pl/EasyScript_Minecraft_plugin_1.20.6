package pl.macie.easyscript.script.action;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class ActionBarAction implements Action {
    private final String message;
    private final SourceLocation source;

    public ActionBarAction(String message, SourceLocation source) {
        this.message = message;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        String text = ColorUtil.colorize(TextUtil.applyPlaceholders(message, context));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
