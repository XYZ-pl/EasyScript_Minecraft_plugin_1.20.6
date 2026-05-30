package pl.macie.easyscript.script.action;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class CommandAction implements Action {
    private final Target target;
    private final String command;
    private final SourceLocation source;

    public CommandAction(Target target, String command, SourceLocation source) {
        this.target = target;
        this.command = command;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        CommandSender executor = switch (target) {
            case CONSOLE -> Bukkit.getConsoleSender();
            case PLAYER -> context.getPlayer();
        };

        if (executor == null) {
            return;
        }

        String preparedCommand = ColorUtil.colorize(TextUtil.applyPlaceholders(command, context));
        while (preparedCommand.startsWith("/")) {
            preparedCommand = preparedCommand.substring(1);
        }
        Bukkit.dispatchCommand(executor, preparedCommand);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    public enum Target {
        CONSOLE,
        PLAYER
    }
}
