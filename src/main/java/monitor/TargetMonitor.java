package monitor;

import client.PauseResume;
import core.Progress;
import core.SyncImpl;
import core.FileUtils;
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
    private static final String TARGET_NAME = "Target";
    private static boolean isFirst = true; // unconditional first sync
    private Progress progress;
    private PauseResume pauseResume;

    @Override
    public void run() {
        try {
            if (isFirst || source.getLastModified() != target.searchLastModified()) {
                FileUtils.doSync(source, target, progress, pauseResume, TARGET_NAME);
                isFirst = false;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
