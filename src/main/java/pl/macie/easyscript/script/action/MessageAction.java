package pl.macie.easyscript.script.action;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class MessageAction implements Action {
    private final Target target;
    private final String message;
    private final SourceLocation source;

    public MessageAction(Target target, String message, SourceLocation source) {
        this.target = target;
        this.message = message;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        CommandSender receiver = switch (target) {
            case PLAYER -> context.getPlayer();
            case SENDER -> context.getSender();
            case CONSOLE -> Bukkit.getConsoleSender();
        };

        if (receiver == null) {
            return;
        }

        String text = ColorUtil.colorize(TextUtil.applyPlaceholders(message, context));
        receiver.sendMessage(text);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    public enum Target {
        PLAYER,
        SENDER,
        CONSOLE
    }
}
