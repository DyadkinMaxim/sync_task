package monitor;

import core.Progress;
import datasource.base.FileUtils;
import datasource.base.IFile;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import lombok.extern.slf4j.Slf4j;

/**
 * Instant monitoring of source directory,
 * Calls sync if source directory has been modified by user
 */
@Slf4j
public class SourceMonitor {

    private static final String SOURCE_NAME = "Local";

    public static void sourceWatch(IFile source, IFile target, Progress progress) throws Exception {
        WatchService watchService
                = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(source.getCanonicalPath());

        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                log.info("Watch service triggered: " + event.context());
                var relativePath = (Path) event.context();
                var sourceLM = new File(path.resolve(relativePath).toString()).lastModified();
                log.info("sourceLM: " + event.context() + " lastSync: " + FileUtils.lastSync);
                if (FileUtils.lastSync != 0 && FileUtils.lastSync > sourceLM) {
                    FileUtils.doSync(source, target, progress, SOURCE_NAME);
                }
            }
            key.reset();
        }
    }
}