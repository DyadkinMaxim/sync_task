package monitor;

import core.Progress;
import fileManagement.IFile;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatasourceMonitor {

    public static void monitor(IFile source, IFile target, Progress progress) {
        try {
            var scheduler = Executors.newScheduledThreadPool(1);
            var targetMonitor = new TargetMonitor(source, target, progress);
            scheduler.scheduleAtFixedRate(
                    targetMonitor, 0, 60, TimeUnit.SECONDS);
            SourceMonitor.sourceWatch(source, target, progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}