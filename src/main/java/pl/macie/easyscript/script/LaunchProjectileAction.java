package pl.macie.easyscript.script.action;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Trident;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class LaunchProjectileAction implements Action {
    private final String projectile;
    private final SourceLocation source;

    public LaunchProjectileAction(String projectile, SourceLocation source) {
        this.projectile = projectile;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        player.launchProjectile(projectileClass(context));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private Class<? extends Projectile> projectileClass(ScriptContext context) {
        String prepared = TextUtil.applyPlaceholders(projectile, context).trim().toLowerCase(Locale.ROOT);
        return switch (prepared) {
            case "arrow" -> Arrow.class;
            case "snowball" -> Snowball.class;
            case "egg" -> Egg.class;
            case "fireball" -> Fireball.class;
            case "small_fireball", "small fireball" -> SmallFireball.class;
            case "trident" -> Trident.class;
            default -> throw new IllegalArgumentException("Unknown projectile: " + prepared);
        };
    }
}
