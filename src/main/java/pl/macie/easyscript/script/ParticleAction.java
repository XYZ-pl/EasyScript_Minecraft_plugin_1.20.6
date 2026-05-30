package pl.macie.easyscript.script.action;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class ParticleAction implements Action {
    private final String particleName;
    private final int count;
    private final SourceLocation source;

    public ParticleAction(String particleName, int count, SourceLocation source) {
        this.particleName = particleName;
        this.count = Math.max(1, count);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        String preparedName = TextUtil.applyPlaceholders(particleName, context).toUpperCase(Locale.ROOT);
        Particle particle = Particle.valueOf(preparedName);
        player.getWorld().spawnParticle(particle, player.getLocation().add(0.0, 1.0, 0.0), count, 0.35, 0.45, 0.35, 0.01);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
