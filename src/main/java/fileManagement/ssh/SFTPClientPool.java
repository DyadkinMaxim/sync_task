package fileManagement.ssh;

import core.ConnectionPool;
import java.util.ArrayList;
import java.util.List;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;


public class SFTPClientPool implements ConnectionPool {

    private SSHClientPool clientPool;
    private List<SFTPClient> sftpClientPool;
    private List<SFTPClient> usedSFTPClients = new ArrayList<>();
    private static int INITIAL_POOL_SIZE = 1;
    private static int MAX_POOL_SIZE = 2;
    private static int MAX_TIMEOUT = 60000;

    public static SFTPClientPool create(
            SSHClientPool clientPool) {
        List<SFTPClient> SFTPClientPool = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            SFTPClientPool.add(createSFTPClient(clientPool));
        }
        return new SFTPClientPool(clientPool, SFTPClientPool);
    }

    public SFTPClientPool(SSHClientPool clientPool,
                          List<SFTPClient> sftpClientPool
    ) {
        this.clientPool = clientPool;
        this.sftpClientPool = sftpClientPool;
    }

    @Override
    public SFTPClient getConnection() {
        if ( sftpClientPool.isEmpty()) {
            if (usedSFTPClients.size() < MAX_POOL_SIZE) {
                sftpClientPool.add(createSFTPClient(clientPool));
            } else {
                System.out.println("Maximum SFTP pool size reached, no available connections!");
                try{
                    Thread.sleep(100);
                } catch (InterruptedException ex){
                    System.out.println(ex.getMessage());
                }
                getConnection();
            }
        }
        SFTPClient SFTPClient = sftpClientPool
                .remove(sftpClientPool.size() - 1);
        usedSFTPClients.add(SFTPClient);
        return SFTPClient;
    }


    public boolean releaseConnection(SFTPClient SFTPClient) {
        sftpClientPool.add(SFTPClient);
        //clientPool.releaseConnection(sshClient);
        return usedSFTPClients.remove(SFTPClient);
    }

    private static SFTPClient createSFTPClient(SSHClientPool clientPool) {
        SFTPClient SFTPClient = null;
        try {
            if(clientPool.getSize() > 0) {
                SFTPClient = clientPool.getConnection().newSFTPClient();
            } else {
                Thread.sleep(100);
                createSFTPClient(clientPool);
            }
        } catch (Exception e) {
            throw new RuntimeException("ERROR: Unrecoverable error when trying to get SFTPClient from sshClientPool");
        }
        return SFTPClient;
    }

    public int getSize() {
        return sftpClientPool.size() + usedSFTPClients.size();
    }

    public void shutdown() throws Exception {
        usedSFTPClients.forEach(this::releaseConnection);
        for (SFTPClient s : sftpClientPool) {
            s.close();
        }
        sftpClientPool.clear();
    }
}
