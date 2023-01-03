package fileManagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface IFile {

    void build();

    File getSystemFile();

    boolean exists();

    boolean mkdirs();

    boolean isDirectory();

    String getCanonicalPath();

    String[] list();

    long length();

    long lastModified();

    Path toPath();

    IFile getChild(String child);

    void copyFile(IFile source) throws IOException;

    void delete() throws IOException;
}
