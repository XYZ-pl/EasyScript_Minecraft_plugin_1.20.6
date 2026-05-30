package pl.macie.easyscript.script.action;

import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class PlayerWeatherAction implements Action {
    private final String weather;
    private final boolean reset;
    private final SourceLocation source;

    public PlayerWeatherAction(String weather, boolean reset, SourceLocation source) {
        this.weather = weather;
        this.reset = reset;
        this.source = source;
    }

    public static PlayerWeatherAction reset(SourceLocation source) {
        return new PlayerWeatherAction("", true, source);
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        if (reset) {
            player.resetPlayerWeather();
            return;
        }
        player.setPlayerWeather(weatherType(context));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private WeatherType weatherType(ScriptContext context) {
        String prepared = TextUtil.applyPlaceholders(weather, context).trim().toLowerCase(Locale.ROOT);
        return switch (prepared) {
            case "clear", "sun", "sunny" -> WeatherType.CLEAR;
            case "rain", "storm", "thunder", "thunderstorm" -> WeatherType.DOWNFALL;
            default -> throw new IllegalArgumentException("Player weather must be clear or rain");
        };
    }
}
