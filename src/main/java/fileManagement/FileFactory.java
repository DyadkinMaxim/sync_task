package fileManagement;

import fileManagement.local.LocalFileFactory;
import fileManagement.ssh.SSHFileFactory;
import java.util.Map;

public abstract class FileFactory {
    public static IFile create(FileType type, Map<String, String> parameters) {
        IFile file = null;
        switch(type) {
            case LOCAL:
                var localFactory = new LocalFileFactory();
                file = localFactory.createFile(parameters);
                break;
            case SSH:
                var sshFactory = new SSHFileFactory();
                file = sshFactory.createFile(parameters);
                break;
        }
        return file;
    }
    public abstract IFile createFile(Map<String, String> parameters);
}