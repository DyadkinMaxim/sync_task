package fileManagement;

import java.io.Console;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.FileMode;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.xfer.FileSystemFile;

@AllArgsConstructor
@Getter
@Setter
public class SSHTargetFile implements TargetFile {

    private String loadDirectory;
    private String targetPath;
    private String sshHost;
    private String sftpPort;
    private String sshUser;
    private String sshPrivateKey;
    private FileSystemFile file;

    @Override
    public File getFile() {
        return file.getFile();
    }

    @Override
    public boolean exists() {
        return getAttrs() != null;
    }

    @Override
    public boolean mkdirs() {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        String username = sshUser;
        File privateKey = new File(sshPrivateKey);
        try {
            KeyProvider keys = client.loadKeys(privateKey.getPath());
            client.connect(sshHost);
            try {
                client.authPublickey(username, keys);
                final Session session = client.startSession();
                final SFTPClient sftp = client.newSFTPClient();
                try {
                    var path = toPath().toString();
                    var pathForwardSlashes = path.replaceAll("\\\\", "/");
                    sftp.mkdirs(pathForwardSlashes);
                    return true;
                } finally {
                    sftp.close();
                    session.close();
                }
            } finally {
                client.disconnect();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isDirectory() {
        return getAttrs().getMode().getType().equals(FileMode.Type.DIRECTORY);
    }

    @Override
    public String getCanonicalPath() {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        String username = sshUser;
        File privateKey = new File(sshPrivateKey);
        String canonicalPath = null;
        try {
            KeyProvider keys = client.loadKeys(privateKey.getPath());
            client.connect(sshHost);
            try {
                client.authPublickey(username, keys);
                final Session session = client.startSession();
                final SFTPClient sftp = client.newSFTPClient();
                try {
                    canonicalPath = sftp.canonicalize(file.getFile().getPath());
                } finally {
                    sftp.close();
                    session.close();
                }
            } finally {
                client.disconnect();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return canonicalPath;
    }

    @Override
    public String[] list() {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        String username = sshUser;
        File privateKey = new File(sshPrivateKey);
        var list = new ArrayList<>();
        try {
            KeyProvider keys = client.loadKeys(privateKey.getPath());
            client.connect(sshHost);
            try {
                client.authPublickey(username, keys);
                final Session session = client.startSession();
                final SFTPClient sftp = client.newSFTPClient();
                try {
                    System.out.println("Listing started for file " + file.getName());
                    var path = toPath().toString();
                    var pathForwardSlashes = path.replaceAll("\\\\", "/");
                    for (var file : sftp.ls(pathForwardSlashes)) {
                        list.add(file.getName());
                    }
                } finally {
                    sftp.close();
                    session.close();
                }
            } finally {
                client.disconnect();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public long length() {
        return getAttrs().getSize();
    }

    @Override
    public long lastModified() {
        return getAttrs().getMtime();
    }

    @Override
    public Path toPath() {
        return Path.of(getCanonicalPath());
    }

    @Override
    public TargetFile getChild(String child) {
        return new SSHTargetFile(loadDirectory,
        targetPath, sshHost, sftpPort, sshUser, sshPrivateKey,
                new FileSystemFile(new File(file.getFile(), child)));
    }

    private FileAttributes getAttrs() {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        String username = sshUser;
        File privateKey = new File(sshPrivateKey);
        FileAttributes attrs = null;
        try {
            KeyProvider keys = client.loadKeys(privateKey.getPath());
            client.connect(sshHost);
            try {
                client.authPublickey(username, keys);
                final Session session = client.startSession();
                final SFTPClient sftp = client.newSFTPClient();
                try {
                    var path = toPath().toString();
                    var pathForwardSlashes = path.replaceAll("\\\\", "/");
                    attrs = sftp.statExistence(pathForwardSlashes);
                } finally {
                    sftp.close();
                    session.close();
                }
            } finally {
                client.disconnect();
            }
        } finally {
            System.out.println("Attributes found for " + file.getName());
            return attrs;
        }
    }
}