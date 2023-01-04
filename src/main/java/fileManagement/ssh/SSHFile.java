package fileManagement.ssh;

import fileManagement.IFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.FileMode;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

@AllArgsConstructor
@Getter
@Setter
public class SSHFile implements IFile {

    private SSHClientPool sshClientPool;
    private SFTPClientPool sftpClientPool;
    private FileSystemFile systemFile;

    public File getSystemFile() {
        return systemFile.getFile();
    }

    @Override
    public boolean exists() {
        return getAttrs() != null;
    }

    @Override
    public boolean mkdirs() {
        SFTPClient sftp = null;
        try {
            sftp = sftpClientPool.getConnection();
            var path = toPath().toString();
            var pathForwardSlashes = path.replaceAll("\\\\", "/");
            sftp.mkdirs(pathForwardSlashes);
            return true;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            sftpClientPool.releaseConnection(sftp);
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
        SFTPClient sftp = null;
        try {
            sftp = sftpClientPool.getConnection();
            canonicalPath = sftp.canonicalize(systemFile.getFile().getPath());
            ;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            sftpClientPool.releaseConnection(sftp);
        }
        return canonicalPath;
    }

    @Override
    public String[] list() {
        var list = new ArrayList<>();
        SFTPClient sftp = null;
        try {
            System.out.println("Listing started for file " + systemFile.getName());
            sftp = sftpClientPool.getConnection();
            var path = toPath().toString();
            var pathForwardSlashes = path.replaceAll("\\\\", "/");
            for (var file : sftp.ls(pathForwardSlashes)) {
                list.add(file.getName());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            sftpClientPool.releaseConnection(sftp);
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public long length() {
        return getAttrs().getSize();
    }

    @Override
    public long lastModified() {
        return getAttrs().getMtime();
    }

    @Override
    public Path toPath() {
        return Path.of(getCanonicalPath());
    }

    @Override
    public IFile getChild(String child) {
        return new SSHFile(sshClientPool, sftpClientPool,
                new FileSystemFile(new File(systemFile.getFile(), child)));
    }

    private FileAttributes getAttrs() {
        FileAttributes attrs = null;
        SFTPClient sftp = null;
        try {
            sftp = sftpClientPool.getConnection();
            var path = toPath().toString();
            var pathForwardSlashes = path.replaceAll("\\\\", "/");
            attrs = sftp.statExistence(pathForwardSlashes);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            sftpClientPool.releaseConnection(sftp);
        }
        return attrs;
    }

    @Override
    public void copyFile(IFile source) throws IOException {
        System.out.println("Started copy for " + source.getCanonicalPath());
        SSHClient sshClient = null;
        try {
            sshClient = sshClientPool.getConnection();
            sshClient.useCompression();
            var initialTarget = getCanonicalPath();
            var validatedTarget = initialTarget.replaceAll("\\\\", "/");
            sshClient.newSCPFileTransfer().upload(source.getCanonicalPath(), validatedTarget);
            System.out.println("Uploaded file:" + source.getCanonicalPath());
        } finally {
            sshClientPool.releaseConnection(sshClient);
        }
    }

    @Override
    public void delete() {
        SFTPClient sftp = null;
        try {
            sftp = sftpClientPool.getConnection();
            delete(this, sftp);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            sftpClientPool.releaseConnection(sftp);
        }
    }


    private void delete(IFile fileForDelete, SFTPClient sftp) throws IOException {
        var initialPath = fileForDelete.getCanonicalPath();
        var validatedPath = initialPath.replaceAll("\\\\", "/");
        if (fileForDelete.isDirectory()) {
            if (fileForDelete.list().length == 0) {
                sftp.rmdir(validatedPath);
                System.out.println("Deleted directory : " + fileForDelete.getCanonicalPath());
            } else {
                var files = fileForDelete.list();
                for (var temp : files) {
                    delete(fileForDelete.getChild(temp), sftp);
                }
                if (fileForDelete.list().length == 0) {
                    delete(fileForDelete, sftp);
                }
            }
        } else {
            sftp.rm(validatedPath);
            System.out.println("Deleted fileForDelete  : " + fileForDelete.getCanonicalPath());
        }
    }
}