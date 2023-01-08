package datasource.base;

import java.io.IOException;
import java.util.List;

/**
 * General behavior of any datasource
 */
public interface Datasource {
    List<Param> getConnectionSettings();

    void connect(List<Param> params) throws IOException;

    void disconnect() throws IOException;

    String getName();

    IFile getRoot();
}
