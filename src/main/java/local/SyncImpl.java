package local;

import fileManagement.FileManager;
import fileManagement.LocalTargetFile;
import fileManagement.TargetFile;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SyncImpl implements Sync {

    private static final int FAT_PRECISION = 2000;
    private static boolean globalPause = false;

    @Override
    public void synchronize(File source, TargetFile target, FileManager fm) throws IOException {
        if (source.isDirectory()) {
            if (!target.exists()) {
                if (!target.mkdirs()) {
                    throw new IOException("Could not create path " + target);
                }
            } else if (!target.isDirectory()) {
                throw new IOException(
                        "Source and Destination not of the same type:"
                                + source.getCanonicalPath() + " , " + target.getCanonicalPath()
                );
            }
            String[] sources = source.list();
            Set<String> srcNames = new HashSet<>(Arrays.asList(sources));
            String[] targets = target.list();

            //delete files not present in source
            for (String fileName : targets) {
                if (!srcNames.contains(fileName)) {
                    fm.delete(new File( target.getFile(), fileName ));
                }
            }
            //copy each file from source
            for (String fileName : sources) {
                File sourceFile = new File(source, fileName);
                TargetFile targetFile = target.getChild(fileName);
                synchronize(sourceFile, targetFile, fm);
            }
        } else {
            if (target.exists() && target.isDirectory()) {
                fm.delete(target.getFile());
            }
            if (target.exists()) {
                long sts = source.lastModified() / FAT_PRECISION;
                long dts = target.lastModified() / FAT_PRECISION;
                //do not copy if smart and same timestamp and same length
                if (sts == 0 || sts != dts || source.length() != target.length()) {
                    fm.copyFile(source, target);
                }
            } else {
                fm.copyFile(source, target);
            }
        }
    }

    @Override
    public void setPause(boolean isPaused) {
        globalPause = isPaused;
    }
}