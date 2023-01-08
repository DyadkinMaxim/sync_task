package core;

import core.Progress;
import core.Sync;
import core.SyncImpl;
import datasource.base.IFile;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

    private static Sync sync = new SyncImpl();
    public static volatile Instant lastSync;

    public static synchronized void doSync(IFile source, IFile target, Progress progress, String type) {
        log.info(String.format("%s-event sync started: %s", type, source.getCanonicalPath()));
        try {
            progress.initProgress(source, target);
            sync.synchronize(source, target, progress);
            source.setLastModified(target.getLastModified());
            lastSync = Instant.now();
            progress.resetProgress();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    public static long countAll(IFile file) {
        var counter = new AtomicLong();
        getFilesCount(file, counter);
        log.debug(String.format("Counted %s files in %s", counter, file.getCanonicalPath()));
        return counter.longValue();
    }

    private static void getFilesCount(IFile file, AtomicLong counter) {
        String[] files = file.list();
        if (files != null) {
            for (var child : files) {
                IFile next = file.getChild(child);
                counter.incrementAndGet();
                if (next.isDirectory()) {
                    getFilesCount(next, counter);
                }
            }
        }
    }
}
