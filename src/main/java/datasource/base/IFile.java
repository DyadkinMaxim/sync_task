package datasource.base;

import core.Progress;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface IFile {

    File getSystemFile();

    boolean exists();

    boolean mkdirs();

    boolean isDirectory();

    String getCanonicalPath();

    long countAll();

    String[] list();

    long length();

    long lastModified();

    Path toPath();

    IFile getChild(String child);

    void copyFile(IFile source) throws IOException;

    void setLastModified(long value);

    void delete();

    void setProgress(Progress progress);
}
