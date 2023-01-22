package monitor;

import client.PauseResume;
import core.FileUtils;
import core.Progress;
import datasource.base.IFile;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Instant monitoring of source directory,
 * Calls sync if source directory has been modified by user
 */
@Slf4j
public class SourceMonitor {

    private WatchService watchService;
    private final String SOURCE_NAME = "Local";

    public synchronized void sourceWatch(
            IFile source, IFile target,
            Progress progress, PauseResume pauseResume) throws Exception {
        watchService
                = FileSystems.getDefault().newWatchService();
        Path rootPath = Paths.get(source.getCanonicalPath());
        registerAllSubdirectories(rootPath);
        WatchKey key;
        try {
            while ((key = watchService.take()) != null) {
                Thread.sleep(50); // to avoid duplicated events(change metadata event + change content event )
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path dir = (Path) key.watchable();
                    Path child = dir.resolve((Path) event.context());
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(child)) {
                        registerAllSubdirectories(child);
                    }
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
        } catch (InterruptedException ex) {
            throw new InterruptedException("Current sync was cancelled");
        }
    }

    public void registerAllSubdirectories(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}