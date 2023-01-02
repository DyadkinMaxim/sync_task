package fileManagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface  TargetFile {

    File getFile();

    boolean exists();

    boolean mkdirs();

    boolean isDirectory();

    String getCanonicalPath();

    String[] list();

    long length();

    long lastModified();

    Path toPath();

    TargetFile getChild(String child);
}
