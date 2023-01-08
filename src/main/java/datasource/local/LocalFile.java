package datasource.local;

import core.Progress;
import core.FileUtils;
import datasource.base.IFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class LocalFile implements IFile {

    private File file;
    private Progress progress;

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
        String path = null;
        try {
            path = file.getCanonicalPath();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return path;
    }

    @Override
    public long countAll() {
        return FileUtils.countAll(this);
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
    public long getLastModified() {
        return file.lastModified();
    }

    public void setLastModified(long value) {
        file.setLastModified(value);
    }

    @Override
    public Path toPath() {
        return file.toPath();
    }

    @Override
    public IFile getChild(String child) {
        return new LocalFile(new File(file, child), progress);
    }

    @Override
    public void copyFile(IFile source) throws IOException {
        Files.copy(source.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        log.debug(String.format("Local copy %s to %s", source.getCanonicalPath(), getCanonicalPath()));
        source.setLastModified(getLastModified());
        progress.incrementProgress();
    }

    @Override
    public void delete() {
        log.debug("Deleting file:" + getCanonicalPath());
        try {
            Files.walk(file.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        file.delete();
                        progress.incrementProgress();
                    });
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void setProgress(Progress progress) {
        this.progress = progress;
    }
}
