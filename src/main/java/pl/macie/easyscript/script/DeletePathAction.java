package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptFileUtil;

import java.io.File;

public final class DeletePathAction implements Action {
    private final String path;
    private final SourceLocation source;

    public DeletePathAction(String path, SourceLocation source) {
        this.path = path;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        File file = ScriptFileUtil.resolve(context, path);
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null && children.length > 0) {
                throw new IllegalStateException("Folder is not empty: " + file.getName());
            }
        }
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Could not delete path: " + file.getName());
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
