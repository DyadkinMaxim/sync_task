package local;

import fileManagement.FileManager;
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
    public synchronized void synchronize(File source, TargetFile target, FileManager fm) throws IOException {
        System.out.println("Sync started");
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
                    fm.delete(target.getChild(fileName));
                }
            }
            System.out.println("All files not present is source are deleted");
            //copy each file from source
            for (String fileName : sources) {
                File sourceFile = new File(source, fileName);
                TargetFile targetFile = target.getChild(fileName);
                System.out.println("Sync recursively called for targetfile" + targetFile);
                synchronize(sourceFile, targetFile, fm);
            }
        } else {
            if (target.exists() && target.isDirectory()) {
                fm.delete(target);
            }
            System.out.println("Entered copy section for " + source.getName());
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
        System.out.println("Sync successfully finished for "+ target.getCanonicalPath());
    }

    @Override
    public void setPause(boolean isPaused) {
        globalPause = isPaused;
    }
}