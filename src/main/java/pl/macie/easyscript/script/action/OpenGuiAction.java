package pl.macie.easyscript.script.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class OpenGuiAction implements Action {
    private final String title;
    private final int rows;
    private final SourceLocation source;

    public OpenGuiAction(String title, int rows, SourceLocation source) {
        this.title = title;
        this.rows = Math.max(1, Math.min(6, rows));
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        Inventory inventory = Bukkit.createInventory(player, rows * 9, ColorUtil.colorize(TextUtil.applyPlaceholders(title, context)));
        player.openInventory(inventory);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
