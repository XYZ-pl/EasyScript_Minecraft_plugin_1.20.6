package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptFileUtil;

import java.io.File;
import java.io.IOException;

public final class CreateFileAction implements Action {
    private final String path;
    private final SourceLocation source;

    public CreateFileAction(String path, SourceLocation source) {
        this.path = path;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        File file = ScriptFileUtil.resolve(context, path);
        ScriptFileUtil.ensureParent(file);
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new IllegalStateException("Could not create file: " + file.getName());
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create file: " + file.getName(), exception);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
