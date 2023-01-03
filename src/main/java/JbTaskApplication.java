import fileManagement.LocalFile;
import fileManagement.SSHFile;
import fileManagement.IFile;
import java.io.File;
import core.SyncImpl;
import net.schmizz.sshj.xfer.FileSystemFile;

public class JbTaskApplication {

    private static final String sourceDir = "C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\source\\";
    private static final String targetDir = "C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\source\\";
    private static final String sshPath = "/test";
    private static final String sshHost = "34.71.206.155";
    private static final String sshPort = "22";
    private static final String sshUser = "dyadkinm";
    private static final String sshPrivateKey = "C:\\Users\\Dyadkin Maxim\\.ssh\\id_ed25519";

    public static void main(String[] args) {
        IFile source = new LocalFile(new File(sourceDir));
        IFile localTarget = new LocalFile(new File(targetDir));
        IFile sshTarget = new SSHFile(
                sshHost, sshPort, sshUser, sshPrivateKey,
                new FileSystemFile("target"));

//        var localScheduler = new LocalScheduler();
//        localScheduler.localSchedule(source, sshTargetFile, sshFileManager);
        try {
           var sync = new SyncImpl();
           sync.synchronize(source, localTarget);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}