package pl.macie.easyscript.script.action;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class ClearChatAction implements Action {
    private static final int EMPTY_LINES = 120;

    private final Target target;
    private final SourceLocation source;

    public ClearChatAction(Target target, SourceLocation source) {
        this.target = target;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        if (target == Target.PLAYER) {
            Player player = context.getPlayer();
            if (player != null) {
                clear(player);
            }
            return;
        }

        for (Player player : context.getPlugin().getServer().getOnlinePlayers()) {
            clear(player);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private void clear(CommandSender sender) {
        for (int index = 0; index < EMPTY_LINES; index++) {
            sender.sendMessage("");
        }
    }

    public enum Target {
        PLAYER,
        ALL
    }
}
