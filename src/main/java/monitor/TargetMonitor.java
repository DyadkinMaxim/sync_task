package monitor;

import core.Progress;
import core.SyncImpl;
import datasource.base.FileUtils;
import datasource.base.IFile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Scheduled(1 min) monitoring of target directory,
 * Calls sync if any directory has been modified in different way
 */
@Slf4j
@AllArgsConstructor
public class TargetMonitor implements Runnable {

    private final IFile source;
    private final IFile target;
    private Progress progress;
    private static final String TARGET_NAME = "Target";

    @Override
    public void run() {
        var sync = new SyncImpl();
        try {
            var sourceLM = source.getLastModified();
            var targetLM = target.getLastModified();
            if (sourceLM != targetLM) {
                FileUtils.doSync(source, target, progress, TARGET_NAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
