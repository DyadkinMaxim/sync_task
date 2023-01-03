package fileManagement;

import java.io.File;
import java.io.IOException;
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
public class SSHFile implements IFile {

    private String sshHost;
    private String sftpPort;
    private String sshUser;
    private String sshPrivateKey;
    private FileSystemFile systemFile;

    @Override
    public void build() {
        System.out.println("Build Motorcycle");
    }

    public File getSystemFile() {
        return systemFile.getFile();
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
                    canonicalPath = sftp.canonicalize(systemFile.getFile().getPath());
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
                    System.out.println("Listing started for file " + systemFile.getName());
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
    public IFile getChild(String child) {
        return new SSHFile(sshHost, sftpPort, sshUser, sshPrivateKey,
                new FileSystemFile(new File(systemFile.getFile(), child)));
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
            System.out.println("Attributes found for " + systemFile.getName());
            return attrs;
        }
    }

    @Override
    public void copyFile(IFile source) throws IOException {
        System.out.println("Started copy for "+ source.getCanonicalPath());
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        String username = sshUser;
        File privateKey = new File(sshPrivateKey);
        KeyProvider keys = client.loadKeys(privateKey.getPath());
        client.connect(sshHost);
        try {
            client.authPublickey(username, keys);
            final Session session = client.startSession();
            try {
                client.useCompression();
                var initialTarget = getCanonicalPath();
                var validatedTarget = initialTarget.replaceAll("\\\\", "/");
                client.newSCPFileTransfer().upload(source.getCanonicalPath(), validatedTarget);
                System.out.println("Uploaded file:" + source.getCanonicalPath());
            } finally {
                session.close();
            }
        } finally {
            client.disconnect();
        }
    }

    @Override
    public void delete() throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        String username = sshUser;
        File privateKey = new File(sshPrivateKey);
        KeyProvider keys = client.loadKeys(privateKey.getPath());
        client.connect(sshHost);
        try {
            client.authPublickey(username, keys);
            final Session session = client.startSession();
            final SFTPClient sftp = client.newSFTPClient();
            try {
                delete(this, sftp);
            } finally {
                sftp.close();
                session.close();
            }
        } finally {
            client.disconnect();
        }
    }

    private void delete(IFile fileForDelete, SFTPClient sftp) throws IOException {
        var initialPath = fileForDelete.getCanonicalPath();
        var validatedPath = initialPath.replaceAll("\\\\", "/");
        if (fileForDelete.isDirectory()) {
            if (fileForDelete.list().length == 0) {
                sftp.rmdir(validatedPath);
                System.out.println("Deleted directory : " + fileForDelete.getCanonicalPath());
            } else {
                var files = fileForDelete.list();
                for (var temp : files) {
                    delete(fileForDelete.getChild(temp), sftp);
                }
                if (fileForDelete.list().length == 0) {
                    delete(fileForDelete, sftp);
                }
            }
        } else {
            sftp.rm(validatedPath);
            System.out.println("Deleted fileForDelete  : " + fileForDelete.getCanonicalPath());
        }
    }
}