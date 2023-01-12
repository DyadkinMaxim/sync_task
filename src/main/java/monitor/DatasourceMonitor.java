package monitor;

import client.PauseResume;
import core.Progress;
import datasource.base.IFile;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Enter point to monitor source and target directories
 */
@Slf4j
public class DatasourceMonitor {

    public static void monitor(IFile source, IFile target,
                               Progress progress, PauseResume pauseResume) {
        try {
            pauseResume.printProgress(String.format("Monitoring started for source: %s, target: %s",
                    source.getCanonicalPath(), target.toPath().getFileName()));
            var scheduler = Executors.newScheduledThreadPool(1);
            var targetMonitor = new TargetMonitor(source, target, progress, pauseResume);
            scheduler.scheduleAtFixedRate(
                    targetMonitor, 0, 60, TimeUnit.SECONDS);
            SourceMonitor.sourceWatch(source, target, progress, pauseResume);
        } catch (Exception ex) {
          log.warn(ex.getMessage());
        }
    }
}