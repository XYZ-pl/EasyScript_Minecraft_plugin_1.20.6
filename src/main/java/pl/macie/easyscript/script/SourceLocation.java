package pl.macie.easyscript.script;

import java.io.File;

public final class SourceLocation {
    private final String fileName;
    private final int line;

    public SourceLocation(File file, int line) {
        this.fileName = file.getName();
        this.line = line;
    }

    public String display() {
        return fileName + ":" + line;
    }
}
