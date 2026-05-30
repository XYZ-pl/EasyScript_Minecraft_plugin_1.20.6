package pl.macie.easyscript.script.action;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.ScriptFileUtil;

import java.io.File;

public final class SendFileAction implements Action {
    private final MessageAction.Target target;
    private final String path;
    private final SourceLocation source;

    public SendFileAction(MessageAction.Target target, String path, SourceLocation source) {
        this.target = target;
        this.path = path;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        File file = ScriptFileUtil.resolve(context, path);
        if (!file.isFile()) {
            throw new IllegalStateException("File does not exist: " + file.getName());
        }

        CommandSender receiver = switch (target) {
            case PLAYER -> context.getPlayer();
            case SENDER -> context.getSender();
            case CONSOLE -> Bukkit.getConsoleSender();
        };
        if (receiver == null) {
            return;
        }

        String text = ScriptFileUtil.readLimited(context, file);
        receiver.sendMessage(ColorUtil.colorize(text));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
