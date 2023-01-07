package core;

import fileManagement.IFile;
import java.io.IOException;

public interface Sync {
    void  synchronize(IFile source, IFile target, Progress progress) throws IOException;

    void setPause(boolean isPaused);
}
