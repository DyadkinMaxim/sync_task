package fileManagement;

import java.util.Map;
import net.schmizz.sshj.xfer.FileSystemFile;

public class SSHFileFactory extends FileFactory {
    @Override
    protected IFile createFile(Map<String, String> parameters) {
        String host;
        String port;
        String username;
        String privateKeyPath;
        String systemFilePath;
        if (parameters.containsKey("host")) {
            host = parameters.get("host");
        } else {
            throw new IllegalArgumentException("No host found");
        }
        if (parameters.containsKey("port")) {
            port = parameters.get("port");
        } else {
            throw new IllegalArgumentException("No port found");
        }
        if (parameters.containsKey("username")) {
            username = parameters.get("username");
        } else {
            throw new IllegalArgumentException("No username found");
        }
        if (parameters.containsKey("privateKeyPath")) {
            privateKeyPath = parameters.get("privateKeyPath");
        } else {
            throw new IllegalArgumentException("No privateKeyPath found");
        }
        if (parameters.containsKey("systemFilePath")) {
            systemFilePath = parameters.get("systemFilePath");
        } else {
            throw new IllegalArgumentException("No system file path found");
        }
        return new SSHFile(host, port, username, privateKeyPath, new FileSystemFile(systemFilePath));
    }
}