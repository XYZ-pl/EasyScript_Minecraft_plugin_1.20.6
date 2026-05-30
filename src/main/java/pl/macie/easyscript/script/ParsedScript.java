package pl.macie.easyscript.script;

import org.bukkit.boss.BarColor;
import pl.macie.easyscript.api.EasyScriptApi;
import pl.macie.easyscript.script.action.*;
import pl.macie.easyscript.script.condition.*;
import pl.macie.easyscript.script.model.ScriptCommand;
import pl.macie.easyscript.script.model.ScriptEvent;
import pl.macie.easyscript.script.model.TriggerType;
import pl.macie.easyscript.util.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ScriptParser {
    public ParsedScript parse(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        Map<String, String> options = readOptions(lines);
        List<ScriptCommand> commands = new ArrayList<>();
        List<ScriptEvent> events = new ArrayList<>();
        List<pl.macie.easyscript.script.model.ScriptFunction> functions = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        ScriptEvent currentEvent = null;
        CommandBuilder currentCommand = null;
        FunctionBuilder currentFunction = null;
        boolean inCommandTrigger = false;
        boolean inOptions = false;

        for (int index = 0; index < lines.size(); index++) {
            int lineNumber = index + 1;
            String rawLine = lines.get(index);
            String withoutComment = stripComment(rawLine).stripTrailing();
            if (withoutComment.isBlank()) {
                continue;
            }

            int indent = countIndent(withoutComment);
            if (inOptions && indent > 0) {
                continue;
            }
            if (indent == 0) {
                inOptions = false;
            }

            String line = withoutComment.trim();
            line = applyOptions(line, options);
            SourceLocation source = new SourceLocation(file, lineNumber);

            if (indent == 0 && line.endsWith(":")) {
                if (currentCommand != null) {
                    commands.add(currentCommand.build());
                    currentCommand = null;
                }
                if (currentFunction != null) {
                    functions.add(currentFunction.build());
                    currentFunction = null;
                }
                currentEvent = null;
                inCommandTrigger = false;

                String header = line.substring(0, line.length() - 1).trim();
                String lowerHeader = header.toLowerCase(Locale.ROOT);
                if (lowerHeader.equals("options")) {
                    inOptions = true;
                } else if (lowerHeader.equals("on load")) {
                    currentEvent = new ScriptEvent(TriggerType.LOAD, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on unload")) {
                    currentEvent = new ScriptEvent(TriggerType.UNLOAD, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on join")) {
                    currentEvent = new ScriptEvent(TriggerType.JOIN, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on quit") || lowerHeader.equals("on leave")) {
                    currentEvent = new ScriptEvent(TriggerType.QUIT, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on chat")) {
                    currentEvent = new ScriptEvent(TriggerType.CHAT, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on command")) {
                    currentEvent = new ScriptEvent(TriggerType.COMMAND, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on death")) {
                    currentEvent = new ScriptEvent(TriggerType.DEATH, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on respawn")) {
                    currentEvent = new ScriptEvent(TriggerType.RESPAWN, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on block break") || lowerHeader.equals("on break")) {
                    currentEvent = new ScriptEvent(TriggerType.BLOCK_BREAK, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on block place") || lowerHeader.equals("on place")) {
                    currentEvent = new ScriptEvent(TriggerType.BLOCK_PLACE, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on interact") || lowerHeader.equals("on right click")) {
                    currentEvent = new ScriptEvent(TriggerType.INTERACT, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on drop")) {
                    currentEvent = new ScriptEvent(TriggerType.DROP, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on pickup") || lowerHeader.equals("on pick up")) {
                    currentEvent = new ScriptEvent(TriggerType.PICKUP, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on damage")) {
                    currentEvent = new ScriptEvent(TriggerType.DAMAGE, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on inventory click")) {
                    currentEvent = new ScriptEvent(TriggerType.INVENTORY_CLICK, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on gui click")) {
                    currentEvent = new ScriptEvent(TriggerType.GUI_CLICK, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on item consume") || lowerHeader.equals("on consume")) {
                    currentEvent = new ScriptEvent(TriggerType.ITEM_CONSUME, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on projectile hit")) {
                    currentEvent = new ScriptEvent(TriggerType.PROJECTILE_HIT, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on entity death")) {
                    currentEvent = new ScriptEvent(TriggerType.ENTITY_DEATH, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on player move") || lowerHeader.equals("on move")) {
                    currentEvent = new ScriptEvent(TriggerType.PLAYER_MOVE, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on teleport")) {
                    currentEvent = new ScriptEvent(TriggerType.TELEPORT, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on login")) {
                    currentEvent = new ScriptEvent(TriggerType.LOGIN, source);
                    events.add(currentEvent);
                } else if (lowerHeader.equals("on server ping")) {
                    currentEvent = new ScriptEvent(TriggerType.SERVER_PING, source);
                    events.add(currentEvent);
                } else if (lowerHeader.startsWith("command ")) {
                    String commandName = normalizeCommandName(header.substring("command ".length()).trim());
                    currentCommand = new CommandBuilder(commandName, source);
                    if (commandName.isBlank()) {
                        errors.add(source.display() + ": command name cannot be empty");
                    }
                } else if (lowerHeader.startsWith("function ")) {
                    currentFunction = parseFunctionHeader(header, source, errors);
                } else {
                    errors.add(source.display() + ": unknown header '" + header + "'");
                }
                continue;
            }

            if (currentEvent != null) {
                ActionParseResult parsedAction = parseActionAt(lines, options, file, index, indent, errors);
                if (parsedAction.action() != null) {
                    currentEvent.addAction(parsedAction.action());
                }
                index = parsedAction.nextIndex() - 1;
                continue;
            }

            if (currentFunction != null) {
                ActionParseResult parsedAction = parseActionAt(lines, options, file, index, indent, errors);
                if (parsedAction.action() != null) {
                    currentFunction.actions.add(parsedAction.action());
                }
                index = parsedAction.nextIndex() - 1;
                continue;
            }

            if (currentCommand != null) {
                if (line.equalsIgnoreCase("trigger:")) {
                    inCommandTrigger = true;
                    continue;
                }

                if (!inCommandTrigger && parseCommandDirective(currentCommand, line, source, errors)) {
                    continue;
                }

                ActionParseResult parsedAction = parseActionAt(lines, options, file, index, indent, errors);
                if (parsedAction.action() != null) {
                    currentCommand.actions.add(parsedAction.action());
                }
                index = parsedAction.nextIndex() - 1;
                continue;
            }

            errors.add(source.display() + ": line is outside of a trigger or command");
        }

        if (currentCommand != null) {
            commands.add(currentCommand.build());
        }
        if (currentFunction != null) {
            functions.add(currentFunction.build());
        }

        return new ParsedScript(commands, events, functions, errors);
    }

    private Map<String, String> readOptions(List<String> lines) {
        Map<String, String> options = new HashMap<>();
        boolean inOptions = false;
        for (String rawLine : lines) {
            String withoutComment = stripComment(rawLine).stripTrailing();
            if (withoutComment.isBlank()) {
                continue;
            }

            int indent = countIndent(withoutComment);
            String line = withoutComment.trim();
            if (indent == 0) {
                inOptions = line.equalsIgnoreCase("options:");
                continue;
            }

            if (!inOptions) {
                continue;
            }

            int separator = line.indexOf(':');
            if (separator <= 0) {
                continue;
            }
            String key = line.substring(0, separator).trim().toLowerCase(Locale.ROOT);
            String value = stripOptionalQuotes(line.substring(separator + 1).trim());
            if (!key.isBlank()) {
                options.put(key, value);
            }
        }
        return options;
    }

    private String applyOptions(String line, Map<String, String> options) {
        String result = line;
        for (Map.Entry<String, String> option : options.entrySet()) {
            result = result.replace("{@" + option.getKey() + "}", option.getValue());
        }
        return result;
    }

    private FunctionBuilder parseFunctionHeader(String header, SourceLocation source, List<String> errors) {
        String functionText = header.substring("function ".length()).trim();
        String name = functionText;
        List<String> parameters = new ArrayList<>();

        int open = functionText.indexOf('(');
        int close = functionText.lastIndexOf(')');
        if (open >= 0 || close >= 0) {
            if (open <= 0 || close < open) {
                errors.add(source.display() + ": invalid function header");
                return new FunctionBuilder("invalid", List.of(), source);
            }
            name = functionText.substring(0, open).trim();
            String parameterText = functionText.substring(open + 1, close).trim();
            if (!parameterText.isBlank()) {
                for (String parameter : parameterText.split(",")) {
                    String normalized = parameter.trim().toLowerCase(Locale.ROOT);
                    if (!normalized.isBlank()) {
                        parameters.add(normalized);
                    }
                }
            }
        }

        if (!name.matches("[a-zA-Z][a-zA-Z0-9_.-]*")) {
            errors.add(source.display() + ": invalid function name '" + name + "'");
        }
        return new FunctionBuilder(name.toLowerCase(Locale.ROOT), parameters, source);
    }

    private boolean parseCommandDirective(CommandBuilder command, String line, SourceLocation source, List<String> errors) {
        String lower = line.toLowerCase(Locale.ROOT);
        if (lower.startsWith("aliases:")) {
            String aliasesText = line.substring("aliases:".length()).trim();
            if (!aliasesText.isBlank()) {
                for (String alias : aliasesText.split(",")) {
                    String normalizedAlias = normalizeCommandName(stripOptionalQuotes(alias.trim()));
                    if (!normalizedAlias.isBlank()) {
                        command.aliases.add(normalizedAlias);
                    }
                }
            }
            return true;
        }

        if (lower.startsWith("permission message:")) {
            command.permissionMessage = stripOptionalQuotes(line.substring("permission message:".length()).trim());
            return true;
        }

        if (lower.startsWith("permission:")) {
            command.permission = stripOptionalQuotes(line.substring("permission:".length()).trim());
            return true;
        }

        if (lower.startsWith("usage:")) {
            command.usage = stripOptionalQuotes(line.substring("usage:".length()).trim());
            return true;
        }

        if (lower.startsWith("description:")) {
            command.description = stripOptionalQuotes(line.substring("description:".length()).trim());
            return true;
        }

        if (lower.startsWith("arguments:")) {
            String argumentText = line.substring("arguments:".length()).trim();
            if (!argumentText.isBlank()) {
                for (String argumentType : argumentText.split(",")) {
                    String normalized = argumentType.trim().toLowerCase(Locale.ROOT);
                    if (!normalized.isBlank()) {
                        command.argumentTypes.add(normalized);
                    }
                }
            }
            return true;
        }

        if (lower.startsWith("cooldown message:")) {
            command.cooldownMessage = stripOptionalQuotes(line.substring("cooldown message:".length()).trim());
            return true;
        }

        if (lower.startsWith("cooldown:")) {
            String duration = line.substring("cooldown:".length()).trim();
            long ticks = TimeUtil.parseTicks(duration);
            if (ticks < 0L) {
                errors.add(source.display() + ": invalid cooldown duration '" + duration + "'");
            } else {
                command.cooldownTicks = ticks;
            }
            return true;
        }

        if (lower.endsWith(":")) {
            errors.add(source.display() + ": unknown command section '" + line + "'");
            return true;
        }

        return false;
    }

    private ActionParseResult parseActionAt(
            List<String> rawLines,
            Map<String, String> options,
            File file,
            int index,
            int indent,
            List<String> errors
    ) {
        PreparedLine preparedLine = prepareLine(rawLines.get(index), options);
        SourceLocation source = new SourceLocation(file, index + 1);
        String line = preparedLine.text();
        String lower = line.toLowerCase(Locale.ROOT);

        if (line.endsWith(":")) {
            String header = line.substring(0, line.length() - 1).trim();
            String lowerHeader = header.toLowerCase(Locale.ROOT);

            if (lowerHeader.startsWith("if ")) {
                Condition condition = parseCondition(header.substring("if ".length()).trim(), source, errors);
                BlockParseResult thenBlock = parseActionBlock(rawLines, options, file, index + 1, indent, errors);
                List<Action> elseActions = List.of();
                int nextIndex = thenBlock.nextIndex();
                int contentIndex = nextContentIndex(rawLines, nextIndex);
                if (contentIndex < rawLines.size()) {
                    PreparedLine maybeElse = prepareLine(rawLines.get(contentIndex), options);
                    if (maybeElse.indent() == indent && maybeElse.text().equalsIgnoreCase("else:")) {
                        BlockParseResult elseBlock = parseActionBlock(rawLines, options, file, contentIndex + 1, indent, errors);
                        elseActions = elseBlock.actions();
                        nextIndex = elseBlock.nextIndex();
                    }
                }
                return new ActionParseResult(condition == null ? null : new BlockIfAction(condition, thenBlock.actions(), elseActions, source), nextIndex);
            }

            if (lowerHeader.startsWith("after ") || lowerHeader.startsWith("wait ")) {
                String durationText = header.substring(lowerHeader.startsWith("after ") ? "after ".length() : "wait ".length()).trim();
                long ticks = TimeUtil.parseTicks(durationText);
                if (ticks < 0L) {
                    errors.add(source.display() + ": invalid delay duration '" + durationText + "'");
                    return new ActionParseResult(null, index + 1);
                }
                BlockParseResult block = parseActionBlock(rawLines, options, file, index + 1, indent, errors);
                return new ActionParseResult(new DelayAction(ticks, new ActionGroup(block.actions(), source), source), block.nextIndex());
            }

            if (lowerHeader.startsWith("repeat ")) {
                RepeatHeader repeatHeader = parseRepeatHeader(header, source, errors);
                BlockParseResult block = parseActionBlock(rawLines, options, file, index + 1, indent, errors);
                if (repeatHeader == null) {
                    return new ActionParseResult(null, block.nextIndex());
                }
                return new ActionParseResult(new LoopTimesAction(repeatHeader.times(), repeatHeader.periodTicks(), block.actions(), source), block.nextIndex());
            }

            if (lowerHeader.equals("loop all players")) {
                BlockParseResult block = parseActionBlock(rawLines, options, file, index + 1, indent, errors);
                return new ActionParseResult(new LoopPlayersAction(block.actions(), source), block.nextIndex());
            }

            if (lowerHeader.startsWith("loop list ")) {
                QuotedText list = readQuoted(header.substring("loop list ".length()).trim(), source, errors);
                BlockParseResult block = parseActionBlock(rawLines, options, file, index + 1, indent, errors);
                return new ActionParseResult(list == null ? null : new LoopListAction(list.text(), block.actions(), source), block.nextIndex());
            }

            if (lowerHeader.startsWith("loop ") && lowerHeader.endsWith(" times")) {
                int times = readInt(header.substring("loop ".length(), header.length() - " times".length()).trim(), 1);
                BlockParseResult block = parseActionBlock(rawLines, options, file, index + 1, indent, errors);
                return new ActionParseResult(new LoopTimesAction(times, 1L, block.actions(), source), block.nextIndex());
            }

            if (lowerHeader.startsWith("as player ")) {
                QuotedText playerName = readQuoted(header.substring("as player ".length()).trim(), source, errors);
                BlockParseResult block = parseActionBlock(rawLines, options, file, index + 1, indent, errors);
                return new ActionParseResult(playerName == null ? null : new TargetPlayerAction(playerName.text(), block.actions(), source), block.nextIndex());
            }

            if (lower.equals("else:")) {
                errors.add(source.display() + ": else must follow an if block");
                return new ActionParseResult(null, index + 1);
            }
        }

        return new ActionParseResult(parseAction(line, source, errors), index + 1);
    }

    private BlockParseResult parseActionBlock(
            List<String> rawLines,
            Map<String, String> options,
            File file,
            int startIndex,
            int parentIndent,
            List<String> errors
    ) {
        List<Action> actions = new ArrayList<>();
        int index = startIndex;
        while (index < rawLines.size()) {
            PreparedLine preparedLine = prepareLine(rawLines.get(index), options);
            if (preparedLine.text().isBlank()) {
                index++;
                continue;
            }
            if (preparedLine.indent() <= parentIndent) {
                break;
            }

            ActionParseResult parsedAction = parseActionAt(rawLines, options, file, index, preparedLine.indent(), errors);
            if (parsedAction.action() != null) {
                actions.add(parsedAction.action());
            }
            index = parsedAction.nextIndex();
        }
        return new BlockParseResult(actions, index);
    }

    private PreparedLine prepareLine(String rawLine, Map<String, String> options) {
        String withoutComment = stripComment(rawLine).stripTrailing();
        if (withoutComment.isBlank()) {
            return new PreparedLine(0, "");
        }
        return new PreparedLine(countIndent(withoutComment), applyOptions(withoutComment.trim(), options));
    }

    private int nextContentIndex(List<String> rawLines, int startIndex) {
        int index = startIndex;
        while (index < rawLines.size()) {
            if (!stripComment(rawLines.get(index)).stripTrailing().isBlank()) {
                return index;
            }
            index++;
        }
        return index;
    }

    private Action parseAction(String line, SourceLocation source, List<String> errors) {
        String lower = line.toLowerCase(Locale.ROOT);

        if (lower.startsWith("if ")) {
            return parseIfAction(line, source, errors);
        }

        if (lower.startsWith("after ") || lower.startsWith("wait ")) {
            return parseDelayAction(line, source, errors);
        }

        if (lower.startsWith("repeat ")) {
            return parseRepeatAction(line, source, errors);
        }

        if (lower.startsWith("call ")) {
            return parseCallFunctionAction(line, source, errors);
        }

        if (lower.equals("cancel event")) {
            return new CancelEventAction(source);
        }

        if (lower.startsWith("log ")) {
            QuotedText quotedText = readQuoted(line.substring("log ".length()).trim(), source, errors);
            return quotedText == null ? null : new LogAction(quotedText.text(), source);
        }

        if (lower.startsWith("send nearby ")) {
            return parseNearbyMessageAction(line, source, errors);
        }

        if (lower.startsWith("send resource pack ")) {
            return parseResourcePackAction(line, source, errors);
        }

        if (lower.startsWith("send ")) {
            return parseSendAction(line, source, errors);
        }

        if (lower.startsWith("broadcast ")) {
            QuotedText quotedText = readQuoted(line.substring("broadcast ".length()).trim(), source, errors);
            return quotedText == null ? null : new BroadcastAction(quotedText.text(), source);
        }

        if (lower.startsWith("actionbar ")) {
            QuotedText quotedText = readQuoted(line.substring("actionbar ".length()).trim(), source, errors);
            if (quotedText == null) {
                return null;
            }
            if (quotedText.tail().trim().equalsIgnoreCase("to all")) {
                return new ActionBarAllAction(quotedText.text(), source);
            }
            if (!quotedText.tail().trim().equalsIgnoreCase("to player")) {
                errors.add(source.display() + ": actionbar action must end with 'to player' or 'to all'");
                return null;
            }
            return new ActionBarAction(quotedText.text(), source);
        }

        if (lower.equals("clear chat of player")) {
            return new ClearChatAction(ClearChatAction.Target.PLAYER, source);
        }

        if (lower.equals("clear chat of all")) {
            return new ClearChatAction(ClearChatAction.Target.ALL, source);
        }

        if (lower.startsWith("title ")) {
            return parseTitleAction(line, source, errors);
        }

        if (lower.startsWith("stop sound ")) {
            QuotedText quotedText = readQuoted(line.substring("stop sound ".length()).trim(), source, errors);
            if (quotedText == null) {
                return null;
            }
            if (!quotedText.tail().trim().equalsIgnoreCase("for player")) {
                errors.add(source.display() + ": stop sound action must end with 'for player'");
                return null;
            }
            return new StopSoundAction(quotedText.text(), source);
        }

        if (lower.startsWith("sound ")) {
            return parseSoundAction(line, source, errors);
        }

        if (lower.startsWith("particle ")) {
            return parseParticleAction(line, source, errors);
        }

        if (lower.startsWith("effect ")) {
            return parsePotionEffectAction(line, source, errors);
        }

        if (lower.startsWith("give permission ")) {
            return parsePermissionAttachmentAction(line, true, source, errors);
        }

        if (lower.startsWith("deny permission ")) {
            return parsePermissionAttachmentAction(line, false, source, errors);
        }

        if (lower.startsWith("remove permission ")) {
            return parseRemovePermissionAction(line, source, errors);
        }

        if (lower.startsWith("give ")) {
            return parseGiveAction(line, source, errors);
        }

        if (lower.startsWith("remove ") && lower.contains(" from variable ")) {
            return parseRemoveVariableAction(line, source, errors);
        }

        if (lower.startsWith("subtract ")) {
            return parseSubtractVariableAction(line, source, errors);
        }

        if (lower.startsWith("remove ")) {
            return parseRemoveItemAction(line, source, errors);
        }

        if (lower.equals("clear inventory of player")) {
            return new ClearInventoryAction(source);
        }

        if (lower.startsWith("open gui ")) {
            return parseOpenGuiAction(line, source, errors);
        }

        if (lower.startsWith("set gui slot ")) {
            return parseSetGuiSlotAction(line, source, errors);
        }

        if (lower.startsWith("open ") && lower.endsWith(" to player")) {
            return parseOpenInventoryAction(line, source, errors);
        }

        if (lower.equals("close inventory of player")) {
            return new CloseInventoryAction(source);
        }

        if (lower.startsWith("set held slot to ")) {
            return new HeldSlotAction(readInt(line.substring("set held slot to ".length()).trim(), 1), source);
        }

        if (lower.startsWith("set held item amount to ")) {
            return new HeldItemAmountAction(readInt(line.substring("set held item amount to ".length()).trim(), 1), source);
        }

        if (lower.startsWith("set helmet to ")) {
            return parseArmorAction(line, "helmet", source, errors);
        }

        if (lower.startsWith("set chestplate to ")) {
            return parseArmorAction(line, "chestplate", source, errors);
        }

        if (lower.startsWith("set leggings to ")) {
            return parseArmorAction(line, "leggings", source, errors);
        }

        if (lower.startsWith("set boots to ")) {
            return parseArmorAction(line, "boots", source, errors);
        }

        if (lower.startsWith("set offhand to ")) {
            return parseArmorAction(line, "offhand", source, errors);
        }

        if (lower.equals("clear armor of player")) {
            return new ClearArmorAction(source);
        }

        if (lower.equals("heal player")) {
            return new HealAction(null, source);
        }

        if (lower.startsWith("set health to ")) {
            return new SetHealthAction(readDouble(line.substring("set health to ".length()).trim(), 20.0), source);
        }

        if (lower.startsWith("set max health to ")) {
            return new SetMaxHealthAction(readDouble(line.substring("set max health to ".length()).trim(), 20.0), source);
        }

        if (lower.startsWith("heal player by ")) {
            return new HealAction(readDouble(line.substring("heal player by ".length()).trim(), 0.0), source);
        }

        if (lower.startsWith("damage player by ")) {
            return new DamagePlayerAction(readDouble(line.substring("damage player by ".length()).trim(), 0.0), source);
        }

        if (lower.equals("feed player")) {
            return new FoodAction(20, source);
        }

        if (lower.startsWith("set food to ")) {
            return new FoodAction(readInt(line.substring("set food to ".length()).trim(), 20), source);
        }

        if (lower.startsWith("set saturation to ")) {
            return new SaturationAction((float) readDouble(line.substring("set saturation to ".length()).trim(), 5.0), source);
        }

        if (lower.startsWith("set exhaustion to ")) {
            return new ExhaustionAction((float) readDouble(line.substring("set exhaustion to ".length()).trim(), 0.0), source);
        }

        if (lower.startsWith("set xp progress to ")) {
            return new ExperienceProgressAction((float) readDouble(line.substring("set xp progress to ".length()).trim(), 0.0), source);
        }

        if (lower.equals("clear potion effects of player")) {
            return new ClearPotionEffectsAction(source);
        }

        if (lower.startsWith("set op to ")) {
            return new SetOperatorAction(readBoolean(line.substring("set op to ".length()).trim()), source);
        }

        if (lower.startsWith("gamemode ")) {
            return parseGameModeAction(line, source, errors);
        }

        if (lower.startsWith("teleport player to ")) {
            return parseTeleportAction(line, source, errors);
        }

        if (lower.startsWith("set respawn point to ")) {
            return parseRespawnPointAction(line, source, errors);
        }

        if (lower.equals("clear respawn point")) {
            return new ClearRespawnPointAction(source);
        }

        if (lower.startsWith("set compass target to ")) {
            return new CompassTargetAction(stripOptionalQuotes(line.substring("set compass target to ".length()).trim()), source);
        }

        if (lower.startsWith("set player time to ")) {
            return new PlayerTimeAction(stripOptionalQuotes(line.substring("set player time to ".length()).trim()), false, source);
        }

        if (lower.equals("reset player time")) {
            return PlayerTimeAction.reset(source);
        }

        if (lower.startsWith("set player weather to ")) {
            return new PlayerWeatherAction(stripOptionalQuotes(line.substring("set player weather to ".length()).trim()), false, source);
        }

        if (lower.equals("reset player weather")) {
            return PlayerWeatherAction.reset(source);
        }

        if (lower.startsWith("kick player ")) {
            QuotedText quotedText = readQuoted(line.substring("kick player ".length()).trim(), source, errors);
            return quotedText == null ? null : new KickAction(quotedText.text(), source);
        }

        if (lower.startsWith("kick all players ")) {
            QuotedText quotedText = readQuoted(line.substring("kick all players ".length()).trim(), source, errors);
            return quotedText == null ? null : new KickAllAction(quotedText.text(), source);
        }

        if (lower.equals("lightning at player")) {
            return new LightningAction(false, source);
        }

        if (lower.equals("lightning effect at player")) {
            return new LightningAction(true, source);
        }

        if (lower.startsWith("spawn ")) {
            return parseSpawnEntityAction(line, source, errors);
        }

        if (lower.startsWith("drop ")) {
            return parseDropItemAction(line, source, errors);
        }

        if (lower.startsWith("launch ")) {
            return parseLaunchProjectileAction(line, source, errors);
        }

        if (lower.startsWith("set velocity to ")) {
            QuotedText quotedText = readQuoted(line.substring("set velocity to ".length()).trim(), source, errors);
            return quotedText == null ? null : new VelocityAction(quotedText.text(), false, source);
        }

        if (lower.startsWith("push player by ")) {
            QuotedText quotedText = readQuoted(line.substring("push player by ".length()).trim(), source, errors);
            return quotedText == null ? null : new VelocityAction(quotedText.text(), true, source);
        }

        if (lower.startsWith("set block at ")) {
            return parseSetBlockAction(line, source, errors);
        }

        if (lower.startsWith("break block ")) {
            return parseBreakBlockAction(line, source, errors);
        }

        if (lower.startsWith("explosion at ")) {
            return parseExplosionAction(line, source, errors);
        }

        if (lower.startsWith("set time to ")) {
            return new WorldTimeAction(stripOptionalQuotes(line.substring("set time to ".length()).trim()), source);
        }

        if (lower.startsWith("set weather to ")) {
            return parseWeatherAction(line, source, errors);
        }

        if (lower.startsWith("set worldborder ")) {
            return parseWorldBorderAction(line, source, errors);
        }

        if (lower.equals("save world")) {
            return new SaveWorldAction(source);
        }

        if (lower.startsWith("save world seed to variable ")) {
            QuotedText quotedText = readQuoted(line.substring("save world seed to variable ".length()).trim(), source, errors);
            return quotedText == null ? null : new SaveWorldSeedAction(quotedText.text(), source);
        }

        if (lower.startsWith("bossbar ")) {
            return parseBossBarAction(line, source, errors);
        }

        if (lower.startsWith("sidebar title ")) {
            return parseSidebarAction(line, source, errors);
        }

        if (lower.equals("clear sidebar of player")) {
            return new ClearSidebarAction(source);
        }

        if (lower.startsWith("tab header ")) {
            return parseTabHeaderFooterAction(line, source, errors);
        }

        if (lower.startsWith("set display name to ")) {
            QuotedText quotedText = readQuoted(line.substring("set display name to ".length()).trim(), source, errors);
            return quotedText == null ? null : new PlayerNameAction(PlayerNameAction.Type.DISPLAY, quotedText.text(), source);
        }

        if (lower.equals("reset display name")) {
            return new ResetPlayerNameAction(ResetPlayerNameAction.Type.DISPLAY, source);
        }

        if (lower.startsWith("set tab name to ")) {
            QuotedText quotedText = readQuoted(line.substring("set tab name to ".length()).trim(), source, errors);
            return quotedText == null ? null : new PlayerNameAction(PlayerNameAction.Type.TAB, quotedText.text(), source);
        }

        if (lower.equals("reset tab name")) {
            return new ResetPlayerNameAction(ResetPlayerNameAction.Type.TAB, source);
        }

        if (lower.startsWith("add xp ")) {
            return new ExperienceAction(ExperienceAction.Mode.ADD_XP, readInt(readBetween(line, "add xp ", " to player"), 0), source);
        }

        if (lower.startsWith("set level to ")) {
            return new ExperienceAction(ExperienceAction.Mode.SET_LEVEL, readInt(line.substring("set level to ".length()).trim(), 0), source);
        }

        if (lower.startsWith("set flight to ")) {
            return FlightAction.toggle(FlightAction.Mode.ALLOW_FLIGHT, readBoolean(line.substring("set flight to ".length()).trim()), source);
        }

        if (lower.startsWith("set flying to ")) {
            return FlightAction.toggle(FlightAction.Mode.FLYING, readBoolean(line.substring("set flying to ".length()).trim()), source);
        }

        if (lower.startsWith("set walk speed to ")) {
            return FlightAction.speed(FlightAction.Mode.WALK_SPEED, (float) readDouble(line.substring("set walk speed to ".length()).trim(), 0.2), source);
        }

        if (lower.equals("reset walk speed")) {
            return new ResetSpeedAction(ResetSpeedAction.Type.WALK, source);
        }

        if (lower.startsWith("set fly speed to ")) {
            return FlightAction.speed(FlightAction.Mode.FLY_SPEED, (float) readDouble(line.substring("set fly speed to ".length()).trim(), 0.1), source);
        }

        if (lower.equals("reset fly speed")) {
            return new ResetSpeedAction(ResetSpeedAction.Type.FLY, source);
        }

        if (lower.startsWith("set fire ticks to ")) {
            return new FireAction(readInt(line.substring("set fire ticks to ".length()).trim(), 0), source);
        }

        if (lower.equals("extinguish player")) {
            return new FireAction(0, source);
        }

        if (lower.startsWith("set freeze ticks to ")) {
            return new FreezeTicksAction(readInt(line.substring("set freeze ticks to ".length()).trim(), 0), source);
        }

        if (lower.equals("clear freeze ticks")) {
            return new FreezeTicksAction(0, source);
        }

        if (lower.startsWith("set remaining air to ")) {
            return new RemainingAirAction(readInt(line.substring("set remaining air to ".length()).trim(), 300), source);
        }

        if (lower.startsWith("set arrows in body to ")) {
            return new ArrowsInBodyAction(readInt(line.substring("set arrows in body to ".length()).trim(), 0), source);
        }

        if (lower.startsWith("set glowing to ")) {
            return new EntityToggleAction(EntityToggleAction.Mode.GLOWING, readBoolean(line.substring("set glowing to ".length()).trim()), source);
        }

        if (lower.startsWith("set invulnerable to ")) {
            return new EntityToggleAction(EntityToggleAction.Mode.INVULNERABLE, readBoolean(line.substring("set invulnerable to ".length()).trim()), source);
        }

        if (lower.startsWith("set silent to ")) {
            return new EntityToggleAction(EntityToggleAction.Mode.SILENT, readBoolean(line.substring("set silent to ".length()).trim()), source);
        }

        if (lower.startsWith("set gravity to ")) {
            return new EntityToggleAction(EntityToggleAction.Mode.GRAVITY, readBoolean(line.substring("set gravity to ".length()).trim()), source);
        }

        if (lower.startsWith("set visual fire to ")) {
            return new EntityToggleAction(EntityToggleAction.Mode.VISUAL_FIRE, readBoolean(line.substring("set visual fire to ".length()).trim()), source);
        }

        if (lower.equals("swing main hand")) {
            return new SwingHandAction(SwingHandAction.Hand.MAIN, source);
        }

        if (lower.equals("swing off hand")) {
            return new SwingHandAction(SwingHandAction.Hand.OFF, source);
        }

        if (lower.startsWith("save location to variable ")) {
            QuotedText quotedText = readQuoted(line.substring("save location to variable ".length()).trim(), source, errors);
            return quotedText == null ? null : new CopyLocationAction(quotedText.text(), false, source);
        }

        if (lower.startsWith("save exact location to variable ")) {
            QuotedText quotedText = readQuoted(line.substring("save exact location to variable ".length()).trim(), source, errors);
            return quotedText == null ? null : new CopyLocationAction(quotedText.text(), true, source);
        }

        if (lower.startsWith("send resource pack ")) {
            QuotedText quotedText = readQuoted(line.substring("send resource pack ".length()).trim(), source, errors);
            if (quotedText == null) {
                return null;
            }
            if (!quotedText.tail().trim().equalsIgnoreCase("to player")) {
                errors.add(source.display() + ": resource pack action must end with 'to player'");
                return null;
            }
            return new ResourcePackAction(quotedText.text(), source);
        }

        if (lower.startsWith("set whitelist to ")) {
            return new WhitelistAction(readBoolean(line.substring("set whitelist to ".length()).trim()), source);
        }

        if (lower.startsWith("rename held item to ")) {
            QuotedText quotedText = readQuoted(line.substring("rename held item to ".length()).trim(), source, errors);
            return quotedText == null ? null : new RenameHeldItemAction(quotedText.text(), source);
        }

        if (lower.startsWith("set held item unbreakable to ")) {
            return new HeldItemUnbreakableAction(readBoolean(line.substring("set held item unbreakable to ".length()).trim()), source);
        }

        if (lower.startsWith("set custom model data to ")) {
            return new HeldItemCustomModelDataAction(readInt(line.substring("set custom model data to ".length()).trim(), 0), source);
        }

        if (lower.equals("clear custom model data of held item")) {
            return new HeldItemCustomModelDataAction(null, source);
        }

        if (lower.startsWith("add item flag ")) {
            return parseHeldItemFlagAction(line, true, source, errors);
        }

        if (lower.startsWith("remove item flag ")) {
            return parseHeldItemFlagAction(line, false, source, errors);
        }

        if (lower.equals("clear item flags of held item")) {
            return new ClearHeldItemFlagsAction(source);
        }

        if (lower.startsWith("set lore of held item to ")) {
            QuotedText quotedText = readQuoted(line.substring("set lore of held item to ".length()).trim(), source, errors);
            return quotedText == null ? null : new LoreHeldItemAction(quotedText.text(), source);
        }

        if (lower.startsWith("enchant held item with ")) {
            return parseEnchantHeldItemAction(line, source, errors);
        }

        if (lower.equals("repair held item")) {
            return new RepairHeldItemAction(source);
        }

        if (lower.startsWith("set join message to ")) {
            QuotedText quotedText = readQuoted(line.substring("set join message to ".length()).trim(), source, errors);
            return quotedText == null ? null : new EventMessageAction(EventMessageAction.Type.JOIN, quotedText.text(), source);
        }

        if (lower.startsWith("set quit message to ")) {
            QuotedText quotedText = readQuoted(line.substring("set quit message to ".length()).trim(), source, errors);
            return quotedText == null ? null : new EventMessageAction(EventMessageAction.Type.QUIT, quotedText.text(), source);
        }

        if (lower.startsWith("set death message to ")) {
            QuotedText quotedText = readQuoted(line.substring("set death message to ".length()).trim(), source, errors);
            return quotedText == null ? null : new EventMessageAction(EventMessageAction.Type.DEATH, quotedText.text(), source);
        }

        if (lower.startsWith("set variable ")) {
            Action transformed = parseTransformedVariableAction(line, source, errors);
            if (transformed != null) {
                return transformed;
            }
            return parseSetVariableAction(line, source, errors);
        }

        if (lower.startsWith("add ") && lower.contains(" to list ")) {
            return parseAddToListAction(line, source, errors);
        }

        if (lower.startsWith("add ")) {
            return parseAddVariableAction(line, source, errors);
        }

        if (lower.startsWith("remove ") && lower.contains(" from list ")) {
            return parseRemoveFromListAction(line, source, errors);
        }

        if (lower.startsWith("remove ")) {
            Action removeVariable = parseRemoveVariableAction(line, source, errors);
            if (removeVariable != null) {
                return removeVariable;
            }
        }

        if (lower.startsWith("clear list ")) {
            QuotedText quotedText = readQuoted(line.substring("clear list ".length()).trim(), source, errors);
            return quotedText == null ? null : new ClearListAction(quotedText.text(), source);
        }

        if (lower.startsWith("delete variable ")) {
            QuotedText quotedText = readQuoted(line.substring("delete variable ".length()).trim(), source, errors);
            return quotedText == null ? null : new DeleteVariableAction(quotedText.text(), source);
        }

        if (lower.startsWith("create folder ")) {
            QuotedText quotedText = readQuoted(line.substring("create folder ".length()).trim(), source, errors);
            return quotedText == null ? null : new CreateFolderAction(quotedText.text(), source);
        }

        if (lower.startsWith("create file ")) {
            QuotedText quotedText = readQuoted(line.substring("create file ".length()).trim(), source, errors);
            return quotedText == null ? null : new CreateFileAction(quotedText.text(), source);
        }

        if (lower.startsWith("write file ")) {
            return parseWriteFileAction(line, source, errors, false);
        }

        if (lower.startsWith("overwrite file ")) {
            return parseWriteFileAction(line, source, errors, false);
        }

        if (lower.startsWith("append file ")) {
            return parseWriteFileAction(line, source, errors, true);
        }

        if (lower.startsWith("read file ")) {
            return parseReadFileAction(line, source, errors);
        }

        if (lower.startsWith("send file ")) {
            return parseSendFileAction(line, source, errors);
        }

        if (lower.startsWith("list folder ")) {
            return parseListFolderAction(line, source, errors);
        }

        if (lower.startsWith("delete file ")) {
            QuotedText quotedText = readQuoted(line.substring("delete file ".length()).trim(), source, errors);
            return quotedText == null ? null : new DeletePathAction(quotedText.text(), source);
        }

        if (lower.startsWith("delete folder ")) {
            QuotedText quotedText = readQuoted(line.substring("delete folder ".length()).trim(), source, errors);
            return quotedText == null ? null : new DeletePathAction(quotedText.text(), source);
        }

        if (lower.startsWith("console ")) {
            QuotedText quotedText = readQuoted(line.substring("console ".length()).trim(), source, errors);
            return quotedText == null ? null : new CommandAction(CommandAction.Target.CONSOLE, quotedText.text(), source);
        }

        if (lower.startsWith("execute console ")) {
            QuotedText quotedText = readQuoted(line.substring("execute console ".length()).trim(), source, errors);
            return quotedText == null ? null : new CommandAction(CommandAction.Target.CONSOLE, quotedText.text(), source);
        }

        if (lower.startsWith("execute player ")) {
            QuotedText quotedText = readQuoted(line.substring("execute player ".length()).trim(), source, errors);
            return quotedText == null ? null : new CommandAction(CommandAction.Target.PLAYER, quotedText.text(), source);
        }

        if (lower.startsWith("set chat format to ")) {
            QuotedText quotedText = readQuoted(line.substring("set chat format to ".length()).trim(), source, errors);
            return quotedText == null ? null : new ChatFormatAction(quotedText.text(), source);
        }

        if (lower.startsWith("set format to ")) {
            QuotedText quotedText = readQuoted(line.substring("set format to ".length()).trim(), source, errors);
            return quotedText == null ? null : new ChatFormatAction(quotedText.text(), source);
        }

        if (lower.startsWith("set message to ")) {
            QuotedText quotedText = readQuoted(line.substring("set message to ".length()).trim(), source, errors);
            return quotedText == null ? null : new ChatMessageAction(quotedText.text(), source);
        }

        Action customAction = EasyScriptApi.registry().parseAction(line, source, errors);
        if (customAction != null) {
            return customAction;
        }

        errors.add(source.display() + ": unknown action '" + line + "'");
        return null;
    }

    private Action parseSendAction(String line, SourceLocation source, List<String> errors) {
        QuotedText quotedText = readQuoted(line.substring("send ".length()).trim(), source, errors);
        if (quotedText == null) {
            return null;
        }

        String targetText = quotedText.tail().trim();
        String target = targetText.toLowerCase(Locale.ROOT);
        if (target.equals("to player")) {
            return new MessageAction(MessageAction.Target.PLAYER, quotedText.text(), source);
        }
        if (target.equals("to sender")) {
            return new MessageAction(MessageAction.Target.SENDER, quotedText.text(), source);
        }
        if (target.equals("to console")) {
            return new MessageAction(MessageAction.Target.CONSOLE, quotedText.text(), source);
        }

        if (target.startsWith("to players with permission ")) {
            QuotedText permission = readQuoted(targetText.substring("to players with permission ".length()).trim(), source, errors);
            return permission == null ? null : new SendPermissionAction(quotedText.text(), permission.text(), source);
        }

        errors.add(source.display() + ": send action must end with 'to player', 'to sender', 'to console' or 'to players with permission \"permission\"'");
        return null;
    }

    private Action parseNearbyMessageAction(String line, SourceLocation source, List<String> errors) {
        QuotedText message = readQuoted(line.substring("send nearby ".length()).trim(), source, errors);
        if (message == null) {
            return null;
        }
        double radius = readDoubleOption(message.tail(), "radius", 10.0);
        boolean includeSelf = readBooleanOption(message.tail(), "self", true);
        return new NearbyMessageAction(message.text(), radius, includeSelf, source);
    }

    private Action parseResourcePackAction(String line, SourceLocation source, List<String> errors) {
        QuotedText quotedText = readQuoted(line.substring("send resource pack ".length()).trim(), source, errors);
        if (quotedText == null) {
            return null;
        }
        if (!quotedText.tail().trim().equalsIgnoreCase("to player")) {
            errors.add(source.display() + ": resource pack action must end with 'to player'");
            return null;
        }
        return new ResourcePackAction(quotedText.text(), source);
    }

    private Action parsePermissionAttachmentAction(String line, boolean value, SourceLocation source, List<String> errors) {
        String prefix = value ? "give permission " : "deny permission ";
        QuotedText permission = readQuoted(line.substring(prefix.length()).trim(), source, errors);
        if (permission == null) {
            return null;
        }

        String tail = permission.tail().trim();
        String lowerTail = tail.toLowerCase(Locale.ROOT);
        if (!lowerTail.startsWith("to player")) {
            errors.add(source.display() + ": permission action must use 'to player'");
            return null;
        }

        long duration = 0L;
        String afterTarget = tail.substring("to player".length()).trim();
        if (afterTarget.toLowerCase(Locale.ROOT).startsWith("for ")) {
            String durationText = afterTarget.substring("for ".length()).trim();
            duration = TimeUtil.parseTicks(durationText);
            if (duration < 0L) {
                errors.add(source.display() + ": invalid permission duration '" + durationText + "'");
                return null;
            }
        }
        return new PermissionAttachmentAction(permission.text(), value, duration, source);
    }

    private Action parseRemovePermissionAction(String line, SourceLocation source, List<String> errors) {
        QuotedText permission = readQuoted(line.substring("remove permission ".length()).trim(), source, errors);
        if (permission == null) {
            return null;
        }

        if (!permission.tail().trim().equalsIgnoreCase("from player")) {
            errors.add(source.display() + ": remove permission action must end with 'from player'");
            return null;
        }
        return new RemovePermissionAttachmentAction(permission.text(), source);
    }

    private Action parseOpenInventoryAction(String line, SourceLocation source, List<String> errors) {
        String type = line.substring("open ".length(), line.length() - " to player".length()).trim().toLowerCase(Locale.ROOT);
        return switch (type) {
            case "enderchest", "ender chest" -> new OpenInventoryAction(OpenInventoryAction.Type.ENDER_CHEST, source);
            case "workbench", "crafting", "crafting table" -> new OpenInventoryAction(OpenInventoryAction.Type.WORKBENCH, source);
            case "enchanting", "enchanting table" -> new OpenInventoryAction(OpenInventoryAction.Type.ENCHANTING, source);
            case "inventory", "player inventory" -> new OpenInventoryAction(OpenInventoryAction.Type.PLAYER_INVENTORY, source);
            default -> {
                errors.add(source.display() + ": open action must use enderchest, workbench, enchanting or inventory");
                yield null;
            }
        };
    }

    private Action parseOpenGuiAction(String line, SourceLocation source, List<String> errors) {
        QuotedText title = readQuoted(line.substring("open gui ".length()).trim(), source, errors);
        if (title == null) {
            return null;
        }
        String tail = title.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).endsWith("to player")) {
            errors.add(source.display() + ": open gui action must end with 'to player'");
            return null;
        }
        int rows = readIntOption(tail, "rows", 3);
        return new OpenGuiAction(title.text(), rows, source);
    }

    private Action parseSetGuiSlotAction(String line, SourceLocation source, List<String> errors) {
        String rest = line.substring("set gui slot ".length()).trim();
        int toIndex = rest.toLowerCase(Locale.ROOT).indexOf(" to ");
        if (toIndex < 0) {
            errors.add(source.display() + ": set gui slot action must use 'to'");
            return null;
        }

        int slot = readInt(rest.substring(0, toIndex).trim(), 0);
        QuotedText material = readQuoted(rest.substring(toIndex + " to ".length()).trim(), source, errors);
        if (material == null) {
            return null;
        }

        String tail = material.tail().trim();
        String name = "";
        String lore = "";
        int namedIndex = tail.toLowerCase(Locale.ROOT).indexOf("named ");
        if (namedIndex >= 0) {
            QuotedText nameText = readQuoted(tail.substring(namedIndex + "named ".length()).trim(), source, errors);
            if (nameText == null) {
                return null;
            }
            name = nameText.text();
            tail = nameText.tail().trim();
        }
        int loreIndex = tail.toLowerCase(Locale.ROOT).indexOf("lore ");
        if (loreIndex >= 0) {
            QuotedText loreText = readQuoted(tail.substring(loreIndex + "lore ".length()).trim(), source, errors);
            if (loreText == null) {
                return null;
            }
            lore = loreText.text();
        }
        return new SetGuiSlotAction(slot, material.text(), name, lore, source);
    }

    private Action parseArmorAction(String line, String slot, SourceLocation source, List<String> errors) {
        String prefix = "set " + slot + " to ";
        QuotedText item = readQuoted(line.substring(prefix.length()).trim(), source, errors);
        return item == null ? null : new ArmorAction(slot, item.text(), source);
    }

    private Action parseDropItemAction(String line, SourceLocation source, List<String> errors) {
        QuotedText item = readQuoted(line.substring("drop ".length()).trim(), source, errors);
        if (item == null) {
            return null;
        }

        String tail = item.tail().trim();
        String lowerTail = tail.toLowerCase(Locale.ROOT);
        int atIndex = lowerTail.lastIndexOf(" at ");
        if (atIndex < 0) {
            errors.add(source.display() + ": drop action must use 'at player' or 'at \"world,x,y,z\"'");
            return null;
        }

        int amount = readIntOption(tail.substring(0, atIndex), "amount", 1);
        String target = stripOptionalQuotes(tail.substring(atIndex + " at ".length()).trim());
        return new DropItemAction(item.text(), amount, target, source);
    }

    private Action parseLaunchProjectileAction(String line, SourceLocation source, List<String> errors) {
        QuotedText projectile = readQuoted(line.substring("launch ".length()).trim(), source, errors);
        if (projectile == null) {
            return null;
        }
        if (!projectile.tail().trim().equalsIgnoreCase("from player")) {
            errors.add(source.display() + ": launch action must end with 'from player'");
            return null;
        }
        return new LaunchProjectileAction(projectile.text(), source);
    }

    private Action parseSetBlockAction(String line, SourceLocation source, List<String> errors) {
        String rest = line.substring("set block at ".length()).trim();
        String lowerRest = rest.toLowerCase(Locale.ROOT);
        int toIndex = lowerRest.indexOf(" to ");
        if (toIndex < 0) {
            errors.add(source.display() + ": set block action must use 'to'");
            return null;
        }

        String target = stripOptionalQuotes(rest.substring(0, toIndex).trim());
        QuotedText material = readQuoted(rest.substring(toIndex + " to ".length()).trim(), source, errors);
        return material == null ? null : new SetBlockAction(target, material.text(), source);
    }

    private Action parseBreakBlockAction(String line, SourceLocation source, List<String> errors) {
        String lower = line.toLowerCase(Locale.ROOT);
        boolean naturally = lower.startsWith("break block naturally at ");
        String prefix = naturally ? "break block naturally at " : "break block at ";
        if (!lower.startsWith(prefix)) {
            errors.add(source.display() + ": break block action must use 'break block at ...'");
            return null;
        }
        String target = stripOptionalQuotes(line.substring(prefix.length()).trim());
        return new BreakBlockAction(target, naturally, source);
    }

    private Action parseExplosionAction(String line, SourceLocation source, List<String> errors) {
        String rest = line.substring("explosion at ".length()).trim();
        String lowerRest = rest.toLowerCase(Locale.ROOT);
        int powerIndex = lowerRest.indexOf(" power ");
        String target = powerIndex < 0 ? rest : rest.substring(0, powerIndex).trim();
        String options = powerIndex < 0 ? "" : rest.substring(powerIndex + 1).trim();
        float power = readFloatOption(options, "power", 2.0F);
        boolean fire = readBooleanOption(options, "fire", false);
        boolean breakBlocks = readBooleanOption(options, "break", false);
        return new ExplosionAction(stripOptionalQuotes(target), power, fire, breakBlocks, source);
    }

    private Action parseWorldBorderAction(String line, SourceLocation source, List<String> errors) {
        String lower = line.toLowerCase(Locale.ROOT);
        if (lower.startsWith("set worldborder size to ")) {
            return WorldBorderAction.size(readDouble(line.substring("set worldborder size to ".length()).trim(), 100.0), source);
        }

        String prefix = "set worldborder center to ";
        if (!lower.startsWith(prefix)) {
            errors.add(source.display() + ": worldborder action must set size or center");
            return null;
        }
        String rest = line.substring(prefix.length()).trim();
        String lowerRest = rest.toLowerCase(Locale.ROOT);
        int sizeIndex = lowerRest.indexOf(" size ");
        if (sizeIndex < 0) {
            errors.add(source.display() + ": worldborder center action must include size");
            return null;
        }

        String target = stripOptionalQuotes(rest.substring(0, sizeIndex).trim());
        double size = readDouble(rest.substring(sizeIndex + " size ".length()).trim(), 100.0);
        return WorldBorderAction.center(target, size, source);
    }

    private Action parseHeldItemFlagAction(String line, boolean add, SourceLocation source, List<String> errors) {
        String prefix = add ? "add item flag " : "remove item flag ";
        QuotedText flag = readQuoted(line.substring(prefix.length()).trim(), source, errors);
        if (flag == null) {
            return null;
        }
        if (!flag.tail().trim().equalsIgnoreCase("to held item") && !flag.tail().trim().equalsIgnoreCase("from held item")) {
            errors.add(source.display() + ": item flag action must target held item");
            return null;
        }
        return new HeldItemFlagAction(flag.text(), add, source);
    }

    private Action parseIfAction(String line, SourceLocation source, List<String> errors) {
        int colon = findOutsideQuotes(line, ':');
        if (colon < 0) {
            errors.add(source.display() + ": if action must use 'if condition: action'");
            return null;
        }

        String conditionText = line.substring("if ".length(), colon).trim();
        String actionText = line.substring(colon + 1).trim();
        if (actionText.isBlank()) {
            errors.add(source.display() + ": if action is empty");
            return null;
        }

        Condition condition = parseCondition(conditionText, source, errors);
        Action nestedAction = parseAction(actionText, source, errors);
        if (condition == null || nestedAction == null) {
            return null;
        }
        return new IfAction(condition, nestedAction, source);
    }

    private Action parseDelayAction(String line, SourceLocation source, List<String> errors) {
        int colon = findOutsideQuotes(line, ':');
        if (colon < 0) {
            errors.add(source.display() + ": delayed action must use 'after 5 seconds: action'");
            return null;
        }

        String durationText = line.substring(line.toLowerCase(Locale.ROOT).startsWith("after ") ? 6 : 5, colon).trim();
        long ticks = TimeUtil.parseTicks(durationText);
        if (ticks < 0L) {
            errors.add(source.display() + ": invalid delay duration '" + durationText + "'");
            return null;
        }

        String actionText = line.substring(colon + 1).trim();
        Action nestedAction = parseAction(actionText, source, errors);
        return nestedAction == null ? null : new DelayAction(ticks, nestedAction, source);
    }

    private Action parseRepeatAction(String line, SourceLocation source, List<String> errors) {
        int colon = findOutsideQuotes(line, ':');
        if (colon < 0) {
            errors.add(source.display() + ": repeat action must use 'repeat 3 times every 1 second: action'");
            return null;
        }

        String header = line.substring("repeat ".length(), colon).trim();
        String[] parts = header.split("\\s+");
        if (parts.length < 4 || !parts[1].equalsIgnoreCase("times") || !parts[2].equalsIgnoreCase("every")) {
            errors.add(source.display() + ": repeat action must use 'repeat 3 times every 1 second: action'");
            return null;
        }

        int times = readInt(parts[0], -1);
        String duration = String.join(" ", java.util.Arrays.copyOfRange(parts, 3, parts.length));
        long periodTicks = TimeUtil.parseTicks(duration);
        if (times <= 0 || periodTicks < 0L) {
            errors.add(source.display() + ": invalid repeat count or duration");
            return null;
        }

        Action nestedAction = parseAction(line.substring(colon + 1).trim(), source, errors);
        return nestedAction == null ? null : new RepeatAction(times, periodTicks, nestedAction, source);
    }

    private RepeatHeader parseRepeatHeader(String header, SourceLocation source, List<String> errors) {
        String repeatText = header.substring("repeat ".length()).trim();
        String[] parts = repeatText.split("\\s+");
        if (parts.length == 2 && parts[1].equalsIgnoreCase("times")) {
            int times = readInt(parts[0], -1);
            if (times <= 0) {
                errors.add(source.display() + ": invalid repeat count");
                return null;
            }
            return new RepeatHeader(times, 1L);
        }

        if (parts.length < 4 || !parts[1].equalsIgnoreCase("times") || !parts[2].equalsIgnoreCase("every")) {
            errors.add(source.display() + ": repeat block must use 'repeat 3 times:' or 'repeat 3 times every 1 second:'");
            return null;
        }

        int times = readInt(parts[0], -1);
        String duration = String.join(" ", java.util.Arrays.copyOfRange(parts, 3, parts.length));
        long periodTicks = TimeUtil.parseTicks(duration);
        if (times <= 0 || periodTicks < 0L) {
            errors.add(source.display() + ": invalid repeat count or duration");
            return null;
        }
        return new RepeatHeader(times, periodTicks);
    }

    private Action parseCallFunctionAction(String line, SourceLocation source, List<String> errors) {
        String callText = line.substring("call ".length()).trim();
        String functionName = callText;
        List<String> arguments = List.of();

        int withIndex = callText.toLowerCase(Locale.ROOT).indexOf(" with ");
        if (withIndex >= 0) {
            functionName = callText.substring(0, withIndex).trim();
            arguments = readQuotedList(callText.substring(withIndex + " with ".length()).trim(), source, errors);
            if (arguments == null) {
                return null;
            }
        }

        if (!functionName.matches("[a-zA-Z][a-zA-Z0-9_.-]*")) {
            errors.add(source.display() + ": invalid function name '" + functionName + "'");
            return null;
        }
        return new CallFunctionAction(functionName.toLowerCase(Locale.ROOT), arguments, source);
    }

    private Action parseGiveAction(String line, SourceLocation source, List<String> errors) {
        QuotedText item = readQuoted(line.substring("give ".length()).trim(), source, errors);
        if (item == null) {
            return null;
        }
        if (!item.tail().toLowerCase(Locale.ROOT).contains("to player")) {
            errors.add(source.display() + ": give action must use 'to player'");
            return null;
        }
        return new GiveItemAction(item.text(), readIntOption(item.tail(), "amount", 1), source);
    }

    private Action parseRemoveItemAction(String line, SourceLocation source, List<String> errors) {
        QuotedText item = readQuoted(line.substring("remove ".length()).trim(), source, errors);
        if (item == null) {
            return null;
        }
        if (!item.tail().toLowerCase(Locale.ROOT).contains("from player")) {
            errors.add(source.display() + ": remove action must use 'from player'");
            return null;
        }
        return new RemoveItemAction(item.text(), readIntOption(item.tail(), "amount", 1), source);
    }

    private Action parseGameModeAction(String line, SourceLocation source, List<String> errors) {
        QuotedText mode = readQuoted(line.substring("gamemode ".length()).trim(), source, errors);
        if (mode == null) {
            return null;
        }
        if (!mode.tail().trim().equalsIgnoreCase("to player")) {
            errors.add(source.display() + ": gamemode action must end with 'to player'");
            return null;
        }
        return new GameModeAction(mode.text(), source);
    }

    private Action parseTeleportAction(String line, SourceLocation source, List<String> errors) {
        String target = line.substring("teleport player to ".length()).trim();
        if (target.equalsIgnoreCase("spawn")) {
            return new TeleportAction("spawn", source);
        }

        QuotedText location = readQuoted(target, source, errors);
        return location == null ? null : new TeleportAction(location.text(), source);
    }

    private Action parseRespawnPointAction(String line, SourceLocation source, List<String> errors) {
        String target = line.substring("set respawn point to ".length()).trim();
        if (target.equalsIgnoreCase("player") || target.equalsIgnoreCase("here") || target.equalsIgnoreCase("spawn")) {
            return new RespawnPointAction(target, source);
        }

        QuotedText location = readQuoted(target, source, errors);
        return location == null ? null : new RespawnPointAction(location.text(), source);
    }

    private Action parseSpawnEntityAction(String line, SourceLocation source, List<String> errors) {
        QuotedText entity = readQuoted(line.substring("spawn ".length()).trim(), source, errors);
        if (entity == null) {
            return null;
        }
        if (!entity.tail().trim().equalsIgnoreCase("at player")) {
            errors.add(source.display() + ": spawn action must end with 'at player'");
            return null;
        }
        return new SpawnEntityAction(entity.text(), source);
    }

    private Action parseWeatherAction(String line, SourceLocation source, List<String> errors) {
        String weather = stripOptionalQuotes(line.substring("set weather to ".length()).trim()).toLowerCase(Locale.ROOT);
        return switch (weather) {
            case "clear", "sun", "sunny" -> new WeatherAction(WeatherAction.Mode.CLEAR, source);
            case "rain", "storm" -> new WeatherAction(WeatherAction.Mode.RAIN, source);
            case "thunder", "thunderstorm" -> new WeatherAction(WeatherAction.Mode.THUNDER, source);
            default -> {
                errors.add(source.display() + ": weather must be clear, rain or thunder");
                yield null;
            }
        };
    }

    private Action parseBossBarAction(String line, SourceLocation source, List<String> errors) {
        QuotedText title = readQuoted(line.substring("bossbar ".length()).trim(), source, errors);
        if (title == null) {
            return null;
        }

        String tail = title.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("to player")) {
            errors.add(source.display() + ": bossbar action must use 'to player'");
            return null;
        }

        String colorText = readOption(tail, "color");
        BarColor color = colorText == null ? BarColor.GREEN : BarColor.valueOf(colorText.toUpperCase(Locale.ROOT));
        double progress = readDoubleOption(tail, "progress", 1.0);
        long duration = 60L;
        int forIndex = tail.toLowerCase(Locale.ROOT).indexOf(" for ");
        if (forIndex >= 0) {
            long parsed = TimeUtil.parseTicks(tail.substring(forIndex + " for ".length()).trim());
            if (parsed >= 0L) {
                duration = parsed;
            }
        }
        return new BossBarAction(title.text(), color, progress, duration, source);
    }

    private Action parseSidebarAction(String line, SourceLocation source, List<String> errors) {
        QuotedText title = readQuoted(line.substring("sidebar title ".length()).trim(), source, errors);
        if (title == null) {
            return null;
        }

        String tail = title.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("lines ")) {
            errors.add(source.display() + ": sidebar action must use 'lines'");
            return null;
        }

        QuotedText lines = readQuoted(tail.substring("lines ".length()).trim(), source, errors);
        if (lines == null) {
            return null;
        }

        if (!lines.tail().trim().equalsIgnoreCase("to player")) {
            errors.add(source.display() + ": sidebar action must end with 'to player'");
            return null;
        }
        return new SidebarAction(title.text(), lines.text(), source);
    }

    private Action parseTabHeaderFooterAction(String line, SourceLocation source, List<String> errors) {
        QuotedText header = readQuoted(line.substring("tab header ".length()).trim(), source, errors);
        if (header == null) {
            return null;
        }

        String tail = header.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("footer ")) {
            errors.add(source.display() + ": tab action must use 'footer'");
            return null;
        }

        QuotedText footer = readQuoted(tail.substring("footer ".length()).trim(), source, errors);
        if (footer == null) {
            return null;
        }

        if (!footer.tail().trim().equalsIgnoreCase("to player")) {
            errors.add(source.display() + ": tab action must end with 'to player'");
            return null;
        }
        return new TabHeaderFooterAction(header.text(), footer.text(), source);
    }

    private Action parseEnchantHeldItemAction(String line, SourceLocation source, List<String> errors) {
        QuotedText enchantment = readQuoted(line.substring("enchant held item with ".length()).trim(), source, errors);
        if (enchantment == null) {
            return null;
        }

        int level = readIntOption(enchantment.tail(), "level", 1);
        return new EnchantHeldItemAction(enchantment.text(), level, source);
    }

    private Condition parseCondition(String conditionText, SourceLocation source, List<String> errors) {
        String lower = conditionText.toLowerCase(Locale.ROOT);
        if (lower.startsWith("not ")) {
            Condition nested = parseCondition(conditionText.substring("not ".length()).trim(), source, errors);
            return nested == null ? null : new NotCondition(nested);
        }

        if (lower.startsWith("nearby players radius ")) {
            return parseNearbyPlayersCondition(conditionText, source, errors);
        }

        Condition comparison = parseComparisonCondition(conditionText, source, errors);
        if (comparison != null) {
            return comparison;
        }

        if (lower.equals("player is op")) {
            return new PlayerOpCondition();
        }

        if (lower.equals("player is sneaking")) {
            return new PlayerStateCondition(PlayerStateCondition.State.SNEAKING);
        }
        if (lower.equals("player is sprinting")) {
            return new PlayerStateCondition(PlayerStateCondition.State.SPRINTING);
        }
        if (lower.equals("player is flying")) {
            return new PlayerStateCondition(PlayerStateCondition.State.FLYING);
        }
        if (lower.equals("player can fly")) {
            return new PlayerCanFlyCondition();
        }
        if (lower.equals("player is gliding")) {
            return new PlayerStateCondition(PlayerStateCondition.State.GLIDING);
        }
        if (lower.equals("player is swimming")) {
            return new PlayerStateCondition(PlayerStateCondition.State.SWIMMING);
        }
        if (lower.equals("player is blocking")) {
            return new PlayerStateCondition(PlayerStateCondition.State.BLOCKING);
        }

        if (lower.equals("player is whitelisted")) {
            return new WhitelistCondition();
        }

        if (lower.equals("server whitelist is enabled") || lower.equals("whitelist is enabled")) {
            return new ServerWhitelistCondition();
        }

        if (lower.equals("server is full")) {
            return new ServerCapacityCondition(ServerCapacityCondition.Mode.FULL);
        }

        if (lower.equals("server is empty")) {
            return new ServerCapacityCondition(ServerCapacityCondition.Mode.EMPTY);
        }

        if (lower.startsWith("player gamemode is ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("player gamemode is ".length()).trim(), source, errors);
            return quotedText == null ? null : new GameModeCondition(quotedText.text());
        }

        if (lower.startsWith("gamemode is ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("gamemode is ".length()).trim(), source, errors);
            return quotedText == null ? null : new GameModeCondition(quotedText.text());
        }

        if (lower.startsWith("player has effect ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("player has effect ".length()).trim(), source, errors);
            return quotedText == null ? null : new PotionEffectCondition(quotedText.text());
        }

        if (lower.startsWith("player ") && lower.endsWith(" is online")) {
            QuotedText quotedText = readQuoted(conditionText.substring("player ".length()).trim(), source, errors);
            if (quotedText == null || !quotedText.tail().trim().equalsIgnoreCase("is online")) {
                errors.add(source.display() + ": online-player condition must use 'player \"name\" is online'");
                return null;
            }
            return new OnlinePlayerCondition(quotedText.text());
        }

        if (lower.startsWith("chance ") && lower.endsWith("%")) {
            String percent = lower.substring("chance ".length(), lower.length() - 1).trim();
            try {
                return new ChanceCondition(Double.parseDouble(percent.replace(',', '.')));
            } catch (NumberFormatException exception) {
                errors.add(source.display() + ": invalid chance percent '" + percent + "'");
                return null;
            }
        }

        if (lower.equals("inventory has space")) {
            return new InventorySpaceCondition(InventorySpaceCondition.Mode.HAS_SPACE);
        }

        if (lower.equals("inventory is full")) {
            return new InventorySpaceCondition(InventorySpaceCondition.Mode.FULL);
        }

        if (lower.equals("inventory is empty")) {
            return new InventorySpaceCondition(InventorySpaceCondition.Mode.EMPTY);
        }

        if (lower.equals("held item is air")) {
            return HeldItemCondition.air();
        }

        if (lower.startsWith("held item is ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("held item is ".length()).trim(), source, errors);
            return quotedText == null ? null : new HeldItemCondition(quotedText.text(), false);
        }

        if (lower.startsWith("held item name contains ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("held item name contains ".length()).trim(), source, errors);
            return quotedText == null ? null : new HeldItemNameCondition(quotedText.text(), HeldItemNameCondition.Mode.CONTAINS);
        }

        if (lower.startsWith("held item name is ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("held item name is ".length()).trim(), source, errors);
            return quotedText == null ? null : new HeldItemNameCondition(quotedText.text(), HeldItemNameCondition.Mode.EQUALS);
        }

        if (lower.startsWith("held item has enchant ")) {
            return parseHeldItemEnchantCondition(conditionText, source, errors);
        }

        if (lower.startsWith("weather is ")) {
            return parseWeatherCondition(conditionText.substring("weather is ".length()).trim(), source, errors);
        }

        if (lower.equals("time is day")) {
            return new TimeCondition(TimeCondition.Mode.DAY);
        }

        if (lower.equals("time is night")) {
            return new TimeCondition(TimeCondition.Mode.NIGHT);
        }

        if (lower.startsWith("biome is ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("biome is ".length()).trim(), source, errors);
            return quotedText == null ? null : new BiomeCondition(quotedText.text());
        }

        if (lower.startsWith("player is within ")) {
            return parseDistanceCondition(conditionText, source, errors);
        }

        if (lower.startsWith("player has permission ") || lower.startsWith("sender has permission ")) {
            int prefixLength = lower.startsWith("player ") ? "player has permission ".length() : "sender has permission ".length();
            QuotedText quotedText = readQuoted(conditionText.substring(prefixLength).trim(), source, errors);
            return quotedText == null ? null : new PermissionCondition(quotedText.text());
        }

        if (lower.startsWith("player is in world ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("player is in world ".length()).trim(), source, errors);
            return quotedText == null ? null : new WorldCondition(quotedText.text());
        }

        if (lower.startsWith("player has item ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("player has item ".length()).trim(), source, errors);
            if (quotedText == null) {
                return null;
            }
            return new HasItemCondition(quotedText.text(), readIntOption(quotedText.tail(), "amount", 1));
        }

        if (lower.startsWith("block is ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("block is ".length()).trim(), source, errors);
            return quotedText == null ? null : new BlockCondition(quotedText.text());
        }

        if (lower.startsWith("message contains ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("message contains ".length()).trim(), source, errors);
            return quotedText == null ? null : new MessageCondition(MessageCondition.Mode.CONTAINS, quotedText.text());
        }

        if (lower.startsWith("message starts with ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("message starts with ".length()).trim(), source, errors);
            return quotedText == null ? null : new MessageCondition(MessageCondition.Mode.STARTS_WITH, quotedText.text());
        }

        if (lower.startsWith("message is ")) {
            QuotedText quotedText = readQuoted(conditionText.substring("message is ".length()).trim(), source, errors);
            return quotedText == null ? null : new MessageCondition(MessageCondition.Mode.EQUALS, quotedText.text());
        }

        if (lower.startsWith("variable ")) {
            return parseVariableCondition(conditionText, source, errors);
        }

        if (lower.startsWith("list ")) {
            return parseListCondition(conditionText, source, errors);
        }

        Condition customCondition = EasyScriptApi.registry().parseCondition(conditionText, source, errors);
        if (customCondition != null) {
            return customCondition;
        }

        errors.add(source.display() + ": unknown condition '" + conditionText + "'");
        return null;
    }

    private Condition parseNearbyPlayersCondition(String conditionText, SourceLocation source, List<String> errors) {
        String rest = conditionText.substring("nearby players radius ".length()).trim();
        String[] operators = {">=", "<=", "!=", "=", ">", "<"};
        for (String operator : operators) {
            int index = rest.indexOf(" " + operator + " ");
            if (index < 0) {
                continue;
            }
            double radius = readDouble(rest.substring(0, index).trim(), 0.0);
            int amount = readInt(rest.substring(index + operator.length() + 2).trim(), 0);
            return new NearbyPlayersCondition(radius, comparisonOperator(operator), amount);
        }
        errors.add(source.display() + ": nearby players condition must use an operator such as >= or =");
        return null;
    }

    private Condition parseHeldItemEnchantCondition(String conditionText, SourceLocation source, List<String> errors) {
        QuotedText enchantment = readQuoted(conditionText.substring("held item has enchant ".length()).trim(), source, errors);
        if (enchantment == null) {
            return null;
        }
        return new HeldItemEnchantCondition(enchantment.text(), readIntOption(enchantment.tail(), "level", 1));
    }

    private Condition parseWeatherCondition(String weatherText, SourceLocation source, List<String> errors) {
        String weather = stripOptionalQuotes(weatherText).toLowerCase(Locale.ROOT);
        return switch (weather) {
            case "clear", "sun", "sunny" -> new WeatherCondition(WeatherCondition.Mode.CLEAR);
            case "rain", "storm" -> new WeatherCondition(WeatherCondition.Mode.RAIN);
            case "thunder", "thunderstorm" -> new WeatherCondition(WeatherCondition.Mode.THUNDER);
            default -> {
                errors.add(source.display() + ": weather condition must be clear, rain or thunder");
                yield null;
            }
        };
    }

    private Condition parseDistanceCondition(String conditionText, SourceLocation source, List<String> errors) {
        String rest = conditionText.substring("player is within ".length()).trim();
        int ofIndex = rest.toLowerCase(Locale.ROOT).indexOf(" of ");
        if (ofIndex < 0) {
            errors.add(source.display() + ": distance condition must use 'of'");
            return null;
        }

        double radius = readDouble(rest.substring(0, ofIndex).trim(), 0.0);
        QuotedText target = readQuoted(rest.substring(ofIndex + " of ".length()).trim(), source, errors);
        return target == null ? null : new DistanceCondition(target.text(), radius);
    }

    private Condition parseComparisonCondition(String conditionText, SourceLocation source, List<String> errors) {
        QuotedText left = readQuotedQuietly(conditionText);
        if (left == null) {
            return null;
        }

        String tail = left.tail().trim();
        ComparisonCondition.Operator operator;
        String rightText;
        if (tail.startsWith(">=")) {
            operator = ComparisonCondition.Operator.GREATER_OR_EQUAL;
            rightText = tail.substring(2).trim();
        } else if (tail.startsWith("<=")) {
            operator = ComparisonCondition.Operator.LESS_OR_EQUAL;
            rightText = tail.substring(2).trim();
        } else if (tail.startsWith("!=")) {
            operator = ComparisonCondition.Operator.NOT_EQUALS;
            rightText = tail.substring(2).trim();
        } else if (tail.startsWith("=")) {
            operator = ComparisonCondition.Operator.EQUALS;
            rightText = tail.substring(1).trim();
        } else if (tail.startsWith(">")) {
            operator = ComparisonCondition.Operator.GREATER;
            rightText = tail.substring(1).trim();
        } else if (tail.startsWith("<")) {
            operator = ComparisonCondition.Operator.LESS;
            rightText = tail.substring(1).trim();
        } else if (tail.toLowerCase(Locale.ROOT).startsWith("contains ")) {
            operator = ComparisonCondition.Operator.CONTAINS;
            rightText = tail.substring("contains ".length()).trim();
        } else {
            return null;
        }

        QuotedText right = readQuoted(rightText, source, errors);
        return right == null ? null : new ComparisonCondition(left.text(), operator, right.text());
    }

    private Condition parseVariableCondition(String conditionText, SourceLocation source, List<String> errors) {
        QuotedText variable = readQuoted(conditionText.substring("variable ".length()).trim(), source, errors);
        if (variable == null) {
            return null;
        }

        String tail = variable.tail().trim();
        String lowerTail = tail.toLowerCase(Locale.ROOT);
        if (lowerTail.equals("exists")) {
            return new VariableCondition(VariableCondition.Mode.EXISTS, variable.text(), "");
        }
        if (lowerTail.startsWith("is ")) {
            QuotedText expected = readQuoted(tail.substring("is ".length()).trim(), source, errors);
            return expected == null ? null : new VariableCondition(VariableCondition.Mode.EQUALS, variable.text(), expected.text());
        }
        if (lowerTail.startsWith("contains ")) {
            QuotedText expected = readQuoted(tail.substring("contains ".length()).trim(), source, errors);
            return expected == null ? null : new VariableCondition(VariableCondition.Mode.CONTAINS, variable.text(), expected.text());
        }

        errors.add(source.display() + ": variable condition must use exists, is or contains");
        return null;
    }

    private Condition parseListCondition(String conditionText, SourceLocation source, List<String> errors) {
        QuotedText list = readQuoted(conditionText.substring("list ".length()).trim(), source, errors);
        if (list == null) {
            return null;
        }

        String tail = list.tail().trim();
        String lowerTail = tail.toLowerCase(Locale.ROOT);
        if (lowerTail.equals("exists")) {
            return new ListCondition(list.text(), "", ListCondition.Mode.EXISTS);
        }
        if (lowerTail.equals("is empty")) {
            return new ListCondition(list.text(), "", ListCondition.Mode.EMPTY);
        }
        if (lowerTail.startsWith("contains ")) {
            QuotedText value = readQuoted(tail.substring("contains ".length()).trim(), source, errors);
            return value == null ? null : new ListCondition(list.text(), value.text(), ListCondition.Mode.CONTAINS);
        }

        errors.add(source.display() + ": list condition must use exists, is empty or contains");
        return null;
    }

    private Action parseTitleAction(String line, SourceLocation source, List<String> errors) {
        QuotedText title = readQuoted(line.substring("title ".length()).trim(), source, errors);
        if (title == null) {
            return null;
        }

        String subtitle = "";
        String tail = title.tail().trim();
        String lowerTail = tail.toLowerCase(Locale.ROOT);
        if (lowerTail.startsWith("subtitle ")) {
            QuotedText subtitleText = readQuoted(tail.substring("subtitle ".length()).trim(), source, errors);
            if (subtitleText == null) {
                return null;
            }
            subtitle = subtitleText.text();
            tail = subtitleText.tail().trim();
            lowerTail = tail.toLowerCase(Locale.ROOT);
        }

        if (!lowerTail.startsWith("to player")) {
            errors.add(source.display() + ": title action must end with 'to player'");
            return null;
        }

        int stayTicks = 60;
        String afterTarget = tail.substring("to player".length()).trim();
        if (afterTarget.toLowerCase(Locale.ROOT).startsWith("for ")) {
            String durationText = afterTarget.substring("for ".length()).trim();
            long ticks = TimeUtil.parseTicks(durationText);
            if (ticks < 0L) {
                errors.add(source.display() + ": invalid title duration '" + durationText + "'");
                return null;
            }
            stayTicks = (int) Math.min(Integer.MAX_VALUE, ticks);
        }

        return new TitleAction(title.text(), subtitle, 10, stayTicks, 20, source);
    }

    private Action parseSoundAction(String line, SourceLocation source, List<String> errors) {
        QuotedText sound = readQuoted(line.substring("sound ".length()).trim(), source, errors);
        if (sound == null) {
            return null;
        }

        String tail = sound.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("to player")) {
            errors.add(source.display() + ": sound action must use 'to player'");
            return null;
        }

        float volume = readFloatOption(tail, "volume", 1.0F);
        float pitch = readFloatOption(tail, "pitch", 1.0F);
        return new SoundAction(sound.text(), volume, pitch, source);
    }

    private Action parseParticleAction(String line, SourceLocation source, List<String> errors) {
        QuotedText particle = readQuoted(line.substring("particle ".length()).trim(), source, errors);
        if (particle == null) {
            return null;
        }

        String tail = particle.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("at player")) {
            errors.add(source.display() + ": particle action must use 'at player'");
            return null;
        }

        int count = readIntOption(tail, "count", 8);
        return new ParticleAction(particle.text(), count, source);
    }

    private Action parsePotionEffectAction(String line, SourceLocation source, List<String> errors) {
        QuotedText effect = readQuoted(line.substring("effect ".length()).trim(), source, errors);
        if (effect == null) {
            return null;
        }

        String tail = effect.tail().trim();
        String lowerTail = tail.toLowerCase(Locale.ROOT);
        if (!lowerTail.startsWith("to player")) {
            errors.add(source.display() + ": effect action must use 'to player'");
            return null;
        }

        int durationTicks = 200;
        int forIndex = lowerTail.indexOf(" for ");
        if (forIndex >= 0) {
            String durationText = tail.substring(forIndex + " for ".length()).trim();
            int amplifierIndex = durationText.toLowerCase(Locale.ROOT).indexOf(" amplifier ");
            if (amplifierIndex >= 0) {
                durationText = durationText.substring(0, amplifierIndex).trim();
            }
            long ticks = TimeUtil.parseTicks(durationText);
            if (ticks < 0L) {
                errors.add(source.display() + ": invalid effect duration '" + durationText + "'");
                return null;
            }
            durationTicks = (int) Math.min(Integer.MAX_VALUE, ticks);
        }

        int amplifier = readIntOption(tail, "amplifier", 0);
        return new PotionEffectAction(effect.text(), durationTicks, amplifier, source);
    }

    private Action parseSetVariableAction(String line, SourceLocation source, List<String> errors) {
        QuotedText variable = readQuoted(line.substring("set variable ".length()).trim(), source, errors);
        if (variable == null) {
            return null;
        }

        String tail = variable.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("to ")) {
            errors.add(source.display() + ": set variable action must use 'to'");
            return null;
        }

        QuotedText value = readQuoted(tail.substring("to ".length()).trim(), source, errors);
        return value == null ? null : new SetVariableAction(variable.text(), value.text(), source);
    }

    private Action parseTransformedVariableAction(String line, SourceLocation source, List<String> errors) {
        QuotedText variable = readQuoted(line.substring("set variable ".length()).trim(), source, errors);
        if (variable == null) {
            return null;
        }
        String tail = variable.tail().trim();
        String lowerTail = tail.toLowerCase(Locale.ROOT);
        if (!lowerTail.startsWith("to ")) {
            return null;
        }
        String transform = tail.substring("to ".length()).trim();
        String lowerTransform = transform.toLowerCase(Locale.ROOT);

        if (lowerTransform.startsWith("lowercase ")) {
            QuotedText input = readQuoted(transform.substring("lowercase ".length()).trim(), source, errors);
            return input == null ? null : new SetTransformedVariableAction(variable.text(), SetTransformedVariableAction.Mode.LOWERCASE, input.text(), "", "", source);
        }
        if (lowerTransform.startsWith("uppercase ")) {
            QuotedText input = readQuoted(transform.substring("uppercase ".length()).trim(), source, errors);
            return input == null ? null : new SetTransformedVariableAction(variable.text(), SetTransformedVariableAction.Mode.UPPERCASE, input.text(), "", "", source);
        }
        if (lowerTransform.startsWith("trimmed ")) {
            QuotedText input = readQuoted(transform.substring("trimmed ".length()).trim(), source, errors);
            return input == null ? null : new SetTransformedVariableAction(variable.text(), SetTransformedVariableAction.Mode.TRIMMED, input.text(), "", "", source);
        }
        if (lowerTransform.startsWith("rounded ")) {
            QuotedText input = readQuoted(transform.substring("rounded ".length()).trim(), source, errors);
            return input == null ? null : new SetTransformedVariableAction(variable.text(), SetTransformedVariableAction.Mode.ROUNDED, input.text(), "", "", source);
        }
        if (lowerTransform.startsWith("substring ")) {
            QuotedText input = readQuoted(transform.substring("substring ".length()).trim(), source, errors);
            if (input == null) {
                return null;
            }
            String inputTail = input.tail().trim();
            String lowerInputTail = inputTail.toLowerCase(Locale.ROOT);
            if (!lowerInputTail.startsWith("from ") || !lowerInputTail.contains(" to ")) {
                errors.add(source.display() + ": substring transform must use 'from X to Y'");
                return null;
            }
            int toIndex = lowerInputTail.indexOf(" to ");
            String from = inputTail.substring("from ".length(), toIndex).trim();
            String to = inputTail.substring(toIndex + " to ".length()).trim();
            return new SetTransformedVariableAction(variable.text(), SetTransformedVariableAction.Mode.SUBSTRING, input.text(), from, to, source);
        }
        if (lowerTransform.startsWith("replace ")) {
            QuotedText input = readQuoted(transform.substring("replace ".length()).trim(), source, errors);
            if (input == null) {
                return null;
            }
            String inputTail = input.tail().trim();
            if (!inputTail.toLowerCase(Locale.ROOT).startsWith("target ")) {
                errors.add(source.display() + ": replace transform must use target and with");
                return null;
            }
            QuotedText target = readQuoted(inputTail.substring("target ".length()).trim(), source, errors);
            if (target == null || !target.tail().trim().toLowerCase(Locale.ROOT).startsWith("with ")) {
                errors.add(source.display() + ": replace transform must use 'with'");
                return null;
            }
            QuotedText replacement = readQuoted(target.tail().trim().substring("with ".length()).trim(), source, errors);
            return replacement == null ? null : new SetTransformedVariableAction(variable.text(), SetTransformedVariableAction.Mode.REPLACE, input.text(), target.text(), replacement.text(), source);
        }
        return null;
    }

    private Action parseAddVariableAction(String line, SourceLocation source, List<String> errors) {
        QuotedText amount = readQuoted(line.substring("add ".length()).trim(), source, errors);
        if (amount == null) {
            return null;
        }

        String tail = amount.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("to variable ")) {
            errors.add(source.display() + ": add action must use 'to variable'");
            return null;
        }

        QuotedText variable = readQuoted(tail.substring("to variable ".length()).trim(), source, errors);
        return variable == null ? null : new AddVariableAction(variable.text(), amount.text(), source);
    }

    private Action parseAddToListAction(String line, SourceLocation source, List<String> errors) {
        QuotedText value = readQuoted(line.substring("add ".length()).trim(), source, errors);
        if (value == null) {
            return null;
        }
        String tail = value.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("to list ")) {
            errors.add(source.display() + ": add list action must use 'to list'");
            return null;
        }
        QuotedText list = readQuoted(tail.substring("to list ".length()).trim(), source, errors);
        return list == null ? null : new AddToListAction(value.text(), list.text(), source);
    }

    private Action parseRemoveVariableAction(String line, SourceLocation source, List<String> errors) {
        QuotedText amount = readQuoted(line.substring("remove ".length()).trim(), source, errors);
        if (amount == null) {
            return null;
        }

        String tail = amount.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("from variable ")) {
            errors.add(source.display() + ": remove variable action must use 'from variable'");
            return null;
        }

        QuotedText variable = readQuoted(tail.substring("from variable ".length()).trim(), source, errors);
        return variable == null ? null : new RemoveVariableAction(variable.text(), amount.text(), source);
    }

    private Action parseRemoveFromListAction(String line, SourceLocation source, List<String> errors) {
        QuotedText value = readQuoted(line.substring("remove ".length()).trim(), source, errors);
        if (value == null) {
            return null;
        }
        String tail = value.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("from list ")) {
            errors.add(source.display() + ": remove list action must use 'from list'");
            return null;
        }
        QuotedText list = readQuoted(tail.substring("from list ".length()).trim(), source, errors);
        return list == null ? null : new RemoveFromListAction(value.text(), list.text(), source);
    }

    private Action parseSubtractVariableAction(String line, SourceLocation source, List<String> errors) {
        QuotedText amount = readQuoted(line.substring("subtract ".length()).trim(), source, errors);
        if (amount == null) {
            return null;
        }

        String tail = amount.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("from variable ")) {
            errors.add(source.display() + ": subtract action must use 'from variable'");
            return null;
        }

        QuotedText variable = readQuoted(tail.substring("from variable ".length()).trim(), source, errors);
        return variable == null ? null : new RemoveVariableAction(variable.text(), amount.text(), source);
    }

    private Action parseWriteFileAction(String line, SourceLocation source, List<String> errors, boolean append) {
        String prefix = append ? "append file " : line.toLowerCase(Locale.ROOT).startsWith("overwrite file ")
                ? "overwrite file "
                : "write file ";
        QuotedText file = readQuoted(line.substring(prefix.length()).trim(), source, errors);
        if (file == null) {
            return null;
        }

        String tail = file.tail().trim();
        String keyword = append ? "with " : "to ";
        if (!tail.toLowerCase(Locale.ROOT).startsWith(keyword)) {
            errors.add(source.display() + ": file write action must use '" + keyword.trim() + "'");
            return null;
        }

        QuotedText content = readQuoted(tail.substring(keyword.length()).trim(), source, errors);
        return content == null ? null : new WriteFileAction(file.text(), content.text(), append, source);
    }

    private Action parseReadFileAction(String line, SourceLocation source, List<String> errors) {
        QuotedText file = readQuoted(line.substring("read file ".length()).trim(), source, errors);
        if (file == null) {
            return null;
        }

        String tail = file.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("to variable ")) {
            errors.add(source.display() + ": read file action must use 'to variable'");
            return null;
        }

        QuotedText variable = readQuoted(tail.substring("to variable ".length()).trim(), source, errors);
        return variable == null ? null : new ReadFileAction(file.text(), variable.text(), source);
    }

    private Action parseSendFileAction(String line, SourceLocation source, List<String> errors) {
        QuotedText file = readQuoted(line.substring("send file ".length()).trim(), source, errors);
        if (file == null) {
            return null;
        }

        String target = file.tail().trim().toLowerCase(Locale.ROOT);
        if (target.equals("to player")) {
            return new SendFileAction(MessageAction.Target.PLAYER, file.text(), source);
        }
        if (target.equals("to sender")) {
            return new SendFileAction(MessageAction.Target.SENDER, file.text(), source);
        }
        if (target.equals("to console")) {
            return new SendFileAction(MessageAction.Target.CONSOLE, file.text(), source);
        }

        errors.add(source.display() + ": send file action must end with 'to player', 'to sender' or 'to console'");
        return null;
    }

    private Action parseListFolderAction(String line, SourceLocation source, List<String> errors) {
        QuotedText folder = readQuoted(line.substring("list folder ".length()).trim(), source, errors);
        if (folder == null) {
            return null;
        }

        String tail = folder.tail().trim();
        if (!tail.toLowerCase(Locale.ROOT).startsWith("to variable ")) {
            errors.add(source.display() + ": list folder action must use 'to variable'");
            return null;
        }

        QuotedText variable = readQuoted(tail.substring("to variable ".length()).trim(), source, errors);
        return variable == null ? null : new ListFolderAction(folder.text(), variable.text(), source);
    }

    private QuotedText readQuoted(String input, SourceLocation source, List<String> errors) {
        if (!input.startsWith("\"")) {
            errors.add(source.display() + ": expected quoted text");
            return null;
        }

        StringBuilder value = new StringBuilder();
        boolean escaping = false;
        for (int index = 1; index < input.length(); index++) {
            char character = input.charAt(index);
            if (escaping) {
                value.append(switch (character) {
                    case 'n' -> '\n';
                    case '"' -> '"';
                    case '\\' -> '\\';
                    default -> character;
                });
                escaping = false;
                continue;
            }

            if (character == '\\') {
                escaping = true;
                continue;
            }

            if (character == '"') {
                return new QuotedText(value.toString(), input.substring(index + 1));
            }

            value.append(character);
        }

        errors.add(source.display() + ": missing closing quote");
        return null;
    }

    private QuotedText readQuotedQuietly(String input) {
        if (!input.startsWith("\"")) {
            return null;
        }

        StringBuilder value = new StringBuilder();
        boolean escaping = false;
        for (int index = 1; index < input.length(); index++) {
            char character = input.charAt(index);
            if (escaping) {
                value.append(switch (character) {
                    case 'n' -> '\n';
                    case '"' -> '"';
                    case '\\' -> '\\';
                    default -> character;
                });
                escaping = false;
                continue;
            }

            if (character == '\\') {
                escaping = true;
                continue;
            }

            if (character == '"') {
                return new QuotedText(value.toString(), input.substring(index + 1));
            }

            value.append(character);
        }
        return null;
    }

    private List<String> readQuotedList(String input, SourceLocation source, List<String> errors) {
        List<String> values = new ArrayList<>();
        String remaining = input.trim();
        while (!remaining.isBlank()) {
            QuotedText quotedText = readQuoted(remaining, source, errors);
            if (quotedText == null) {
                return null;
            }
            values.add(quotedText.text());
            remaining = quotedText.tail().trim();
            if (remaining.startsWith(",")) {
                remaining = remaining.substring(1).trim();
            } else if (!remaining.isBlank()) {
                errors.add(source.display() + ": function arguments must be separated by commas");
                return null;
            }
        }
        return values;
    }

    private String normalizeCommandName(String input) {
        String value = input.trim();
        while (value.startsWith("/")) {
            value = value.substring(1);
        }
        return value.toLowerCase(Locale.ROOT);
    }

    private String stripOptionalQuotes(String input) {
        String value = input.trim();
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private int countIndent(String input) {
        int indent = 0;
        for (int index = 0; index < input.length(); index++) {
            char character = input.charAt(index);
            if (character == ' ') {
                indent++;
            } else if (character == '\t') {
                indent += 4;
            } else {
                break;
            }
        }
        return indent;
    }

    private String stripComment(String input) {
        boolean inQuote = false;
        boolean escaping = false;
        for (int index = 0; index < input.length(); index++) {
            char character = input.charAt(index);
            if (escaping) {
                escaping = false;
                continue;
            }
            if (character == '\\') {
                escaping = true;
                continue;
            }
            if (character == '"') {
                inQuote = !inQuote;
                continue;
            }
            if (character == '#' && !inQuote) {
                return input.substring(0, index);
            }
        }
        return input;
    }

    private int findOutsideQuotes(String input, char expected) {
        boolean inQuote = false;
        boolean escaping = false;
        for (int index = 0; index < input.length(); index++) {
            char character = input.charAt(index);
            if (escaping) {
                escaping = false;
                continue;
            }
            if (character == '\\') {
                escaping = true;
                continue;
            }
            if (character == '"') {
                inQuote = !inQuote;
                continue;
            }
            if (character == expected && !inQuote) {
                return index;
            }
        }
        return -1;
    }

    private float readFloatOption(String input, String option, float fallback) {
        String value = readOption(input, option);
        if (value == null) {
            return fallback;
        }
        try {
            return Float.parseFloat(value.replace(',', '.'));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private boolean readBooleanOption(String input, String option, boolean fallback) {
        String value = readOption(input, option);
        if (value == null) {
            return fallback;
        }
        return readBoolean(value);
    }

    private double readDoubleOption(String input, String option, double fallback) {
        String value = readOption(input, option);
        if (value == null) {
            return fallback;
        }
        return readDouble(value, fallback);
    }

    private int readIntOption(String input, String option, int fallback) {
        String value = readOption(input, option);
        if (value == null) {
            return fallback;
        }
        return readInt(value, fallback);
    }

    private String readBetween(String input, String prefix, String suffix) {
        String value = input.trim();
        if (value.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))) {
            value = value.substring(prefix.length()).trim();
        }

        int suffixIndex = value.toLowerCase(Locale.ROOT).indexOf(suffix.toLowerCase(Locale.ROOT));
        if (suffixIndex >= 0) {
            return value.substring(0, suffixIndex).trim();
        }
        return value;
    }

    private boolean readBoolean(String value) {
        String normalized = stripOptionalQuotes(value).trim().toLowerCase(Locale.ROOT);
        return normalized.equals("true")
                || normalized.equals("on")
                || normalized.equals("yes")
                || normalized.equals("enabled")
                || normalized.equals("enable")
                || normalized.equals("1");
    }

    private int readInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private double readDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private String readOption(String input, String option) {
        String[] parts = input.trim().split("\\s+");
        for (int index = 0; index < parts.length - 1; index++) {
            if (parts[index].equalsIgnoreCase(option)) {
                return parts[index + 1];
            }
        }
        return null;
    }

    private ComparisonCondition.Operator comparisonOperator(String operator) {
        return switch (operator) {
            case ">=" -> ComparisonCondition.Operator.GREATER_OR_EQUAL;
            case "<=" -> ComparisonCondition.Operator.LESS_OR_EQUAL;
            case "!=" -> ComparisonCondition.Operator.NOT_EQUALS;
            case ">" -> ComparisonCondition.Operator.GREATER;
            case "<" -> ComparisonCondition.Operator.LESS;
            case "=" -> ComparisonCondition.Operator.EQUALS;
            default -> throw new IllegalArgumentException("Unknown comparison operator: " + operator);
        };
    }

    private record PreparedLine(int indent, String text) {
    }

    private record ActionParseResult(Action action, int nextIndex) {
    }

    private record BlockParseResult(List<Action> actions, int nextIndex) {
    }

    private record RepeatHeader(int times, long periodTicks) {
    }

    private record QuotedText(String text, String tail) {
    }

    private static final class CommandBuilder {
        private final String name;
        private final SourceLocation source;
        private final List<String> aliases = new ArrayList<>();
        private final List<Action> actions = new ArrayList<>();
        private String permission;
        private String permissionMessage;
        private String usage;
        private String description;
        private final List<String> argumentTypes = new ArrayList<>();
        private long cooldownTicks;
        private String cooldownMessage;

        private CommandBuilder(String name, SourceLocation source) {
            this.name = name;
            this.source = source;
        }

        private ScriptCommand build() {
            return new ScriptCommand(
                    name,
                    aliases,
                    permission,
                    permissionMessage,
                    usage,
                    description,
                    argumentTypes,
                    cooldownTicks,
                    cooldownMessage,
                    actions,
                    source
            );
        }
    }

    private static final class FunctionBuilder {
        private final String name;
        private final List<String> parameters;
        private final SourceLocation source;
        private final List<Action> actions = new ArrayList<>();

        private FunctionBuilder(String name, List<String> parameters, SourceLocation source) {
            this.name = name;
            this.parameters = List.copyOf(parameters);
            this.source = source;
        }

        private pl.macie.easyscript.script.model.ScriptFunction build() {
            return new pl.macie.easyscript.script.model.ScriptFunction(name, parameters, actions, source);
        }
    }
}
