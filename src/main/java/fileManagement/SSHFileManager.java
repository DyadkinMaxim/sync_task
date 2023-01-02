package fileManagement;

import java.io.File;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

@AllArgsConstructor
@Getter
@Setter
public class SSHFileManager implements FileManager {

    private String loadDirectory;
    private String targetPath;
    private String sshHost;
    private String sftpPort;
    private String sshUser;
    private String sshPrivateKey;

    @Override
    public void copyFile(File source, TargetFile targetFile) throws IOException {
        System.out.println("Started copy for "+ source.getPath());
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
                var initialTarget = targetFile.toPath().toString();
                var validatedTarget = initialTarget.replaceAll("\\\\", "/");
                client.newSCPFileTransfer().upload(source.getPath(), validatedTarget);
                System.out.println("Uploaded file:" + source.getName());
            } finally {
                session.close();
            }
        } finally {
            client.disconnect();
        }
    }

    @Override
    public void delete(TargetFile file) throws IOException {
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
                delete(file, sftp);
            } finally {
                sftp.close();
                session.close();
            }
        } finally {
            client.disconnect();
        }
    }

    private void delete(TargetFile file, SFTPClient sftp) throws IOException {
        var initialPath = file.toPath().toString();
        var validatedPath = initialPath.replaceAll("\\\\", "/");
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                sftp.rmdir(validatedPath);
                System.out.println("Deleted directory : " + file.getCanonicalPath());
            } else {
                var files = file.list();
                for (var temp : files) {
                    delete(file.getChild(temp), sftp);
                }
                if (file.list().length == 0) {
                    delete(file);
                }
            }
        } else {
            sftp.rm(validatedPath);
            System.out.println("Deleted file  : " + file.getCanonicalPath());
        }
    }
}
