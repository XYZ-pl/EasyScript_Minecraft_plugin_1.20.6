package pl.macie.easyscript.script.action;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class SpawnEntityAction implements Action {
    private final String entityName;
    private final SourceLocation source;

    public SpawnEntityAction(String entityName, SourceLocation source) {
        this.entityName = entityName;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        String prepared = TextUtil.applyPlaceholders(entityName, context).toUpperCase(Locale.ROOT);
        EntityType type = EntityType.valueOf(prepared);
        player.getWorld().spawnEntity(player.getLocation(), type);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
