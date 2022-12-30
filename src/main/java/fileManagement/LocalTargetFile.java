package fileManagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LocalTargetFile implements TargetFile {

    private File target;

    @Override
    public File getFile() {
        return target;
    }

    @Override
    public boolean exists() {
        return target.exists();
    }

    @Override
    public boolean mkdirs() {
        return target.mkdirs();
    }

    @Override
    public boolean isDirectory() {
        return target.isDirectory();
    }

    @Override
    public String getCanonicalPath() throws IOException {
        return target.getCanonicalPath();
    }

    @Override
    public String[] list() {
        return target.list();
    }

    @Override
    public long length() {
        return target.length();
    }

    @Override
    public long lastModified() {
        return target.lastModified();
    }

    @Override
    public Path toPath() {
        return target.toPath();
    }

    @Override
    public TargetFile getChild(String child){
        return new LocalTargetFile(new File(target, child));
    }
}
