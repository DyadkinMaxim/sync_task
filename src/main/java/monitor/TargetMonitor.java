package monitor;

import core.SyncImpl;
import fileManagement.IFile;
import java.time.Instant;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TargetMonitor implements Runnable {

    private final IFile source;
    private final IFile target;

    @Override
    public void run() {
        System.out.println(Instant.now() + " " + target.lastModified());
        if (target.lastModified() > 30000) {
            var sync = new SyncImpl();
            try {
                sync.synchronize(source, target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
