package remote;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

@Getter
@Setter
@ToString
public class SSHLoader implements RemoteLoader {

    private final String loadDirectory ;
    private final String sftpPath;
    private final String sftpHost;
    private final String sftpPort;
    private final String sftpUser;
    private final String sftpPassword;

    public SSHLoader(
            String loadDirectory,
            String sftpPath,
            String sftpHost,
            String sftpPort,
            String sftpUser,
            String sftpPassword
    ) {
        this.loadDirectory = loadDirectory;
        this.sftpPath = sftpPath;
        this.sftpHost = sftpHost;
        this.sftpPort = sftpPort;
        this.sftpUser = sftpUser;
        this.sftpPassword = sftpPassword;

    }


    public void execute() throws Exception {
        final SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.loadKnownHosts();
        sshClient.connect(sftpHost);
        System.out.println("Connected");
        try {
            sshClient.authPassword(sftpUser, sftpPassword);
            sshClient.useCompression();
            sshClient.newSCPFileTransfer().upload(loadDirectory, sftpPath);
        } finally {
            sshClient.disconnect();
            System.out.println("Disconnected");
        }
    }

    @Override
    public void download() throws Exception {
        final SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.loadKnownHosts();
        sshClient.connect(sftpHost);
        System.out.println("Connected");
        try {
            sshClient.authPassword(sftpUser, sftpPassword);
            final SFTPClient sftp = sshClient.newSFTPClient();
            try {
                sftp.get("test/", new FileSystemFile(loadDirectory));
            } finally {
                sftp.close();
            }
        } finally {
            sshClient.disconnect();
            System.out.println("Disconnected");
        }
    }

    @Override
    public void upload() throws Exception {
        final SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.loadKnownHosts();
        sshClient.connect(sftpHost);
        System.out.println("Connected");
        try {
            sshClient.authPassword(sftpUser, sftpPassword);
            sshClient.useCompression();
            sshClient.newSCPFileTransfer().upload(loadDirectory, sftpPath);
        } finally {
            sshClient.disconnect();
            System.out.println("Disconnected");
        }
    }
}