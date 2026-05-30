package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptFileUtil;
import pl.macie.easyscript.util.TextUtil;

import java.io.File;

public final class ReadFileAction implements Action {
    private final String path;
    private final String variableName;
    private final SourceLocation source;

    public ReadFileAction(String path, String variableName, SourceLocation source) {
        this.path = path;
        this.variableName = variableName;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        File file = ScriptFileUtil.resolve(context, path);
        if (!file.isFile()) {
            throw new IllegalStateException("File does not exist: " + file.getName());
        }
        context.getVariables().set(
                TextUtil.applyPlaceholders(variableName, context),
                ScriptFileUtil.readLimited(context, file)
        );
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
