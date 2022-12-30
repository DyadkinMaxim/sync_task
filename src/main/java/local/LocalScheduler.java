package local;

import fileManagement.FileManager;
import fileManagement.TargetFile;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LocalScheduler {

    public void localSchedule(File source, TargetFile target, FileManager fm) {
        try {
            var scheduler = Executors.newScheduledThreadPool(1);
            var localTargetValidator = new LocalTargetValidator(
                    source, target, fm);
            scheduler.scheduleAtFixedRate(localTargetValidator, 0, 30, TimeUnit.SECONDS);
            sourceWatch(source, target, fm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sourceWatch(File source, TargetFile target, FileManager fm) throws Exception {
        WatchService watchService
                = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(source.getPath());

        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        var sync = new SyncImpl();
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                sync.synchronize(source, target, fm);
            }
            key.reset();
        }
    }
}