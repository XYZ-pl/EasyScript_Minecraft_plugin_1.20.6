package pl.macie.easyscript.script.action;

import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class SaveWorldAction implements Action {
    private final SourceLocation source;

    public SaveWorldAction(SourceLocation source) {
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.getWorld().save();
            return;
        }
        for (World world : context.getPlugin().getServer().getWorlds()) {
            world.save();
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
