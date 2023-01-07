package monitor;

import core.Progress;
import core.SyncImpl;
import datasource.base.IFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SourceMonitor {
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
        var sync = new SyncImpl();
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                log.info("Source-event sync started: " + source.getCanonicalPath());
                sync.synchronize(source, target, progress);
            }
            key.reset();
        }
    }
}