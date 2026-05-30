package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptFileUtil;

import java.io.File;

public final class CreateFolderAction implements Action {
    private final String path;
    private final SourceLocation source;

    public CreateFolderAction(String path, SourceLocation source) {
        this.path = path;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        File folder = ScriptFileUtil.resolve(context, path);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("Could not create folder: " + folder.getName());
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
