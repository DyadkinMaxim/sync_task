package fileManagement;

import java.io.File;
import java.io.IOException;

public interface FileManager {

    void copyFile(File source, TargetFile target) throws IOException;

    void delete(File file);
}
