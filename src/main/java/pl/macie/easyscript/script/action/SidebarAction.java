package pl.macie.easyscript.script.action;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class SidebarAction implements Action {
    private final String title;
    private final String lines;
    private final SourceLocation source;

    public SidebarAction(String title, String lines, SourceLocation source) {
        this.title = title;
        this.lines = lines;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null || Bukkit.getScoreboardManager() == null) {
            return;
        }

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(
                "es",
                "dummy",
                ColorUtil.colorize(TextUtil.applyPlaceholders(title, context))
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        String[] preparedLines = TextUtil.applyPlaceholders(lines, context).split("\\|", -1);
        int score = preparedLines.length;
        for (int index = 0; index < preparedLines.length; index++) {
            String line = ColorUtil.colorize(preparedLines[index]);
            objective.getScore(uniqueLine(line, index)).setScore(score--);
        }
        player.setScoreboard(scoreboard);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private String uniqueLine(String line, int index) {
        return line + ChatColor.values()[index % ChatColor.values().length];
    }
}
