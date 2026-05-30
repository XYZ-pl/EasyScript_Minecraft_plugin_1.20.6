package pl.macie.easyscript.script.action;

import org.bukkit.Location;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptLocationUtil;

public final class BreakBlockAction implements Action {
    private final String target;
    private final boolean naturally;
    private final SourceLocation source;

    public BreakBlockAction(String target, boolean naturally, SourceLocation source) {
        this.target = target;
        this.naturally = naturally;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Location location = ScriptLocationUtil.blockLocation(context, target);
        if (naturally) {
            location.getBlock().breakNaturally();
        } else {
            location.getBlock().setType(org.bukkit.Material.AIR);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
