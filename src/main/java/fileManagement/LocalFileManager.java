package fileManagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class LocalFileManager implements FileManager {

    @Override
    public void copyFile(File source, TargetFile targetFile) throws IOException {
        Files.copy(source.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void delete(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
