package pl.macie.easyscript.script.action;

import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class BossBarAction implements Action {
    private final String title;
    private final BarColor color;
    private final double progress;
    private final long durationTicks;
    private final SourceLocation source;

    public BossBarAction(String title, BarColor color, double progress, long durationTicks, SourceLocation source) {
        this.title = title;
        this.color = color;
        this.progress = Math.max(0.0, Math.min(1.0, progress));
        this.durationTicks = Math.max(1L, durationTicks);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        Plugin plugin = context.getPlugin();
        BossBar bossBar = plugin.getServer().createBossBar(
                ColorUtil.colorize(TextUtil.applyPlaceholders(title, context)),
                color,
                BarStyle.SOLID
        );
        bossBar.setProgress(progress);
        bossBar.addPlayer(player);
        plugin.getServer().getScheduler().runTaskLater(plugin, bossBar::removeAll, durationTicks);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
