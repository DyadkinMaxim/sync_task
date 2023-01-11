package datasource.base;

import java.io.IOException;
import java.util.List;

/**
 * General behavior of any datasource
 */
public interface Datasource {
    List<Param> getConnectionSettings();


    /**
     * Initializes connection for remote datasources(SSH, SFTP clients etc)
     * Validates local directory path
     */
    void connect(List<Param> params) throws IOException;

    void disconnect() throws IOException;

    String getName();

    IFile getRoot();

    Datasource copy();
}
