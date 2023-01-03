package fileManagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LocalFile implements IFile {

    private File file;

    @Override
    public void build() {
        System.out.println("Build LocalFile");
    }

    @Override
    public File getSystemFile() {
        return file;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public boolean mkdirs() {
        return file.mkdirs();
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public String getCanonicalPath() {
        String  path = null;
        try {
            path = file.getCanonicalPath();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return path;
    }

    @Override
    public String[] list() {
        return file.list();
    }

    @Override
    public long length() {
        return file.length();
    }

    @Override
    public long lastModified() {
        return file.lastModified();
    }

    @Override
    public Path toPath() {
        return file.toPath();
    }

    @Override
    public IFile getChild(String child){
        return new LocalFile(new File(file, child));
    }

    @Override
    public void copyFile(IFile source) throws IOException {
        Files.copy(source.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    @Override
    public void delete() {
        try {
            Files.walk(file.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
