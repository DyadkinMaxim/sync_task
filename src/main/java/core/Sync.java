package core;

import client.PauseResume;
import datasource.base.IFile;
import java.io.IOException;

public interface Sync {
    void  synchronize(
            IFile source, IFile target,
              Progress progress, PauseResume pauseResume) throws IOException;
}
