package monitor;

import core.SyncImpl;
import fileManagement.IFile;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TargetMonitor implements Runnable {

    private final IFile source;
    private final IFile target;

    @Override
    public void run() {
        log.info("Target scan started: " + target.getCanonicalPath());
        if (target.lastModified() > 60000) {
            var sync = new SyncImpl();
            try {
                sync.synchronize(source, target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
