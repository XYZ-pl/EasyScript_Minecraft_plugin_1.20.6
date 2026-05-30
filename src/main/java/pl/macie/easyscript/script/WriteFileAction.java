package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptFileUtil;
import pl.macie.easyscript.util.TextUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public final class WriteFileAction implements Action {
    private final String path;
    private final String content;
    private final boolean append;
    private final SourceLocation source;

    public WriteFileAction(String path, String content, boolean append, SourceLocation source) {
        this.path = path;
        this.content = content;
        this.append = append;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        File file = ScriptFileUtil.resolve(context, path);
        ScriptFileUtil.ensureParent(file);
        String preparedContent = TextUtil.applyPlaceholders(content, context);
        try {
            if (append) {
                Files.writeString(
                        file.toPath(),
                        preparedContent,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            } else {
                Files.writeString(
                        file.toPath(),
                        preparedContent,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not write file: " + file.getName(), exception);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
