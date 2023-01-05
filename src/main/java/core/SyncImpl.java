package core;

import fileManagement.IFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SyncImpl implements Sync {

    private static final int FAT_PRECISION = 2000;
    private static boolean globalPause = false;

    @Override
    public synchronized void synchronize(IFile source, IFile target) throws IOException {
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
                    target.getChild(fileName).delete();
                }
            }
            log.debug("All files not present is source are deleted");
            //copy each file from source
            for (String fileName : sources) {
                IFile sourceFile = source.getChild(fileName);
                IFile targetFile = target.getChild(fileName);
                log.info("Sync recursively called for target file" + targetFile);
                synchronize(sourceFile, targetFile);
            }
        } else {
            if (target.exists() && target.isDirectory()) {
                target.delete();
            }
            if (target.exists()) {
                long sts = source.lastModified() / 1000;
                long dts = target.lastModified();
                //do not copy if same timestamp and same length
                if (sts == 0 || sts != dts || source.length() != target.length()) {
                    target.copyFile(source);
                }
            } else {
                target.copyFile(source);
            }
        }
        log.info(String.format("Sync successfully finished for source: %s, target: %s",
                source.getCanonicalPath(), target.getCanonicalPath()));
    }

    @Override
    public void setPause(boolean isPaused) {
        globalPause = isPaused;
    }
}