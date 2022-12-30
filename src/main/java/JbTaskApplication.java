import fileManagement.LocalFileManager;
import fileManagement.LocalTargetFile;
import fileManagement.TargetFile;
import java.io.File;
import local.LocalScheduler;

public class JbTaskApplication {

    private static final String loadDirectory = "C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\remote\\";
    private static final String sftpPath = "/test";
    private static final String sftpHost = "test759.files.com";
    private static final String sftpPort = "22";
    private static final String sftpUser = "dyadkinm@gmail.com";
    private static final String sftpPassword = "9kJ@sd#R3^";

    public static void main(String[] args) {
        File source = new File("C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\source");
        TargetFile target = new LocalTargetFile(new File("C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\target"));
        LocalFileManager lfm = new LocalFileManager();
        var localScheduler = new LocalScheduler();
        localScheduler.localSchedule(source, target, lfm);
    }
}
