package core;

import java.io.Closeable;
import net.schmizz.sshj.SSHClient;

public interface ConnectionPool {
    Closeable getConnection();
}
