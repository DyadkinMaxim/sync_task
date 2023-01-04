package fileManagement.local;

import fileManagement.FileFactory;
import fileManagement.IFile;
import java.io.File;
import java.util.Map;

public class LocalFileFactory extends FileFactory {

    @Override
    public IFile createFile(Map<String, String> parameters) {
        String localPath;
        if(parameters.containsKey("filePath")){
            localPath = parameters.get("filePath");
        } else {
            throw new IllegalArgumentException("No filePath found");
        }
        return new LocalFile(new File(localPath));
    }
}