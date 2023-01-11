package core;

import client.PauseResume;
import datasource.base.Datasource;
import datasource.base.IFile;
import lombok.extern.slf4j.Slf4j;
import monitor.DatasourceMonitor;

/** Represents executable task for process consuming
 */
@Slf4j
public class SyncJob {
    public static void job(
            Datasource sourceDatasource,
            Datasource targetDatasource,
            PauseResume pauseResume) {
        pauseResume.printProgress("App started");
        pauseResume.printProgress("Establishing connection to target directory...");
        IFile source = sourceDatasource.getRoot();
        IFile localTarget = targetDatasource.getRoot();
        var progress = new Progress(pauseResume);
        source.setProgress(progress);
        localTarget.setProgress(progress);
        DatasourceMonitor.monitor(source, localTarget, progress, pauseResume);
    }
}