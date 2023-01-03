package fileManagement;

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
    protected abstract IFile createFile(Map<String, String> parameters);
}