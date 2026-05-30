package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PermissionAttachmentAction implements Action {
    private static final Map<UUID, Map<String, PermissionAttachment>> ATTACHMENTS = new ConcurrentHashMap<>();

    private final String permission;
    private final boolean value;
    private final long durationTicks;
    private final SourceLocation source;

    public PermissionAttachmentAction(String permission, boolean value, long durationTicks, SourceLocation source) {
        this.permission = permission;
        this.value = value;
        this.durationTicks = Math.max(0L, durationTicks);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        Plugin plugin = context.getPlugin();
        String prepared = TextUtil.applyPlaceholders(permission, context).toLowerCase(java.util.Locale.ROOT);
        remove(player, prepared);
        PermissionAttachment attachment = player.addAttachment(plugin, prepared, value);
        ATTACHMENTS.computeIfAbsent(player.getUniqueId(), ignored -> new ConcurrentHashMap<>()).put(prepared, attachment);

        if (durationTicks > 0L) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> remove(player, prepared, attachment), durationTicks);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    static void remove(Player player, String permission) {
        Map<String, PermissionAttachment> playerAttachments = ATTACHMENTS.get(player.getUniqueId());
        if (playerAttachments == null) {
            return;
        }
        PermissionAttachment attachment = playerAttachments.remove(permission);
        if (attachment != null) {
            player.removeAttachment(attachment);
        }
    }

    private static void remove(Player player, String permission, PermissionAttachment expected) {
        Map<String, PermissionAttachment> playerAttachments = ATTACHMENTS.get(player.getUniqueId());
        if (playerAttachments == null || playerAttachments.get(permission) != expected) {
            return;
        }
        playerAttachments.remove(permission);
        player.removeAttachment(expected);
    }
}
