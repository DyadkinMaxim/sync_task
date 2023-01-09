package datasource.ssh;

import datasource.base.Datasource;
import datasource.base.IFile;
import datasource.base.Param;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.xfer.FileSystemFile;

/**
 * Represents operations with SSH datasource
 */
@Slf4j
@Getter
@Setter
public class SSHDatasource implements Datasource {

    private final String DATASOURCE_NAME = "SSH";
    private final String DEFAULT_PORT = "22";
    private SSHClient sshClient;
    private SFTPClient sftpClient;
    private String systemFilePath;
    private volatile boolean isInitialized;

    @Override
    public List<Param> getConnectionSettings() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("host", new JTextField(), "Host: "));
        params.add(new Param("port", new JTextField(), "Port: "));
        params.add(new Param("systemFilePath",  new JTextField(), "Target directoty path: "));
        params.add(new Param("username", new JTextField(), "Username: "));
        params.add(new Param("privateKeyPath",  new JTextField(), "Choose private key: "));
        //todo add JFileChoosers
        return List.copyOf(params);
    }

    @Override
    public synchronized void connect(List<Param> params) throws IOException {
        if (isInitialized) {
            throw new IllegalStateException("SSH connection is already initialized");
        }
        var host = Param.getParam(params, "host").getValue();
        var port = Param.getParam(params, "port") == null ?
                Param.getParam(params, "port").getValue() : DEFAULT_PORT;
        var username = Param.getParam(params, "username").getValue();
        var privateKeyPath = Param.getParam(params, "privateKeyPath").getValue();
        systemFilePath = Param.getParam(params, "systemFilePath").getValue();
        sshClient = createSSHClient(host, port, username, privateKeyPath);
        sftpClient = sshClient.newSFTPClient();
        log.debug(String.format("SSH connection established for" +
                        "host: %s, port: %s, user: %s, privateKey: %s, filePath: %s",
                host, port, username, privateKeyPath, systemFilePath));
        isInitialized = true;
    }

    @Override
    public void disconnect() throws IOException {
        ensureInitializedOrThrow();
        sshClient.close();
        sftpClient.close();
        isInitialized = false;
        log.debug("SSH connection disabled");
    }

    @Override
    public String getName() {
        return DATASOURCE_NAME;
    }

    @Override
    public IFile getRoot() {
        ensureInitializedOrThrow();
        return new SSHFile(sshClient, sftpClient, new FileSystemFile(systemFilePath), null);
    }

    protected final void ensureInitializedOrThrow() throws IllegalStateException {
        if (!isInitialized) {
            throw new IllegalStateException();
        }
    }

    private static SSHClient createSSHClient(
            String sshHost, String sshPort, String sshUser, String sshPrivateKey) {
        SSHClient client;
        try {
            client = new SSHClient();
            client.addHostKeyVerifier(new PromiscuousVerifier());
            File privateKey = new File(sshPrivateKey);
            KeyProvider keys = client.loadKeys(privateKey.getPath());
            client.connect(sshHost);
            client.authPublickey(sshUser, keys);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format(
                            "ERROR: Unrecoverable error when trying to connect to " +
                                    "host: %s, port: %s, user: %s, keyPath: %s.",
                            sshHost, sshPort, sshUser, sshPrivateKey));
        }
        log.debug("SSH client is created successfully");
        return client;
    }

    public Datasource copy() {
        var copy = new SSHDatasource();
        copy.setSshClient(this.getSshClient());
        copy.setSftpClient(this.getSftpClient());
        copy.setSystemFilePath(this.getSystemFilePath());
        copy.setInitialized(this.isInitialized);
        return copy;
    }
}
