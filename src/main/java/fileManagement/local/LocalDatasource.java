package fileManagement.local;

import datasource.Datasource;
import datasource.Param;
import fileManagement.IFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LocalDatasource implements Datasource {

    private final String datasourceName = "LOCAL";
    private String filePath;

    @Override
    public List<Param> getConnectionSettings() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("filePath"));
        return params;
    }

    @Override
    public synchronized void connect(List<Param> params) throws IOException {
        filePath = Param.getParam(params, "filePath").getValue();
        if (!Files.exists(Path.of(filePath))) {
            throw new IOException("No file found by path: " + filePath);
        }
    }

    @Override
    public void disconnect() {
    }

    @Override
    public String getName() {
        return datasourceName;
    }

    @Override
    public IFile getRoot() {
        return new LocalFile(new File(filePath), null);
    }
}
