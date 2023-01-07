package monitor;

import core.Progress;
import core.SyncImpl;
import datasource.base.IFile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TargetMonitor implements Runnable {

    private final IFile source;
    private final IFile target;
    private Progress progress;

    @Override
    public void run() {
        var sync = new SyncImpl();
        try {
            if (source.lastModified() != target.lastModified()
                    || source.length() != target.length()) {
                log.info("Target-side sync started: " + target.getCanonicalPath());
                sync.synchronize(source, target, progress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
