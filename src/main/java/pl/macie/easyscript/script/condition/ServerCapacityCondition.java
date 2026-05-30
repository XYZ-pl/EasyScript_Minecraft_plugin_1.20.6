package pl.macie.easyscript.script.condition;

import pl.macie.easyscript.script.model.ScriptContext;

public final class ServerCapacityCondition implements Condition {
    private final Mode mode;

    public ServerCapacityCondition(Mode mode) {
        this.mode = mode;
    }

    @Override
    public boolean test(ScriptContext context) {
        int online = context.getPlugin().getServer().getOnlinePlayers().size();
        int maxPlayers = context.getPlugin().getServer().getMaxPlayers();
        return switch (mode) {
            case FULL -> online >= maxPlayers;
            case EMPTY -> online == 0;
        };
    }

    @Override
    public boolean canRunAsynchronously() {
        return true;
    }

    public enum Mode {
        FULL,
        EMPTY
    }
}
