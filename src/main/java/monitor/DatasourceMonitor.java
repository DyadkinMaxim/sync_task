package monitor;

import fileManagement.IFile;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatasourceMonitor {

    public static void monitor(IFile source, IFile target) {
        try {
            var scheduler = Executors.newScheduledThreadPool(1);
            var targetMonitor = new TargetMonitor(source, target);
            scheduler.scheduleAtFixedRate(
                    targetMonitor, 0, 60, TimeUnit.SECONDS);
            SourceMonitor.sourceWatch(source, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}