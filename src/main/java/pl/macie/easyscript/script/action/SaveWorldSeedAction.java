package pl.macie.easyscript.script.action;

import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class SaveWorldSeedAction implements Action {
    private final String variableName;
    private final SourceLocation source;

    public SaveWorldSeedAction(String variableName, SourceLocation source) {
        this.variableName = variableName;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        World world = player == null ? context.getPlugin().getServer().getWorlds().get(0) : player.getWorld();
        String key = TextUtil.applyPlaceholders(variableName, context);
        context.getVariables().set(key, String.valueOf(world.getSeed()));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
