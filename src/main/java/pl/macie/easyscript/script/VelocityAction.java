package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class VelocityAction implements Action {
    private final String vectorText;
    private final boolean add;
    private final SourceLocation source;

    public VelocityAction(String vectorText, boolean add, SourceLocation source) {
        this.vectorText = vectorText;
        this.add = add;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        Vector vector = vector(context);
        player.setVelocity(add ? player.getVelocity().add(vector) : vector);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private Vector vector(ScriptContext context) {
        String prepared = TextUtil.applyPlaceholders(vectorText, context).trim();
        String[] parts = prepared.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Velocity vector must be x,y,z");
        }
        return new Vector(
                Double.parseDouble(parts[0].trim()),
                Double.parseDouble(parts[1].trim()),
                Double.parseDouble(parts[2].trim())
        );
    }
}
