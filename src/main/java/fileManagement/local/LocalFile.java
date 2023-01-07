package fileManagement.local;

import core.Progress;
import fileManagement.IFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class LocalFile implements IFile {

    private File file;
    private Progress progress;

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
        var counter = new Counter();
        getFilesCount(this, counter);
        return counter.getValue();
    }

    private void getFilesCount(IFile file, Counter counter) {
        String[] files = file.list();
        if (files != null) {
            for (var child : files) {
                IFile next = file.getChild(child);
                counter.setValue(counter.getValue()+1);
                if (next.isDirectory()) {
                    getFilesCount(next, counter);
                }
            }
        }
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
    public IFile getChild(String child) {
        return new LocalFile(new File(file, child), progress);
    }

    @Override
    public void copyFile(IFile source) throws IOException {
        Files.copy(source.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        progress.incrementProgress();
    }

    @Override
    public void delete() {
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

    @Getter
    @Setter
    class Counter {
        private long value;
    }
}
