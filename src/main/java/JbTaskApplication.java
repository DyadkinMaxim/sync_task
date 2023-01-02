import fileManagement.SSHFileManager;
import fileManagement.SSHTargetFile;
import fileManagement.TargetFile;
import java.io.File;
import local.SyncImpl;
import net.schmizz.sshj.xfer.FileSystemFile;

public class JbTaskApplication {

    private static final String loadDirectory = "C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\source\\";
    private static final String sshPath = "/test";
    private static final String sshHost = "34.71.206.155";
    private static final String sshPort = "22";
    private static final String sshUser = "dyadkinm";
    private static final String sshPrivateKey = "C:\\Users\\Dyadkin Maxim\\.ssh\\id_ed25519";

    public static void main(String[] args) {
        File source = new File("C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\source");
//        TargetFile target = new LocalTargetFile(new File("C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\target"));
//        LocalFileManager lfm = new LocalFileManager();
        SSHFileManager sshFileManager = new SSHFileManager(
                loadDirectory, sshPath, sshHost, sshPort, sshUser, sshPrivateKey);
        TargetFile sshTargetFile = new SSHTargetFile(
                loadDirectory, sshPath, sshHost, sshPort, sshUser, sshPrivateKey,
                new FileSystemFile("target"));

//        var localScheduler = new LocalScheduler();
//        localScheduler.localSchedule(source, sshTargetFile, sshFileManager);
        try {
           var sync = new SyncImpl();
           sync.synchronize(source, sshTargetFile, sshFileManager);
           // sshFileManager.copyFile(source, sshTargetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}