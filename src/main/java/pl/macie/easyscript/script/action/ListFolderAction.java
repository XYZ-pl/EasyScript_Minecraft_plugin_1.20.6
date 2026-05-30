package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptFileUtil;
import pl.macie.easyscript.util.TextUtil;

import java.io.File;

public final class ListFolderAction implements Action {
    private final String path;
    private final String variableName;
    private final SourceLocation source;

    public ListFolderAction(String path, String variableName, SourceLocation source) {
        this.path = path;
        this.variableName = variableName;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        File folder = ScriptFileUtil.resolve(context, path);
        if (!folder.isDirectory()) {
            throw new IllegalStateException("Folder does not exist: " + folder.getName());
        }
        context.getVariables().set(
                TextUtil.applyPlaceholders(variableName, context),
                ScriptFileUtil.listFolder(folder)
        );
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
