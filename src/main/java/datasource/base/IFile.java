package datasource.base;

import core.Progress;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

/**
 * Represents file from any datasource
 */
public interface IFile {

    boolean exists();

    boolean mkdirs();

    boolean isDirectory();

    String getCanonicalPath();

    long countAll();

    String[] list();

    long length();

    long getLastModified();

    void setLastModified(long value);

    Path toPath();

    IFile getChild(String child);

    void copyFile(IFile source) throws IOException;

    void delete();

    void setProgress(Progress progress);
}
