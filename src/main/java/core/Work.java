package core;

import datasource.base.Datasource;
import datasource.base.DatasourceManager;
import datasource.base.Param;
import datasource.base.IFile;
import datasource.local.LocalDatasource;
import datasource.ssh.SSHDatasource;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Work {

    private static final String sourceDir = "C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\source\\";
    private static final String targetDir = "C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\target\\";
    private static final String sshSystemFilePath = "target";
    private static final String sshHost = "34.71.206.155";
    private static final String sshPort = null;
    private static final String sshUser = "dyadkinm";
    private static final String sshPrivateKey = "C:\\Users\\Dyadkin Maxim\\.ssh\\id_ed25519";

    public static void work() {
        log.info("App started");
        DatasourceManager manager = new DatasourceManager();
        manager.add(new SSHDatasource());
        manager.add(new LocalDatasource());

        var sourceParams = manager.getByName("LOCAL").getConnectionSettings();
            Param.getParam(sourceParams, "filePath").setValue(sourceDir);
        var sshTargetParams = manager.getByName("SSH").getConnectionSettings();
            Param.getParam(sshTargetParams, "host").setValue(sshHost);
            Param.getParam(sshTargetParams, "port").setValue(sshPort);
            Param.getParam(sshTargetParams, "username").setValue(sshUser);
            Param.getParam(sshTargetParams, "privateKeyPath").setValue(sshPrivateKey);
            Param.getParam(sshTargetParams, "systemFilePath").setValue(sshSystemFilePath);

        Datasource sourceDS = manager.getByName("LOCAL");
        Datasource targetDS = manager.getByName("SSH");
        try {
            sourceDS.connect(sourceParams);
            targetDS.connect(sshTargetParams);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        IFile source = sourceDS.getRoot();
        IFile sshTarget = targetDS.getRoot();

        //DatasourceMonitor.monitor(source, sshTarget);
        try {
            var progress = new Progress(source, sshTarget);
            source.setProgress(progress);
            sshTarget.setProgress(progress);
            var sync = new SyncImpl();
            sync.synchronize(source, sshTarget, progress);
            sourceDS.disconnect();
            targetDS.disconnect();
            log.info(String.format("Datasources disconnected: source - %s, target - %s",
                    sourceDS.getName(), targetDS.getName()));

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
