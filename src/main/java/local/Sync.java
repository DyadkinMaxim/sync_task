package local;

import fileManagement.FileManager;
import fileManagement.TargetFile;
import java.io.File;
import java.io.IOException;

public interface Sync {
    void synchronize(File source, TargetFile target, FileManager fm) throws IOException;

    void setPause(boolean isPaused);
}
