import fileManagement.FileFactory;
import fileManagement.FileType;
import fileManagement.IFile;
import core.SyncImpl;
import java.util.HashMap;
import java.util.Map;

public class JbTaskApplication {

    private static final String sourceDir = "C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\source\\";
    private static final String targetDir = "C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\target\\";
    private static final String sshSystemFilePath = "target";
    private static final String sshHost = "34.71.206.155";
    private static final String sshPort = "22";
    private static final String sshUser = "dyadkinm";
    private static final String sshPrivateKey = "C:\\Users\\Dyadkin Maxim\\.ssh\\id_ed25519";

    public static void main(String[] args) {
        Map<String, String> sourceParams = new HashMap<>();
        sourceParams.put("filePath", sourceDir);

        Map<String, String> localTargetParams = new HashMap<>();
        localTargetParams.put("filePath", targetDir);

        Map<String, String> sshTargetParams = new HashMap<>();
        sshTargetParams.put("host", sshHost);
        sshTargetParams.put("port", sshPort);
        sshTargetParams.put("username", sshUser);
        sshTargetParams.put("privateKeyPath", sshPrivateKey);
        sshTargetParams.put("systemFilePath", sshSystemFilePath);

        IFile source = FileFactory.create(FileType.LOCAL, sourceParams);
        IFile sshTarget = FileFactory.create(FileType.SSH, sshTargetParams);
        IFile localTarget = FileFactory.create(FileType.LOCAL, localTargetParams);
//        var localScheduler = new LocalScheduler();
//        localScheduler.localSchedule(source, sshTargetFile, sshFileManager);
        try {
           var sync = new SyncImpl();
           sync.synchronize(source, sshTarget);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}