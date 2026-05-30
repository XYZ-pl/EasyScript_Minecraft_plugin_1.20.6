package pl.macie.easyscript.script.action;

import org.bukkit.Location;
import org.bukkit.Material;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptItemUtil;
import pl.macie.easyscript.util.ScriptLocationUtil;

public final class SetBlockAction implements Action {
    private final String target;
    private final String materialName;
    private final SourceLocation source;

    public SetBlockAction(String target, String materialName, SourceLocation source) {
        this.target = target;
        this.materialName = materialName;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Location location = ScriptLocationUtil.blockLocation(context, target);
        Material material = ScriptItemUtil.material(materialName, context, false);
        if (!material.isBlock()) {
            throw new IllegalArgumentException("Material is not a block: " + material);
        }
        location.getBlock().setType(material);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
