package fileManagement;

import fileManagement.FileManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SSHFileManager implements FileManager {

    private final String loadDirectory = "C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\remote\\";
    private final String sftpPath = "/test";
    private final String sshHost = "34.71.206.155";
    private final String sftpPort = "22";
    private final String sshUser = "dyadkinm";
    private final String sshPrivateKey = "C:\\Users\\Dyadkin Maxim\\.ssh\\id_ed25519";

    @Override
    public void copyFile(File source, TargetFile targetFile) throws IOException {

    }

    @Override
    public void delete(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}