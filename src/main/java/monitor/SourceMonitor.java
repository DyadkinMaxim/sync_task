package monitor;

import client.PauseResume;
import core.FileUtils;
import core.Progress;
import datasource.base.IFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Instant monitoring of source directory,
 * Calls sync if source directory has been modified by user
 */
@Slf4j
public class SourceMonitor {

    private static final String SOURCE_NAME = "Local";

    public static synchronized void sourceWatch(
            IFile source, IFile target,
            Progress progress, PauseResume pauseResume) throws Exception {
        WatchService watchService
                = FileSystems.getDefault().newWatchService();
        Path rootPath = Paths.get(source.getCanonicalPath());

        rootPath.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            // to handle several events, for example: new folder + rename
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                // todo logs to debug level
                log.info("Watch service triggered: " + event.context());
                var sourceLastModified = Instant.now();
                // To avoid miss sync from Watcher: sourceLastModified - lastSync > 1 second
                var shiftedSourceLM = sourceLastModified.minus(1, ChronoUnit.SECONDS);
                if (FileUtils.lastSync != null && shiftedSourceLM.compareTo(FileUtils.lastSync) > 0) {
                    FileUtils.doSync(source, target, progress, pauseResume, SOURCE_NAME);
                } else {
                    log.info(String.format("Event %s is ignored - already processed",
                            rootPath.resolve(event.context().toString())));
                }
            }
            key.reset();
        }
    }
}