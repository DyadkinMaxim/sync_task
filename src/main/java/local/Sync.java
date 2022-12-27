package local;

import java.io.File;
import java.io.IOException;

public interface Sync {
    boolean areInSync(File source, File destination) throws IOException;

    void synchronize(File source, File destination, boolean smart) throws IOException;

    void synchronize(File source, File destination, boolean smart, long chunkSize) throws IOException;

    void delete(File file);
}
