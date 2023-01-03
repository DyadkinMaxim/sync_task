package monitor;

import core.SyncImpl;
import fileManagement.IFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SourceMonitor {

    public void localSchedule(IFile source, IFile target) {
        try {
            var scheduler = Executors.newScheduledThreadPool(1);
            var localTargetValidator = new TargetMonitor(
                    source, target);
            scheduler.scheduleAtFixedRate(localTargetValidator, 0, 30, TimeUnit.SECONDS);
            sourceWatch(source, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sourceWatch(IFile source, IFile target) throws Exception {
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
                sync.synchronize(source, target);
            }
            key.reset();
        }
    }
}