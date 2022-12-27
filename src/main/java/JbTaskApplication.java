import remote.SSHLoader;
import local.Sync;
import local.LocalSync;

import java.io.File;

public class JbTaskApplication {

    private static final String loadDirectory = "C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\remote\\";
    private static final String sftpPath = "/test";
    private static final String sftpHost = "test759.files.com";
    private static final String sftpPort = "22";
    private static final String sftpUser = "dyadkinm@gmail.com";
    private static final String sftpPassword = "9kJ@sd#R3^";

    public static void main(String[] args) {
       SSHLoader sshLoader = new SSHLoader(
               loadDirectory, sftpPath, sftpHost, sftpPort, sftpUser, sftpPassword);
       File source = new File("C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\source");
       sshSync(source, sshLoader);
    }

    public static void localSync(File source, File target) {
        Sync sync = new LocalSync();
        try {
            sync.synchronize(source, target, true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void sshSync(File source, SSHLoader sshLoader) {
        LocalSync localSync = new LocalSync();
        try {
            sshLoader.download();
            localSync.synchronize(source, new File(sshLoader.getLoadDirectory()), true);
            sshLoader.upload();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
