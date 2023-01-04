package fileManagement.ssh;

import core.ConnectionPool;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

public class SSHClientPool implements ConnectionPool {
    private String sshHost;
    private String sshPort;
    private String sshUser;
    private String sshPrivateKey;
    private List<SSHClient> pool;
    private List<SSHClient> usedSSHClients = new ArrayList<>();
    private static int INITIAL_POOL_SIZE = 1;
    private static int MAX_POOL_SIZE = 2;

    public static SSHClientPool create(
            String sshHost, String sshPort, String sshUser, String sshPrivateKey) {
        List<SSHClient> pool = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createSSHClient(sshHost, sshPort, sshUser, sshPrivateKey));
        }
        return new SSHClientPool(sshHost, sshPort, sshUser, sshPrivateKey, pool);
    }

    public SSHClientPool(String sshHost,
                         String sshPort,
                         String sshUser,
                         String sshPrivateKey,
                         List<SSHClient> pool
    ) {
        this.sshHost = sshHost;
        this.sshPort = sshPort;
        this.sshUser = sshUser;
        this.sshPrivateKey = sshPrivateKey;
        this.pool = pool;
    }

    @Override
    public SSHClient getConnection() {
        if (pool.isEmpty()) {
            if (usedSSHClients.size() < MAX_POOL_SIZE) {
                pool.add(createSSHClient(sshHost, sshPort, sshUser, sshPrivateKey));
            } else {
                System.out.println("Maximum SSH pool size reached, no available connections!");
                try{
                    Thread.sleep(100);
                } catch (InterruptedException ex){
                    System.out.println(ex.getMessage());
                }
                getConnection();
            }
        }
        SSHClient SSHClient = pool
                .remove(pool.size() - 1);
        usedSSHClients.add(SSHClient);
        return SSHClient;
    }

    public boolean releaseConnection(SSHClient sshClient) {
        pool.add(sshClient);
        return usedSSHClients.remove(sshClient);
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

    public int getSize() {
        return pool.size() + usedSSHClients.size();
    }

    public void shutdown() throws Exception {
        usedSSHClients.forEach(this::releaseConnection);
        for (SSHClient s : pool) {
            s.close();
        }
        pool.clear();
    }
}
