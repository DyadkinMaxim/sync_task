package datasource.ssh;

import core.Progress;
import datasource.base.FileUtils;
import datasource.base.IFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.FileMode;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

@Slf4j
@AllArgsConstructor
@Getter
@Setter
public class SSHFile implements IFile {

    private SSHClient sshClient;
    private SFTPClient sftpClient;
    private FileSystemFile systemFile;
    private Progress progress;

    public File getSystemFile() {
        return systemFile.getFile();
    }

    @Override
    public boolean exists() {
        return getAttrs() != null;
    }

    @Override
    public boolean mkdirs() {
        try {
            sftpClient.mkdirs(forwardSlashPath(this));
            log.debug("Folder created: " + forwardSlashPath(this));
            return true;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean isDirectory() {
        return getAttrs().getMode().getType().equals(FileMode.Type.DIRECTORY);
    }

    @Override
    public String getCanonicalPath() {
        String canonicalPath = null;
        try {
            canonicalPath = sftpClient.canonicalize(systemFile.getFile().getPath());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        log.debug(String.format("Canonical path for %s is", systemFile.getName(), canonicalPath));
        return canonicalPath;
    }

    private static String forwardSlashPath(IFile file) {
        return file.getCanonicalPath().replaceAll("\\\\", "/");
    }

    @Override
    public long countAll() {
        return FileUtils.countAll(this);
    }

    @Override
    public String[] list() {
        var list = new ArrayList<>();
        try {
            log.debug("Listing started for file " + systemFile.getName());
            for (var file : sftpClient.ls(forwardSlashPath(this))) {
                list.add(file.getName());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public long length() {
        return getAttrs().getSize();
    }

    @Override
    public long lastModified() {
        return getAttrs().getMtime() * 1000;
    }

    @Override
    public Path toPath() {
        return Path.of(getCanonicalPath());
    }

    @Override
    public IFile getChild(String child) {
        return new SSHFile(sshClient, sftpClient,
                new FileSystemFile(new File(systemFile.getFile(), child)), progress);
    }

    private FileAttributes getAttrs() {
        FileAttributes attrs = null;
        try {
            attrs = sftpClient.statExistence(forwardSlashPath(this));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return attrs;
    }

    @Override
    public void copyFile(IFile source) throws IOException {
        sshClient.useCompression();
        sshClient.newSCPFileTransfer().upload(source.getCanonicalPath(), forwardSlashPath(this));
        progress.incrementProgress();
        log.debug("Uploaded file:" + source.getCanonicalPath());
    }

    @Override
    public void setLastModified(long value) {
        //only for source
    }

    @Override
    public void delete() {
        try {
            delete(this, sftpClient);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    private void delete(IFile fileForDelete, SFTPClient sftpClient) throws IOException {
        if (fileForDelete.isDirectory()) {
            if (fileForDelete.list().length == 0) {
                sftpClient.rmdir(forwardSlashPath(fileForDelete));
                progress.incrementProgress();
                log.debug("Deleted directory : " + fileForDelete.getCanonicalPath());
            } else {
                var files = fileForDelete.list();
                for (var temp : files) {
                    delete(fileForDelete.getChild(temp), sftpClient);
                }
                if (fileForDelete.list().length == 0) {
                    delete(fileForDelete, sftpClient);
                }
            }
        } else {
            sftpClient.rm(forwardSlashPath(fileForDelete));
            progress.incrementProgress();
            log.debug("Deleted file  : " + fileForDelete.getCanonicalPath());
        }
    }

    @Override
    public void setProgress(Progress progress) {
        this.progress = progress;
    }
}