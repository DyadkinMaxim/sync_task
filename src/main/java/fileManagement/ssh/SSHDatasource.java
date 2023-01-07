package fileManagement.ssh;

import datasource.Datasource;
import datasource.Param;
import fileManagement.IFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.xfer.FileSystemFile;

public class SSHDatasource implements Datasource {

    private final String datasourceName = "SSH";
    private SSHClient sshClient;
    private SFTPClient sftpClient;
    private String systemFilePath;
    private volatile boolean isInitialized;
    private final String DEFAULT_PORT = "22";

    @Override
    public List<Param> getConnectionSettings() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("host"));
        params.add(new Param("port"));
        params.add(new Param("systemFilePath"));
        params.add(new Param("username"));
        params.add(new Param("privateKeyPath"));
        return params;
    }

    @Override
    public synchronized void connect(List<Param> params) throws IOException {
        if(isInitialized) {
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
        isInitialized = true;
    }

    @Override
    public void disconnect() throws IOException {
        ensureInitializedOrThrow();
        sshClient.close();
        sftpClient.close();
        isInitialized = false;
    }

    @Override
    public String getName() {
       return datasourceName;
    }

    @Override
    public IFile getRoot() {
        ensureInitializedOrThrow();
        return new SSHFile(sshClient, sftpClient, new FileSystemFile(systemFilePath), null);
    }

    protected final void ensureInitializedOrThrow() throws IllegalStateException{
        if( !isInitialized ) {
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
        return client;
    }
}
